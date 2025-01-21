package dzh.its.service.impl;

import dzh.its.dao.AppUserDAO;
import dzh.its.dto.MailParams;
import dzh.its.entity.AppUser;
import dzh.its.service.AppUserService;
import dzh.its.utils.CryptoTool;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

import static dzh.its.entity.enums.UserState.BASIC_STATE;
import static dzh.its.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;

@Log4j
@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    @Value("${service.mail.uri}")
    private String mailServiceUrl;

    public AppUserServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public String registerUser(AppUser appUser) { //обработка запроса на регистрацию юзера
        if (appUser.getIsActive()) { //если юзер уже активирован
            return "Вы уже зарегистрированы!";
        } else if (appUser.getEmail() != null) { //если юзер не активирован, но почта была указана ранее
            return "Письмо для подтверждения регистрации было отправлено Вам на почту ранее\n" +
                    "Для завершения регистрации перейдите по ссылке в письме";
        }
        appUser.setState(WAIT_FOR_EMAIL_STATE); //иначе - перевод юзера в режим ввода электронной почты
        appUserDAO.save(appUser); //сохранение объекта в БД
        return "Для продолжения регистрации введите адрес Вашей электронной почты";
    }

    @Override
    public String setEmail(AppUser appUser, String email) { //проверка валидности введенной электронной почты
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate(); //метод для валидации электронной почты
        } catch (AddressException e) { //если адрес невалидный
            return "Введите корректный адрес электронной почты\nДля отмены команды введите \"/cancel\"";
        }
        //электронная почта валидная - поиск в БД юзера с указанной почтой
        Optional<AppUser> optional = appUserDAO.findByEmail(email);
        if (optional.isEmpty()) { //если в БД юзера с такой почтой нет
            appUser.setEmail(email); //установка введенной почты юзеру
            appUser.setState(BASIC_STATE); //перевод юзера в дефолтное состояние
            appUser = appUserDAO.save(appUser); //сохранение данных в БД

            //формирование и отправка запроса к mail-сервису со ссылкой для активации
            String cryptoUserId = cryptoTool.hashOf(appUser.getId()); //шифрование идентификатора юзера
            ResponseEntity<String> response = sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) { //если отправка запроса завершилась неудачно
                String msg = String.format("Неудачная отправка письма на электронную почту \"%s\"", email);
                log.error(msg);
                appUser.setEmail(null); //удаление почты из БД для возможности повторной регистрации
                appUserDAO.save(appUser); //сохранение данных в БД
                return msg; //сообщение для юзера
            }
            return "Письмо для подтверждения регистрации отправлено Вам на почту\n" +
                    "Для завершения регистрации перейдите по ссылке в письме"; //ответа при успешной отправке запроса
        } else { //если адрес уже используется в БД
            return "Этот адрес электронной почты уже используется, введите другой email\n" +
                    "Для отмены команды введите \"/cancel\"";
        }
    }

    //метод формирования отправки запроса к mail-сервису со ссылкой для активации (POST-запрос)
    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        RestTemplate restTemplate = new RestTemplate(); //Spring-инструмент для формирования http-запросов
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); //передача данных в формате JSON (указание для Spring)
        MailParams mailParams = MailParams.builder() //создание DTO с данными
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        HttpEntity<MailParams> request = new HttpEntity<>(mailParams, headers);

        //подстановка параметров в uri-запрос
        return restTemplate.exchange(mailServiceUrl,
                HttpMethod.POST,
                request,
                String.class);
    }
}