package dzh.its.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService { //для отправки ответов с узлов в брокер
    void produceAnswer(SendMessage sendMessage);
}