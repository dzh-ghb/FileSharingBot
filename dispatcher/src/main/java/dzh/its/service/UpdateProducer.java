package dzh.its.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducer { //для передачи апдейтов в RabbitMQ
    void produce(String rabbitQueue, Update update);
}
