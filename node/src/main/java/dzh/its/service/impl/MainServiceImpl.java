package dzh.its.service.impl;

import dzh.its.dao.AppUserDAO;
import dzh.its.dao.RawDataDAO;
import dzh.its.entity.AppUser;
import dzh.its.entity.RawData;
import dzh.its.entity.enums.UserState;
import dzh.its.service.MainService;
import dzh.its.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static dzh.its.entity.enums.UserState.BASIC_STATE;
import static dzh.its.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static dzh.its.service.enums.ServiceCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void processTextMessage(Update update) { //обработка текстовых сообщений
        saveRawData(update); //сохранения адпейта целиком
        AppUser appUser = findOrSaveAppUser(update); //сохранение или поиск юзера в БД
        UserState userState = appUser.getState(); //текущее состояние юзера
        String text = update.getMessage().getText(); //текст сообщения из входящего апдейта
        String output = ""; //переменная-заготовка для ответа

        if (CANCEL.equals(text)) { //входящий текст содержит команду отмены
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

        //TODO: добавить функционал по сохранению документов
        String answer = "Документ успешно загружен. Ссылка для скачивания: http://benzotest.ru/get-doc/???";
        sendAnswer(chatId, answer);
    }

    @Override
    public void processPhotoMessage(Update update) { //обработка сообщения-изображения
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) { //если загрузка контента запрещена
            return;
        }

        //TODO: добавить функционал по сохранению изображений
        String answer = "Документ успешно загружен. Ссылка для скачивания: http://benzotest.ru/get-photo/???";
        sendAnswer(chatId, answer);
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        UserState userState = appUser.getState();

        if (!appUser.getIsActive()) { //проверка подтверждения активации учетной записи
            String error = "Необходимо пройти процесс регистрации или активировать свою учетную запись для предоставления возможности загрузки контента";
            sendAnswer(chatId, error);
            return true;
        } else if (!BASIC_STATE.equals(userState)) { //проверка текущего состояние юзера - загружать контент можно только в базовом состоянии
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
        if (REGISTRATION.equals(cmd)) { //входящая команда - команда регистрации
            //TODO: добавить регистрацию
            return "Временно недоступно";
        } else if (HELP.equals(cmd)) { //-//- - команда получения справки
            return help(); //возврат списка доступных команд
        } else if (START.equals(cmd)) { //-//- - команда начала работы
            return "Приветствую!\nЧтобы проверить список доступных команд введите \"/help\"";
        } else { //несуществующая команда
            return "Неизвестная команда. Чтобы проверить список доступных команд введите \"/help\"";
        }
    }

    private String help() {
        return "Список доступных команд:\n"
                + "\"/cancel\" - отмена выполнения текущей команды\n"
                + "\"/registration\" - регистрация пользователя";
    }

    private AppUser findOrSaveAppUser(Update update) { //поиск юзера в БД
        User telegramUser = update.getMessage().getFrom();
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId()); //поиск уже существующего юзера
        if (persistentAppUser == null) {
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
        return persistentAppUser;
    }

    private void saveRawData(Update update) { //создание объекта RawData
        RawData rawData = RawData.builder() //использование паттерна Builder
                .event(update)
                .build();
        rawDataDAO.save(rawData); //метод автоматически создается Spring через JpaRepository
    }
}