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
        Long chatId = update.getMessage().getChatId();
        String userName = update.getMessage().getChat().getUserName();
        log.debug(String.format("Получено текстовое сообщение от %s [chat-id: %s]", userName, chatId));
        mainService.processTextMessage(update); //передача апдейта в MainService для дальнейшей обработки
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.doc-message-update}")
    public void consumeDocMessageUpdates(Update update) {
        Long chatId = update.getMessage().getChatId();
        String userName = update.getMessage().getChat().getUserName();
        log.debug(String.format("Получен документ от %s [chat-id: %s]", userName, chatId));
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.photo-message-update}")
    public void consumePhotoMessageUpdates(Update update) {
        Long chatId = update.getMessage().getChatId();
        String userName = update.getMessage().getChat().getUserName();
        log.debug(String.format("Получено изображение от %s [chat-id: %s]", userName, chatId));
        mainService.processPhotoMessage(update);
    }
}