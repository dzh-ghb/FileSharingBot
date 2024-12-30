package dzh.its.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService { //обработка всех входящих сообщений
    void processTextMessage(Update update); //обработка апдейта (текстовое сообщение)

    void processDocMessage(Update update); //обработка апдейта (документ)

    void processPhotoMessage(Update update); //обработка апдейта (изображение)
}