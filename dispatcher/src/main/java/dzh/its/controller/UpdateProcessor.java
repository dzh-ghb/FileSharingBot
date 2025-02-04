package dzh.its.controller;

import dzh.its.configuration.RabbitConfiguration;
import dzh.its.service.UpdateProducer;
import dzh.its.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@RequiredArgsConstructor
@Component
public class UpdateProcessor { //для распределения входящих сообщений (из бота)
    private TelegramBot telegramBot;

    private final MessageUtils messageUtils;

    private final UpdateProducer updateProducer;

    private final RabbitConfiguration rabbitConfiguration;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot; //связывание UpdateController и Telegram Bot
    }

    public void processUpdate(Update update) { //метод первичной проверки апдейта
        if (update == null) {
            log.error("Обновлений нет");
            return; //дальнейшая обработка апдейта производиться не будет
        }

        if (update.hasMessage()) {
            distributeMessageByType(update); //обработка апдейта по типу входящих данных
        } else {
            log.error("Неподдерживаемый тип сообщения в обновлении: " + update);
        }
    }

    private void distributeMessageByType(Update update) { //распределение сообщений в зависимости от типа входящих данных
        Message message = update.getMessage();
        if (message.hasText()) { //обработка текстового сообщения
            processTextMessage(update);
        } else if (message.hasDocument()) { //обработка сообщения в виде документа
            processDocMessage(update);
        } else if (message.hasPhoto()) { //обработка сообщения в виде изображения
            processPhotoMessage(update);
        } else { //обработка некорректного типа сообщения
            setUnsupportedMessageTypeView(update);
        }
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(rabbitConfiguration.getTextMessageUpdateQueue(), update); //передача апдейта с текстовым сообщением в соответствующую очередь
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(rabbitConfiguration.getDocMessageUpdateQueue(), update);
        setFileIsReceivedView(update);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(rabbitConfiguration.getPhotoMessageUpdateQueue(), update);
        setFileIsReceivedView(update);
    }

    private void setFileIsReceivedView(Update update) { //метод отправки промежуточного ответа об обработке запроса
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update, "Файл получен, идет обработка");
        setView(sendMessage);
    }

    private void setUnsupportedMessageTypeView(Update update) { //метод для получения ответа о неподдерживаемом типе входящего сообщения
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип сообщения");
        setView(sendMessage); //промежуточный метод для проброса ответа в телеграм бот
    }

    public void setView(SendMessage sendMessage) { //промежуточный метод для передачи сообщения в телеграм бота
        telegramBot.sendAnswerMessage(sendMessage);
    }
}