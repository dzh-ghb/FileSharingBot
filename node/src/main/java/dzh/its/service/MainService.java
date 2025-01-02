package dzh.its.service;

import org.telegram.telegrambots.meta.api.objects.Update;

//MainService - связующее звено между БД и ConsumerServiceImpl, который будет считывать сообщения из брокера сообщений RabbitMQ
public interface MainService { //обработка всех входящих сообщений и последующего вызова необходимого сервиса в зависимости от типа входящего сообщения
    void processTextMessage(Update update); //обработка апдейта (текстовое сообщение)

    void processDocMessage(Update update); //обработка апдейта (документ)

    void processPhotoMessage(Update update); //обработка апдейта (изображение)
}