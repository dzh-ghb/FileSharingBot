package dzh.its.service.impl;

import dzh.its.service.UpdateProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@RequiredArgsConstructor
@Component
@Service
public class UpdateProducerImpl implements UpdateProducer {
    private final RabbitTemplate rabbitTemplate; //bean из зависимости-стартера spring-boot-starter-amqp

    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText());
        rabbitTemplate.convertAndSend(rabbitQueue, update); //передача апдейта в брокер сообщений
    }
}