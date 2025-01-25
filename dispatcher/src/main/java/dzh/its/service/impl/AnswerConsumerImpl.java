package dzh.its.service.impl;

import dzh.its.controller.UpdateProcessor;
import dzh.its.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static dzh.its.model.RabbitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateProcessor updateProcessor;

    public AnswerConsumerImpl(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE) //прослушивание очереди
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage); //передача считанных данных в UpdateController
    }
}