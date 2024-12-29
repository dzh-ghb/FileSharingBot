package dzh.its.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AnswerConsumer { //для приема ответов из RabbitMQ и передачи их в UpdateController
    void consume(SendMessage sendMessage);
}
