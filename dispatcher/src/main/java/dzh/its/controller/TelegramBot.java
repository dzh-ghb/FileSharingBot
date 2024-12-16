package dzh.its.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component //аннотация, чтобы Spring создал бин и поместил его в контекст
@Log4j //аннотация, автоматически под капотом добавляющая функциональность (logger), использование Lombok
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}") //аннотация, устанавливающая значение переменной ниже из файла со свойствами
    private String botName;
    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message originalMessage = update.getMessage();
        log.debug(originalMessage.getText());

        SendMessage response = new SendMessage();
        response.setChatId(originalMessage.getChatId());
        response.setText("DZHITS");
        sendAnswerMessage(response);
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }
}