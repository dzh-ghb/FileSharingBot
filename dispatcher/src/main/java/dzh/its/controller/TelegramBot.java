package dzh.its.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component //аннотация, чтобы Spring создал бин и поместил его в контекст
@Log4j //аннотация, автоматически под капотом добавляющая функциональность (logger), использование Lombok
public class TelegramBot extends TelegramLongPollingBot { //класс для взаимодействия с API Telegram
    @Value("${bot.name}") //аннотация, устанавливающая значение переменной ниже из файла со свойствами
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    private UpdateController updateController;

    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }

    @PostConstruct
    public void init() { //передача ссылки на бота в UpdateController
        updateController.registerBot(this);
    }

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
        updateController.processUpdate(update); //передача апдейта в UpdateController для последующей обработки
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