package dzh.its.service;

import dzh.its.entity.AppDocument;
import dzh.its.entity.AppPhoto;

public interface FileService { //для получения файлов/фото и преобразования полученного массива байтов для передачи в теле http-ответа
    AppDocument getDocument(String id); //получение документа

    AppPhoto getPhoto(String id); //получение фото
}