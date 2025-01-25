package dzh.its.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component //аннотация, чтобы Spring создал бин и поместил его в контекст
@Log4j //аннотация, автоматически под капотом добавляющая функциональность (logger), использование Lombok
public class TelegramBot extends TelegramWebhookBot { //класс для взаимодействия с API Telegram
    @Value("${bot.name}") //аннотация, устанавливающая значение переменной ниже из файла со свойствами
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.uri}")
    private String botUri; //статический IP-адрес системы

    private UpdateProcessor updateProcessor;

    public TelegramBot(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    @PostConstruct
    public void init() { //передача ссылки на бота в UpdateController
        updateProcessor.registerBot(this);
        try {
            SetWebhook setWebhook = SetWebhook.builder() //объект класса из библиотеки Telegram
                    .url(botUri).build();
            this.setWebhook(setWebhook); //основная логика передачи IP-адреса на сервер Telegram выполняется под капотом
        } catch (TelegramApiException e) {
            log.error(e);
        }
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
    public String getBotPath() {
        return "/update"; //передача части uri-пути, на который Telegram будет отправлять апдейты
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) { //метод использоваться не будет
        return null;
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