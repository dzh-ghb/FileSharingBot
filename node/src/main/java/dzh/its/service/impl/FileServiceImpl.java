package dzh.its.service.impl;

import dzh.its.dao.AppDocumentDAO;
import dzh.its.dao.AppPhotoDAO;
import dzh.its.dao.BinaryContentDAO;
import dzh.its.entity.AppDocument;
import dzh.its.entity.AppPhoto;
import dzh.its.entity.BinaryContent;
import dzh.its.exceptions.UploadFileException;
import dzh.its.service.FileService;
import dzh.its.service.enums.LinkType;
import dzh.its.utils.CryptoTool;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

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

    @Value("${link.address}") //адрес rest-сервиса
    private String linkAddress;

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, BinaryContentDAO binaryContentDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) { //метод для обработки файлов
        Document telegramDoc = telegramMessage.getDocument(); //получение документа из telegram-объекта
        String fileId = telegramDoc.getFileId();
        ResponseEntity<String> response = getFilePath(fileId); //http-запрос к Telegram
        if (response.getStatusCode() == HttpStatus.OK) { //работа с объектом, содержащим ответные данные из Telegram
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent); //создание сущности transientAppDoc с информацией о файле
            return appDocumentDAO.save(transientAppDoc); //сохранение объекта в БД и возврат persistentAppDoc с привязкой к сессии hibernate и сгенерированным id
        } else {
            //кастомное исключение
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) { //метод для обработки фото
        //TODO: добавление возможности обработки нескольких фото в одном апдейте
        int originalSizeIndex = telegramMessage.getPhoto().size() - 1; //индекс последнего (оригинального по размеру) фото в списке List<PhotoSize>
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(originalSizeIndex); //получение фото из telegram-объекта (хранит список фото разного размера, по индексу 0 получаем самое маленькое по размеру)
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId); //http-запрос к Telegram
        if (response.getStatusCode() == HttpStatus.OK) { //работа с объектом, содержащим ответные данные из Telegram
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent); //создание сущности transientAppPhoto с информацией о фото
            return appPhotoDAO.save(transientAppPhoto); //сохранение объекта в БД и возврат persistentAppPhoto с привязкой к сессии hibernate и сгенерированным id
        } else {
            //кастомное исключение
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) { //метод генерации ссылок
        String hash = cryptoTool.hashOf(docId); //получение хеша
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash; //вставка хеша в ссылку
    }

    //метод для получения объекта persistentBinaryContent
    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath); //запрос к Telegram для скачивания файла в виде массива байт
        BinaryContent transientBinaryContent = BinaryContent.builder() //объект с массивом байт (еще не привязан к БД)
                .fileAsArrayOfBytes(fileInByte)
                .build();
        //сохранение transientBinaryContent в БД и получение persistentBinaryContent с привязкой к сессии hibernate и сгенерированным id
        return binaryContentDAO.save(transientBinaryContent);
    }

    //метод для получения пути к файлу из response
    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody()); //преобразования response в объект с JSON
        return String.valueOf(jsonObject //дерево вложенных ключей JSON
                .getJSONObject("result")
                .getString("file_path")); //получение необходимых данных - путь к файлу
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

    //установка значений из полей telegram-объекта (фото) в объект AppPhoto
    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize()) //тип Integer
                .build();
    }
}