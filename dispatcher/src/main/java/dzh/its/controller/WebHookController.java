package dzh.its.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@RestController
public class WebHookController { //Rest-контроллер для приема запросов от telegram
    private final UpdateProcessor updateProcessor;

    //method - тип запроса, который будет отрабатываться, value - установка идентификатора ресурса для uri-пути
    @RequestMapping(method = RequestMethod.POST, value = "/callback/update") //получение POST-запросов
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update) {
        updateProcessor.processUpdate(update);
        return ResponseEntity.ok().build(); //возврат ответа об успехе,
        //если будет ошибка - Spring перехватит ошибку и вернет 500-ый ответ
    }
}