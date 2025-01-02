package dzh.its.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.*;

@Getter //Lombok - добавление геттеров
@Setter //Lombok - добавление сеттеров
@EqualsAndHashCode(exclude = "id") //Lombok - переопределение методов equals() и hashCode() без мутабельного поля id
@Builder //Lombok - паттерн Builder для удобного создания объектов
@NoArgsConstructor //Lombok - создание конструктора без параметров
@AllArgsConstructor //Lombok - создание конструктора с параметрами
@Entity //Spring - помеченный класс является сущностью, связанной с таблицей в БД
@Table(name = "raw_data") //название таблицы в БД
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) //hibernate-types-52 - поддержка JSONB
public class RawData { //класс-сущность для генерации (если таблица еще не создана) в БД таблицы с информацией обо всех апдейтах (с генерацией первичных ключей через БД)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //генерация значения первичных ключей через БД
    private Long id; //первичный ключ

    @Type(type = "jsonb") //аннотации для подключения типа данных JSONB
    @Column(columnDefinition = "jsonb") //через подключенную библиотеку hibernate-types-52
    private Update event; //объект апдейта из телеграма, который будет преобразовываться в тип данных JSONB
}