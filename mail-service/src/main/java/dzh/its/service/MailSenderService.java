package dzh.its.service;

import dzh.its.dto.MailParams;

public interface MailSenderService { //для передачи объекта MailParams в Spring-класс JavaMailSender для формирования электронного письма
    void send(MailParams mailParams);
}