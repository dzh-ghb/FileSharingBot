package dzh.its.service.impl;

import dzh.its.dao.AppUserDAO;
import dzh.its.dao.RawDataDAO;
import dzh.its.entity.AppDocument;
import dzh.its.entity.AppPhoto;
import dzh.its.entity.AppUser;
import dzh.its.entity.RawData;
import dzh.its.entity.enums.UserState;
import dzh.its.exceptions.UploadFileException;
import dzh.its.service.FileService;
import dzh.its.service.MainService;
import dzh.its.service.ProducerService;
import dzh.its.service.enums.ServiceCommand;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static dzh.its.entity.enums.UserState.BASIC_STATE;
import static dzh.its.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static dzh.its.service.enums.ServiceCommand.*;

@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService) {
        this.rawDataDAO = rawDataDAO; //внедрение bean через конструктор
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
    }

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
            //TODO: реализация при добавлении mail-микросервиса
        } else {
            log.error("Неизвестное состояние пользователя: " + userState);
            output = "Ошибка! Введите \"/cancel\" и попробуйте снова";
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
            //TODO: добавить генерацию ссылки для скачивания документа
            String answer = "Документ успешно загружен\nСсылка для скачивания: http://benzotest.ru/get-doc/???";
            sendAnswer(chatId, answer);
        } catch (UploadFileException e) {
            log.error(e);
            String error = "Загрузка файла завершилась с ошибкой\nПовторите попытку позже";
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
            //TODO: добавить генерацию ссылки для скачивания фото
            String answer = "Фото успешно загружено\nСсылка для скачивания: http://benzotest.ru/get-photo/???";
            sendAnswer(chatId, answer);
        } catch (UploadFileException e) {
            log.error(e);
            String error = "Загрузка фото завершилась с ошибкой\nПовторите попытку позже";
            sendAnswer(chatId, error);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) { //метод обработки ситуации, когда загрузка контента запрещена
        UserState userState = appUser.getState();

        if (!appUser.getIsActive()) { //проверка подтверждения активации учетной записи (если не активирован)
            String error = "Необходимо пройти процесс регистрации или активировать свою учетную запись для предоставления возможности загрузки контента";
            sendAnswer(chatId, error);
            return true;
        } else if (!BASIC_STATE.equals(userState)) { //проверка текущего состояние юзера - загружать контент можно только в базовом состоянии (если НЕ в базовом состоянии)
            String error = "Отмените текущую команду с помощью \"/cancel\" для отправки файлов";
            sendAnswer(chatId, error);
            return true;
        }
        return false;
    }

    private void sendAnswer(Long chatId, String output) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage); //передача сообщения от узла
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE); //установка юзеру базового состояния
        appUserDAO.save(appUser); //сохранение изменений в БД
        return "Команда отменена";
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        ServiceCommand serviceCommand = ServiceCommand.fromValue(cmd);
        if (REGISTRATION.equals(serviceCommand)) { //входящая команда - команда регистрации
            //TODO: добавить регистрацию
            return "Временно недоступно";
        } else if (HELP.equals(serviceCommand)) { //-//- - команда получения справки
            return help(); //возврат списка доступных команд
        } else if (START.equals(serviceCommand)) { //-//- - команда начала работы
            return "Приветствую!\nЧтобы проверить список доступных команд введите \"/help\"";
        } else { //несуществующая команда
            return "Неизвестная команда. Чтобы проверить список доступных команд введите \"/help\"";
        }
    }

    private String help() { //справка о доступных командах
        return "Список доступных команд:\n"
                + "\"/cancel\" - отмена выполнения текущей команды\n"
                + "\"/registration\" - регистрация пользователя";
    }

    private AppUser findOrSaveAppUser(Update update) { //поиск юзера в БД
        User telegramUser = update.getMessage().getFrom(); //получение информации о юзере из апдейта
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId()); //поиск уже существующего юзера
        if (persistentAppUser == null) { //сохранение нового юзера в БД, поле firstLoginDate генерируется автоматически
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .lastName(telegramUser.getLastName())
                    .firstName(telegramUser.getFirstName())
                    //TODO: изменить значение по умолчанию после добавления mail-сервиса и функционала регистрации юзера
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser); //сохранение нового юзера в БД и возвращение значения
        }
        return persistentAppUser; //возвращение информации о существующем юзере
    }

    private void saveRawData(Update update) { //создание объекта RawData (сохранение данных в БД)
        RawData rawData = RawData.builder() //использование паттерна Builder
                .event(update)
                .build();
        rawDataDAO.save(rawData); /*ВАЖНО: метод автоматически создается Spring через JpaRepository,
        после сохранения возвращает обратно сохраненный объект с уже заполненным первичным ключом и привязкой объекта к сессии Hibernate*/
    }
}