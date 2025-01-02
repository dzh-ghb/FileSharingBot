package dzh.its.dao;

import dzh.its.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawDataDAO extends JpaRepository<RawData, Long> { //интерфейс для работы с данными из БД
    /*JpaRepository - механизм Spring, предоставляющий набор готовых стандартных методов по работе с БД
    *и гибкий (декларативный) способ создания методов со скрытием реализации*/
}