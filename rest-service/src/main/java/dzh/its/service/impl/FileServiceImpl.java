package dzh.its.service.impl;

import dzh.its.CryptoTool;
import dzh.its.dao.AppDocumentDAO;
import dzh.its.dao.AppPhotoDAO;
import dzh.its.entity.AppDocument;
import dzh.its.entity.AppPhoto;
import dzh.its.entity.BinaryContent;
import dzh.its.service.FileService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j //подключение логирования
@Service //создание из класса Spring-bean
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final CryptoTool cryptoTool;

    //подключение бинов, необходимых для работы с БД
    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument getDocument(String hash) { //получения объекта документа из БД по идентификатору
        Long id = cryptoTool.idOf(hash); //дешифрование вернувшегося идентификатора для поиска документа в БД
        if (id == null) {
            return null;
        }
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) { //получения объекта фото из БД по идентификатору
        Long id = cryptoTool.idOf(hash); //дешифрование вернувшегося идентификатора для поиска фото в БД
        if (id == null) {
            return null;
        }
        return appPhotoDAO.findById(id).orElse(null);
    }

    //преобразование массива байтов в объект FileSystemResource, который можно отправить в теле ответа юзеру, чтобы браузер скачал файл
    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            //TODO: добавить генерацию имени временного файла
            File temp = File.createTempFile("tempFile", ".bin"); //создание временного файла с расширением ".bin"
            temp.deleteOnExit(); //удаление временного файла из постоянной памяти компьютера после завершения работы приложения
            //(по сути метод регистрирует файл в очередь на удаление)
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes()); //запись массива байтов в объект временного файла
            return new FileSystemResource(temp); //оборачивание объекта временного файла в FileSystemResource и возврат этого объекта
        } catch (IOException e) {
            log.error(e); //логирование в случае ошибки
            return null;
        }
    }
}