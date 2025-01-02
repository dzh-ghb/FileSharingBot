package dzh.its.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils { //класс для генерации текстового сообщения
    public SendMessage generateSendMessageWithText(Update update, String text) {
        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(text);
        return sendMessage; //возврат объекта для ответного сообщения
    }
}