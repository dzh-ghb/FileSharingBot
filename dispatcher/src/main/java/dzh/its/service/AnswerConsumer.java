package dzh.its.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

//для приема ответов из RabbitMQ и передачи их в UpdateController
public interface AnswerConsumer {
    void consume(SendMessage sendMessage);
}