package dzh.its.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService { //для считывания сообщений из брокера
    void consumeTextMessageUpdate(Update update);

    void consumeDocMessageUpdate(Update update);

    void consumePhotoMessageUpdate(Update update);
}
