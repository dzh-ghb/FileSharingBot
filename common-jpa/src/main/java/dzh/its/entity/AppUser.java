package dzh.its.entity;

import dzh.its.entity.enums.UserState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user") //название таблицы в БД
@Entity
public class AppUser { //класс-сущность (генерирует таблицу, если она еще не создана) - таблица в БД с информацией о юзерах
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //первичный ключ

    private Long telegramUserId; //идентификатор юзера из telegram-объекта

    @CreationTimestamp //добавление текущей даты на момент сохранения в БД
    private LocalDateTime firstLoginDate; //дата первого подключения к боту

    private String userName; //информация из telegram-объекта User - никнейм юзера

    private String lastName; //фамилия юзера

    private String firstName; //имя юзера

    private String email; //указанная юзером электронная почта

    private Boolean isActive; //флаг подтверждения электронной почты

    @Enumerated(EnumType.STRING) //указание для Spring Data о том, как Enum будет транслироваться в БД
    private UserState state; //состояние юзера
}