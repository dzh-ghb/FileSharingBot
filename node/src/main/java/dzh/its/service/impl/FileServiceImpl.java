package dzh.its.service.impl;

import dzh.its.dao.AppDocumentDAO;
import dzh.its.dao.BinaryContentDAO;
import dzh.its.entity.AppDocument;
import dzh.its.entity.BinaryContent;
import dzh.its.exceptions.UploadFileException;
import dzh.its.service.FileService;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;

    @Value("${service.file_info.uri}") //адрес для запроса информации о файле у Telegram
    private String fileInfoUri;

    @Value("${service.file_storage.uri}") //адрес для скачивания файла
    private String fileStorageUri;

    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;


    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) { //метод для обработки файлов
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileId); //http-запрос к Telegram
        if (response.getStatusCode() == HttpStatus.OK) { //работа с объектом, содержащим ответные данные из Telegram
            JSONObject jsonObject = new JSONObject(response.getBody()); //преобразования response в объект с JSON
            String filePath = String.valueOf(jsonObject //дерево вложенных ключей JSON
                    .getJSONObject("result")
                    .getString("file_path")); //получение необходимых данных - путь к файлу
            byte[] fileInByte = downloadFile(filePath); //запрос к Telegram для скачивания файла в виде массива байт
            BinaryContent transientBinaryContent = BinaryContent.builder() //объект с массивом байт (еще не привязан к БД)
                    .fileAsArrayOfBytes(fileInByte)
                    .build();
            BinaryContent persistentBinaryContent = binaryContentDAO.save(transientBinaryContent); //сохранение transientBinaryContent в БД и
            //получение persistentBinaryContent с привязкой к сессии hibernate и сгенерированным id
            Document telegramDoc = telegramMessage.getDocument(); //получение документа из telegram-объекта
            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent); //создание сущности transientAppDoc с информацией о файле
            return appDocumentDAO.save(transientAppDoc); //сохранение объекта в БД и возврат persistentAppDoc с привязкой к сессии hibernate и сгенерированным id
        } else {
            //кастомное исключение
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    //формирование http-запроса (GET-запрос) к Telegram с помощью Spring-инструмента RestTemplate
    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        //в метод uri-запросом передаются параметры для подстановки в запрос
        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }

    //формирование окончательного uri для загрузки файла
    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath); //подстановка значений токена и пути к файлу на сервере
        URL urlObj = null; //объект для последующей загрузки объекта
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }

        //TODO: оптимизировать под работу с объемными файлами
        try (InputStream iS = urlObj.openStream()) {
            return iS.readAllBytes(); //скачивание файла в виде потока байтов целиком
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }

    //установка значений из полей telegram-объекта (документа) в объект AppDocument
    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }
}