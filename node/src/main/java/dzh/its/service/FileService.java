package dzh.its.service;

import dzh.its.entity.AppDocument;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService { //для получения сообщения из Telegram, выполнения всех необходимых действий для скачивания и сохранения файла в БД
    AppDocument processDoc(Message externalMessage);
}