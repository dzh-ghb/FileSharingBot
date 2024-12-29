package dzh.its.dao;

import dzh.its.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawDataDAO extends JpaRepository<RawData, Long> { //для работы с БД

}