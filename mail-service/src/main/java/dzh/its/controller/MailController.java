package dzh.its.controller;

import dzh.its.dto.MailParams;
import dzh.its.service.MailSenderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//установка общей части uri-пути (ссылки)
@RequestMapping("/mail")
//в ответ вернется RawData - Spring не будет искать в ресурсах шаблон страницы (view), а сразу вернет массив байтов из body
@RestController
public class MailController { //для обработки входящих post-запросов (запрос на отправку данных на сервер)
    private final MailSenderService mailSenderService;

    public MailController(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @PostMapping("/send") //идентификатор ресурса в ссылке с адресом сайта
    public ResponseEntity<?> sendActivationMail(@RequestBody MailParams mailParams) { //получение входящих POST-запросов
        //ResponseEntity<?> - Spring-класс для удобной сборки http-ответа, @RequestBody - описывает ожидаемые параметры входящего GET-запроса
        mailSenderService.send(mailParams);
        return ResponseEntity.ok().build(); //если будет ошибка, то упадет исключение и Spring вернет 500-й код ошибки
        //в будущем лучше добавлять ControllerAdvice - механизм Spring для перехвата исключений и отправки ответов в соответствии с прописанной логикой
    }
}