package dzh.its.service;

import dzh.its.entity.AppDocument;
import dzh.its.entity.AppPhoto;
import dzh.its.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService { //для получения файлов/фото и преобразования полученного массива байтов для передачи в теле http-ответа
    AppDocument getDocument(String id); //получение документа

    AppPhoto getPhoto(String id); //получение фото

    FileSystemResource getFileSystemResource(BinaryContent binaryContent); //преобразование массива байтов в FileSystemResource (для передачи контента в теле http-ответа)
}
