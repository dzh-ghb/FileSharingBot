package dzh.its.service.impl;

import dzh.its.service.ConsumerService;
import dzh.its.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.text-message-update}")
    //прослушивание очереди с текстовыми сообщениями из RabbitMQ
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: Получено текстовое сообщение");
        mainService.processTextMessage(update); //передача апдейта в MainService для дальнейшей обработки
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.doc-message-update}")
    public void consumeDocMessageUpdates(Update update) {
        log.debug("NODE: Получен документ");
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.photo-message-update}")
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE: Получено изображение");
        mainService.processPhotoMessage(update);
    }
}