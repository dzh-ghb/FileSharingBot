package dzh.its.controller;

import dzh.its.entity.AppDocument;
import dzh.its.entity.AppPhoto;
import dzh.its.entity.BinaryContent;
import dzh.its.service.FileService;
import lombok.extern.log4j.Log4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j
//установка общей для всех методов в контроллере части uri-пути (во избежание дублирования кода)
@RequestMapping("/file")
//в ответ вернется RawData - Spring не будет искать в ресурсах шаблон страницы (view), а сразу вернет массив байтов из body
@RestController
public class FileController { //для обработки входящих http-запросов (запросы на получение данных)
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    //method - тип запроса, который будет отрабатываться, value - установка идентификатора ресурса для uri-пути
    @RequestMapping(method = RequestMethod.GET, value = "/get-doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id) { //получение документа
        //ResponseEntity<?> - Spring-класс для удобной сборки http-ответа, @RequestParam("id") - описывает ожидаемые параметры входящего GET-запроса
        //TODO: добавить ControllerAdvice для формирования Bad Request (для обработки внутренних ошибок и формирования детализированных ответов юзеру)
        AppDocument doc = fileService.getDocument(id); //получение из FileService объекта документа по идентификатору
        if (doc == null) {
            return ResponseEntity.badRequest().build(); //возврат 400-го ответа (Bad Request) об ошибке в запросе (если документа нет)
        }

        BinaryContent binaryContent = doc.getBinaryContent(); //получение BinaryContent объекта из полученного документа
        FileSystemResource fileSystemResource = fileService.getFileSystemResource(binaryContent); //формирование объекта, который можно отправить в теле ответа юзеру
        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build(); //возврат 500-го ответа (Internal Server Error) об ошибке на стороне сервера
            //(например, если документ найден в БД, НО его не получилось вернуть)
        }
        return ResponseEntity.ok() //возврат 200-го ответа (OK) при успешном создании объекта FileSystemResource
                .contentType(MediaType.parseMediaType(doc.getMimeType())) //добавление к ответу заголовка с форматом контента
                //(чтобы браузер создал из потока байтов файл с нужным расширением)
                .header("Content-disposition", "attachment; filename=" + doc.getDocName()) //указывает браузеру, как воспринимать информацию:
                //attachment - скачивание файла, filename - имя файла (без заголовка файл откроется в браузере без скачивания)
                .body(fileSystemResource);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id) { //получение фото, метод идентичен getDoc()
        //TODO: добавить ControllerAdvice для формирования badRequest
        AppPhoto photo = fileService.getPhoto(id);
        if (photo == null) {
            return ResponseEntity.badRequest().build();
        }

        BinaryContent binaryContent = photo.getBinaryContent();
        FileSystemResource fileSystemResource = fileService.getFileSystemResource(binaryContent);
        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) //все фото хранятся в формате JPEG
                .header("Content-disposition", "attachment;") //для фото имя файла неизвестно
                .body(fileSystemResource);
    }
}