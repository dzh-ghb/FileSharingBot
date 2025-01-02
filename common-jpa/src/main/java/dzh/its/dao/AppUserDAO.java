package dzh.its.dao;

import dzh.its.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserDAO extends JpaRepository<AppUser, Long> { //интерфейс для работы с данными из БД
    AppUser findAppUserByTelegramUserId(Long id); //декларативный метод для поиска юзера по идентификатору TelegramUserId (реализация "под капотом" через Spring)
}