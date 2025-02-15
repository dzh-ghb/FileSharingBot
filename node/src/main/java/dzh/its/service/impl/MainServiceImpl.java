package dzh.its.service.impl;

import dzh.its.dao.AppUserDAO;
import dzh.its.dao.RawDataDAO;
import dzh.its.entity.AppDocument;
import dzh.its.entity.AppPhoto;
import dzh.its.entity.AppUser;
import dzh.its.entity.RawData;
import dzh.its.enums.UserState;
import dzh.its.exceptions.UploadFileException;
import dzh.its.service.AppUserService;
import dzh.its.service.FileService;
import dzh.its.service.MainService;
import dzh.its.service.ProducerService;
import dzh.its.service.enums.LinkType;
import dzh.its.service.enums.ServiceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static dzh.its.enums.UserState.BASIC_STATE;
import static dzh.its.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static dzh.its.service.enums.ServiceCommand.CANCEL;
import static dzh.its.service.enums.ServiceCommand.HELP;
import static dzh.its.service.enums.ServiceCommand.REGISTRATION;
import static dzh.its.service.enums.ServiceCommand.START;

@Log4j
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;

    private final ProducerService producerService;

    private final AppUserDAO appUserDAO;

    private final FileService fileService;

    private final AppUserService appUserService;

    @Transactional
    @Override
    public void processTextMessage(Update update) { //обработка текстовых сообщений
        saveRawData(update); //сохранения апдейта в БД целиком
        AppUser appUser = findOrSaveAppUser(update); //сохранение или поиск юзера в БД
        UserState userState = appUser.getState(); //текущее состояние юзера
        String text = update.getMessage().getText(); //текст сообщения из входящего апдейта
        String output = ""; //переменная-заготовка для ответа

        ServiceCommand serviceCommand = ServiceCommand.fromValue(text);
        if (CANCEL.equals(serviceCommand)) { //входящий текст содержит команду отмены
            output = cancelProcess(appUser); //сброс состояния юзера в базовое
        } else if (BASIC_STATE.equals(userState)) { //текущее состояние юзера - базовое
            output = processServiceCommand(appUser, text); //переход в режим ожидания ввода сервисных команд
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) { //текущее состояние юзера - ожидание ввода email
            output = appUserService.setEmail(appUser, text); //обработка ввода электронной почты
        } else {
            Long userId = appUser.getId();
            String userName = appUser.getUserName();
            log.error(String.format("Неизвестное состояние пользователя %s [user-id = %s]: %s", userName, userId, userState));
            output = "Ошибка\nВведите /cancel и попробуйте снова";
        }

        Long chatId = update.getMessage().getChatId();
        sendAnswer(chatId, output); //метод отправки сообщения
    }

    @Override
    public void processDocMessage(Update update) { //обработка сообщения-документа
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) { //если загрузка контента запрещена
            return;
        }

        try {
            AppDocument doc = fileService.processDoc(update.getMessage()); //передача сообщения из апдейта в FileService
            String link = fileService.generateLink(doc.getId(), LinkType.GET_DOC); //метод генерации ссылки на документ
            String answer = "Документ успешно загружен\nСсылка для скачивания:\n" + link;
            sendAnswer(chatId, answer);
        } catch (UploadFileException e) {
            log.error("Загрузка документа завершилась с ошибкой: " + e);
            String error = "Загрузка документа завершилась с ошибкой\nПовторите попытку позже";
            sendAnswer(chatId, error);
        }
    }

    @Override
    public void processPhotoMessage(Update update) { //обработка сообщения-изображения
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) { //если загрузка контента запрещена
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage()); //передача сообщения из апдейта в FileService
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO); //метод генерации ссылки на фото
            String answer = "Изображение успешно загружено\nСсылка для скачивания:\n" + link;
            sendAnswer(chatId, answer);
        } catch (UploadFileException e) {
            log.error("Загрузка изображения завершилось с ошибкой: " + e);
            String error = "Загрузка изображения завершилось с ошибкой\nПовторите попытку позже";
            sendAnswer(chatId, error);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) { //метод обработки ситуации, когда загрузка контента запрещена
        UserState userState = appUser.getState();

        if (!appUser.getIsActive()) { //проверка подтверждения активации учетной записи (если не активирован)
            String error = "Необходимо пройти процесс активации учетной записи для предоставления возможности загрузки контента";
            sendAnswer(chatId, error);
            return true;
        } else if (!BASIC_STATE.equals(userState)) { //проверка текущего состояние юзера - загружать контент можно только в базовом состоянии (если НЕ в базовом состоянии)
            String error = "Отмените текущую команду с помощью /cancel для отправки файлов";
            sendAnswer(chatId, error);
            return true;
        }
        return false;
    }

    private void sendAnswer(Long chatId, String output) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(output);
            producerService.produceAnswer(sendMessage); //передача сообщения от узла
        } catch (Exception e) {
            log.error("Поступили некорректные данные: " + e); //если пришли некорректные данные (например, с ошибкой в JSON)
        }
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE); //установка юзеру базового состояния
        appUserDAO.save(appUser); //сохранение изменений в БД
        return "Команда отменена";
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        ServiceCommand serviceCommand = ServiceCommand.fromValue(cmd);
        if (REGISTRATION.equals(serviceCommand)) { //входящая команда - команда регистрации
            return appUserService.registerUser(appUser); //обработка запроса на регистрацию юзера
        } else if (HELP.equals(serviceCommand)) { //-//- - команда получения справки
            return help(); //возврат списка доступных команд
        } else if (START.equals(serviceCommand)) { //-//- - команда начала работы
            return "Приветствую!\nЧтобы проверить список доступных команд введите /help";
        } else { //несуществующая команда
            return "Неизвестная команда. Чтобы проверить список доступных команд введите /help";
        }
    }

    private String help() { //справка о доступных командах
        return "Список доступных команд:\n"
                + "/registration - регистрация пользователя\n"
                + "/cancel - отмена выполнения текущей команды";
    }

    private AppUser findOrSaveAppUser(Update update) { //поиск юзера в БД
        User telegramUser = update.getMessage().getFrom(); //получение информации о юзере из апдейта
        Optional<AppUser> appUserOpt = appUserDAO.findByTelegramUserId(telegramUser.getId()); //поиск уже существующего юзера
        if (appUserOpt.isEmpty()) { //сохранение нового юзера в БД, поле firstLoginDate генерируется автоматически
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .lastName(telegramUser.getLastName())
                    .firstName(telegramUser.getFirstName())
                    .isActive(false) //дефолтное состояние - юзер не зарегистрирован/не активирован
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser); //сохранение нового юзера в БД и возвращение значения
        }
        return appUserOpt.get(); //возвращение информации о существующем юзере
    }

    private void saveRawData(Update update) { //создание объекта RawData (сохранение данных в БД)
        RawData rawData = RawData.builder() //использование паттерна Builder
                .event(update)
                .build();
        rawDataDAO.save(rawData); /*ВАЖНО: метод автоматически создается Spring через JpaRepository,
        после сохранения возвращает обратно сохраненный объект с уже заполненным первичным ключом и привязкой объекта к сессии Hibernate*/
    }
}