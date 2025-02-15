package dzh.its.dao;

import dzh.its.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserDAO extends JpaRepository<AppUser, Long> { //интерфейс для работы с данными из БД
    Optional<AppUser> findByTelegramUserId(Long id); //декларативный метод для поиска юзера по идентификатору TelegramUserId (реализация "под капотом" через Spring)

    Optional<AppUser> findByEmail(String email); //поиск юзера по электронной почте
}