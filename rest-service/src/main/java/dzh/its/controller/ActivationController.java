package dzh.its.controller;

import dzh.its.service.UserActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
//установка общей для всех методов в контроллере части uri-пути
@RequestMapping("/api")
@RestController
public class ActivationController { //для обработки запросов по ссылке из письма
    private final UserActivationService userActivationService;

    @RequestMapping(method = RequestMethod.GET, value = "/user/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) { //принимает GET-запрос и GET-параметр id (зашифрованный)
        boolean res = userActivationService.activation(id); //получение результата активации юзера в БД
        if (!res) { //если пользователь не активирован
            return ResponseEntity.badRequest().body("Неверная ссылка"); //считаем, что ошибка на стороне сервера (по факту ошибка могла быть в запросе)
        }
        return ResponseEntity.ok().body("Регистрация успешно завершена"); //если пользователь активирован (сообщение отобразиться в браузере)
    }
}