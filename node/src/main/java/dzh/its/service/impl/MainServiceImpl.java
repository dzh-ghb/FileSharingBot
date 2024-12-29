package dzh.its.service.impl;

import dzh.its.dao.AppUserDAO;
import dzh.its.dao.RawDataDAO;
import dzh.its.entity.AppUser;
import dzh.its.entity.RawData;
import dzh.its.service.MainService;
import dzh.its.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static dzh.its.entity.enums.UserState.BASIC_STATE;

@Service
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
    public void processTextMessage(Update update) {
        saveRawData(update);
        Message textMessage = update.getMessage();
        User telegramUser = textMessage.getFrom();
        AppUser appUser = findOrSaveAppUser(telegramUser); //получение и передача в findOrSaveAppUser()

        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("FROM NODE");
        producerService.produceAnswer(sendMessage); //передача сообщения от узла
    }

    private AppUser findOrSaveAppUser(User telegramUser) { //поиск юзера в БД
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
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