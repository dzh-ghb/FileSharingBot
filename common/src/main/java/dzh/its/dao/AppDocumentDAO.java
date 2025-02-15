package dzh.its.dao;

import dzh.its.entity.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> { //интерфейс для работы с данными из БД
}