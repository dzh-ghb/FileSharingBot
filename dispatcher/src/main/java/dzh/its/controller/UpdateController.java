package dzh.its.controller;

import dzh.its.service.UpdateProducer;
import dzh.its.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static dzh.its.model.RabbitQueue.*;

@Component
@Log4j
public class UpdateController { //для распределения входящих сообщений
    private TelegramBot telegramBot;
    private MessageUtils messageUtils;
    private UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils; //внедрение зависимости на MessageUtils
        this.updateProducer = updateProducer; //внедрение зависимости на UpdateProducer
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot; //связывание UpdateController и Telegram Bot
    }

    public void processUpdate(Update update) { //метод первичной проверки апдейта
        if (update == null) {
            log.error("Обновлений нет");
            return; //дальнейшая обработка апдейта производиться не будет
        }

        if (update.getMessage() != null) {
            distributeMessageByType(update); //обработка апдейта по типу входящих данных
        } else {
            log.error("Неподдерживаемый тип сообщения в обновлении: " + update);
        }
    }

    private void distributeMessageByType(Update update) { //распределение сообщений в зависимости от типа входящих данных
        Message message = update.getMessage();
        if (message.getText() != null) {
            processTextMessage(update);
        } else if (message.getDocument() != null) {
            processDocMessage(update);
        } else if (message.getPhoto() != null) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void setFileIsReceivedView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update, "Файл получен, идет обработки");
        setView(sendMessage);
    }

    private void setUnsupportedMessageTypeView(Update update) { //метода для получения ответа о неподдерживаемом типе входящего сообщения
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип сообщения");
        setView(sendMessage); //промежуточный метод для проброса ответа в телеграм бот
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
}