package dzh.its.dao;

import dzh.its.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> { //интерфейс для работы с данными из БД
}