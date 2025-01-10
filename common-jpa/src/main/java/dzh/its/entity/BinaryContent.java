package dzh.its.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "binary_content") //название таблицы в БД
public class BinaryContent { //класс-сущность (генерирует таблицу, если она еще не создана) - таблица в БД с информацией об объектах BinaryContent в виде массива байтов
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private byte[] fileAsArrayOfBytes; //массив байт
}