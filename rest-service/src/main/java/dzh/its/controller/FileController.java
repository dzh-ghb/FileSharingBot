package dzh.its.controller;

import dzh.its.entity.AppDocument;
import dzh.its.entity.AppPhoto;
import dzh.its.entity.BinaryContent;
import dzh.its.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j
@RequiredArgsConstructor
//установка общей для всех методов в контроллере части uri-пути (во избежание дублирования кода)
@RequestMapping("/file")
//в ответ вернется RawData - Spring не будет искать в ресурсах шаблон страницы (view), а сразу вернет массив байтов из body
@RestController
public class FileController { //для обработки входящих http-запросов (GET-запросы для получения данных с сервера)
    private final FileService fileService;

    //method - тип запроса, который будет отрабатываться, value - установка идентификатора ресурса для uri-пути
    @RequestMapping(method = RequestMethod.GET, value = "/get-doc")
    public void getDoc(@RequestParam("id") String id, HttpServletResponse response) { //получение документа, @RequestParam("id") - описывает ожидаемые параметры входящего GET-запроса
        //вместо формирования ответа через ResponseEntity<?> (Spring-класс для удобной сборки http-ответа), ответ формируется вручную через перехваченный объект HttpServletResponse
        //TODO: добавить ControllerAdvice для формирования Bad Request (для обработки внутренних ошибок и формирования детализированных ответов юзеру)
        AppDocument doc = fileService.getDocument(id); //получение из FileService объекта документа по идентификатору
        if (doc == null) { //если объект не был найден
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //возврат 400-го ответа (Bad Request) - считаем, что юзер прислал некорректный id
            return;
        }
        //иначе, ручное формирование успешного ответа
        response.setContentType(String.valueOf(MediaType.parseMediaType(doc.getMimeType()))); //формат файла, в который поток байтов будет преобразован после скачивания
        response.setHeader("Content-disposition", "attachment; filename=" + doc.getDocName()); //указывает браузеру, что файл необходимо скачать
        //(attachment - скачивание файла) и присвоить имя (filename - имя файла) - без указания заголовка файл откроется в браузере без скачивания
        response.setStatus(HttpServletResponse.SC_OK); //200-й ответ (OK)

        BinaryContent binaryContent = doc.getBinaryContent(); //получение BinaryContent объекта из полученного документа
        try {
            ServletOutputStream out = response.getOutputStream();
            out.write(binaryContent.getFileAsArrayOfBytes()); //запись массива байтов с контентом напрямую в поток вывода
            //(не нужно хранить временный файл в постоянной памяти приложения)
            out.close(); //закрытие потока вывода
        } catch (IOException e) { //если возникнет ошибка
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //изменение статуса ответа на 500-ый (Internal Server Error)
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response) { //получение фото, метод идентичен getDoc()
        //TODO: добавить ControllerAdvice для формирования Bad Request
        AppPhoto photo = fileService.getPhoto(id);
        if (photo == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setContentType(String.valueOf(MediaType.IMAGE_JPEG)); //после скачивания поток байтов будет преобразован в файл формата JPEG
        response.setHeader("Content-disposition", "attachment;");
        response.setStatus(HttpServletResponse.SC_OK);

        BinaryContent binaryContent = photo.getBinaryContent();
        try {
            ServletOutputStream out = response.getOutputStream();
            out.write(binaryContent.getFileAsArrayOfBytes());
            out.close();
        } catch (IOException e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}