package dzh.its.service.impl;

import dzh.its.dto.MailParams;
import dzh.its.service.MailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender javaMailSender; //бин на внутренний Spring-класс

    @Value("${spring.mail.username}")
    private String emailFrom; //адрес отправителя
    @Value("${service.activation.uri}")
    private String activationServiceUri; //шаблон uri-ссылки для активации, которая будет отправляться в письме

    public MailSenderServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void send(MailParams mailParams) { //формирование электронного письма
        String subject = "Активация учетной записи"; //тема письма
        String messageBody = getActivationMailBody(mailParams.getId()); //тело - текст письма
        String emailTo = mailParams.getEmailTo(); //адрес почты получателя (из объекта MailParams)

        //сборка объекта для отправки письма
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom); //адрес отправителя
        mailMessage.setTo(emailTo); //адрес получателя
        mailMessage.setSubject(subject); //тема письма
        mailMessage.setText(messageBody); //содержимое письма

        javaMailSender.send(mailMessage); //передача сконфигурированного объекта письма
    }

    private String getActivationMailBody(String id) { //формирование текстового содержимого письма
        String msg = String.format("Для завершения регистрации перейдите по ссылке:\n%s", activationServiceUri); //сообщение со ссылкой для активации
        return msg.replace("{id}", id); //добавление идентификатора из входящего запроса
    }
}