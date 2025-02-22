package dzh.its.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Getter //Lombok - добавление геттеров
@Setter //Lombok - добавление сеттеров
@Builder //Lombok - паттерн Builder для удобного создания объектов
@NoArgsConstructor //Lombok - создание конструктора без параметров
@AllArgsConstructor //Lombok - создание конструктора с параметрами
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) //hibernate-types-52 - поддержка JSONB
@Table(name = "raw_data") //название таблицы в БД
@Entity //Spring - помеченный класс является сущностью, связанной с таблицей в БД
public class RawData { //класс-сущность (генерирует таблицу, если она еще не создана) - таблица в БД с информацией обо всех апдейтах (с генерацией первичных ключей через БД)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //генерация значения первичных ключей через БД
    private Long id; //первичный ключ

    @Type(type = "jsonb") //аннотации для подключения типа данных JSONB
    @Column(columnDefinition = "jsonb") //через подключенную библиотеку hibernate-types-52
    private Update event; //объект апдейта из телеграма, который будет преобразовываться в тип данных JSONB

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RawData rawData = (RawData) o;
        return event != null && Objects.equals(event, rawData.event);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}