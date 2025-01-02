package dzh.its.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService { //для считывания сообщений из брокера (по типам сообщений)
    void consumeTextMessageUpdates(Update update);

    void consumeDocMessageUpdates(Update update);

    void consumePhotoMessageUpdates(Update update);
}