package dzh.its.service;

import dzh.its.entity.AppDocument;
import dzh.its.entity.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService { //для получения сообщения из Telegram, выполнения всех необходимых действий для скачивания и сохранения файла в БД
    AppDocument processDoc(Message telegramMessage);

    AppPhoto processPhoto(Message telegramMessage);
}