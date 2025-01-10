package dzh.its.dao;

import dzh.its.entity.AppPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> { //интерфейс для работы с данными из БД
}