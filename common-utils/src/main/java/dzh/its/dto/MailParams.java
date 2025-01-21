/**
 * Блок dto перенесен из mail-сервиса, так как он также будет использоваться в node-сервисе
 **/
package dzh.its.dto;

import lombok.*;

@Getter
@Setter
@Builder //для удобного создания объектов класса в node-сервисе
@AllArgsConstructor //вместо аннотации @RequiredArgsConstructor, которая не создает конструктор без параметров;
@NoArgsConstructor //без конструктора node-сервис не cкомпилируется (для Spring нужен дефолтный конструктор)
public class MailParams { //для преобразования входящего JSON (при получении запроса в контроллере) в Java-класс,
    //в итоге будет получен объект с полями, значения которых взяты из входящего запроса
    private String id; //id юзера
    private String emailTo; //почта для отправки письма
}