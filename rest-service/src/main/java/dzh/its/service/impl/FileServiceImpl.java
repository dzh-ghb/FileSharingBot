package dzh.its.service.impl;

import dzh.its.dao.AppDocumentDAO;
import dzh.its.dao.AppPhotoDAO;
import dzh.its.entity.AppDocument;
import dzh.its.entity.AppPhoto;
import dzh.its.service.FileService;
import dzh.its.utils.CryptoTool;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

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
}