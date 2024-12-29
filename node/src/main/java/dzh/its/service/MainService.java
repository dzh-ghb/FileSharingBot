package dzh.its.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService { //обработка всех входящих сообщений
    void processTextMessage(Update update); //обработка апдейта
}
