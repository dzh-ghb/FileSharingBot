package dzh.its.entity;

import dzh.its.entity.enums.UserState;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //первичный ключ

    private Long telegramUserId; //идентификатор юзера

    @CreationTimestamp //добавление текущей даты на момент сохранения в БД
    private LocalDateTime firstLoginDate; //дата первого подключения к боту

    private String userName; //информация из telegram-объект User
    private String lastName;
    private String firstName;

    private String email;

    private Boolean isActive; //флаг подтверждения электронной почты

    @Enumerated(EnumType.STRING) //указание, как Enum будет транслироваться в БД
    private UserState state; //состояние юзера
}