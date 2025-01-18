package dzh.its.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MailParams { //для преобразования входящего JSON (при получении запроса в контроллере) в Java-класс,
    //в итоге будет получен объект с полями, значения которых взяты из входящего запроса
    private String id; //id юзера
    private String emailTo; //почта для отправки письма
}