package dzh.its.service.impl;

import dzh.its.dao.AppDocumentDAO;
import dzh.its.dao.AppPhotoDAO;
import dzh.its.entity.AppDocument;
import dzh.its.entity.AppPhoto;
import dzh.its.service.FileService;
import dzh.its.utils.Decoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

@Log4j //подключение логирования
@RequiredArgsConstructor
@Service //создание из класса Spring-bean
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;

    private final AppPhotoDAO appPhotoDAO;

    private final Decoder decoder;

    @Override
    public AppDocument getDocument(String hash) { //получения объекта документа из БД по идентификатору
        Long id = decoder.idOf(hash); //дешифрование вернувшегося идентификатора для поиска документа в БД
        if (id == null) {
            return null;
        }
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) { //получения объекта фото из БД по идентификатору
        Long id = decoder.idOf(hash); //дешифрование вернувшегося идентификатора для поиска фото в БД
        if (id == null) {
            return null;
        }
        return appPhotoDAO.findById(id).orElse(null);
    }
}