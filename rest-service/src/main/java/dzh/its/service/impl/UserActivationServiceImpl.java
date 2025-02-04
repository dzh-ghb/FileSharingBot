package dzh.its.service.impl;

import dzh.its.dao.AppUserDAO;
import dzh.its.entity.AppUser;
import dzh.its.service.UserActivationService;
import dzh.its.utils.CryptoTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserActivationServiceImpl implements UserActivationService {
    private final AppUserDAO appUserDAO;

    private final CryptoTool cryptoTool;

    @Override
    public boolean activation(String cryptoUserId) { //активация юзера в БД после поступления запроса
        Long userId = cryptoTool.idOf(cryptoUserId); //дешифрование идентификатора из запроса
        Optional<AppUser> optional = appUserDAO.findById(userId); //поиск юзера в БД по идентификатору
        if (optional.isEmpty()) { //если юзер в БД не найден
            return false;
        }
        AppUser user = optional.get();
        user.setIsActive(true); //если юзер найден - установка флага регистрации в true
        appUserDAO.save(user); //сохранение данных в БД
        return true; //возвращение true в контроллер
    }
}