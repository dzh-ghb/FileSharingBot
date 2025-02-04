package dzh.its.service;

import org.telegram.telegrambots.meta.api.objects.Update;

//для передачи апдейтов в RabbitMQ
public interface UpdateProducer {
    void produce(String rabbitQueue, Update update);
}