package dzh.its.service.impl;

import dzh.its.dao.AppUserDAO;
import dzh.its.dto.MailParams;
import dzh.its.entity.AppUser;
import dzh.its.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.hashids.Hashids;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

import static dzh.its.enums.UserState.BASIC_STATE;
import static dzh.its.enums.UserState.WAIT_FOR_EMAIL_STATE;

@Log4j
@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDAO appUserDAO;

    private final Hashids hashids;

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;

    @Override
    public String registerUser(AppUser appUser) { //обработка запроса на регистрацию юзера
        if (appUser.getIsActive()) { //если юзер уже активирован
            return "Учетная запись уже активирована";
        } else if (appUser.getEmail() != null) { //если юзер не активирован, но почта была указана ранее
            return "Письмо для подтверждения активации было отправлено на почту ранее\n" +
                    "Для завершения активации перейдите по ссылке из письма";
        }
        appUser.setState(WAIT_FOR_EMAIL_STATE); //иначе - перевод юзера в режим ввода электронной почты
        appUserDAO.save(appUser); //сохранение объекта в БД
        return "Для продолжения активации введите адрес электронной почты";
    }

    @Override
    public String setEmail(AppUser appUser, String email) { //проверка валидности введенной электронной почты
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate(); //метод для валидации электронной почты
        } catch (AddressException e) { //если адрес невалидный
            log.error("Введен невалидный адрес электронной почты: " + e);
            return "Введен невалидный адрес электронной почты, укажите корректный адрес\nДля отмены команды введите /cancel";
        }
        //электронная почта валидная - поиск в БД юзера с указанной почтой
        Optional<AppUser> appUserOpt = appUserDAO.findByEmail(email);
        if (appUserOpt.isEmpty()) { //если в БД юзера с такой почтой нет
            appUser.setEmail(email); //установка введенной почты юзеру
            appUser.setState(BASIC_STATE); //перевод юзера в дефолтное состояние
            appUser = appUserDAO.save(appUser); //сохранение данных в БД

            //формирование и отправка запроса к mail-сервису со ссылкой для активации
            String cryptoUserId = hashids.encode(appUser.getId()); //шифрование идентификатора юзера
            sendRegistrationMail(cryptoUserId, email);
            log.error("Отправка письма для подтверждения активации пользователя");
            return "Письмо для подтверждения активации отправлено на почту\n" +
                    "Для завершения активации перейдите по ссылке из письма"; //ответа при успешной отправке запроса
        } else { //если адрес уже используется в БД
            log.error("Введен использующийся адрес электронной почты");
            return "Этот адрес электронной почты уже используется, введите другой email\n" +
                    "Для отмены команды введите /cancel";
        }
    }

    //метод формирования и отправки письма
    private void sendRegistrationMail(String cryptoUserId, String email) {
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
    }
}