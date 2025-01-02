package dzh.its.service.impl;

import dzh.its.service.UpdateProducer;
import lombok.extern.log4j.Log4j;
import org.jvnet.hk2.annotations.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Service
@Log4j
public class UpdateProducerImpl implements UpdateProducer {
    private final RabbitTemplate rabbitTemplate; //bean из зависимости-стартера spring-boot-starter-amqp

    public UpdateProducerImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText());
        rabbitTemplate.convertAndSend(rabbitQueue, update); //передача апдейта в брокер сообщений
    }
}