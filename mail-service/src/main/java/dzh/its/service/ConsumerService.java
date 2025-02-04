package dzh.its.service;

import dzh.its.dto.MailParams;

public interface ConsumerService { //организация связи между микросервисами node и mail через брокер сообщений
    void consumeRegistrationMail(MailParams mailParams);
}