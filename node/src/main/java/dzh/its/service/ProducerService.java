package dzh.its.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService { //для отправки ответа с узлов в брокер
    void produceAnswer(SendMessage sendMessage);
}
