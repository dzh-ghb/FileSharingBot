package dzh.its.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_photo") //название таблицы в БД
@Entity
public class AppPhoto { //класс-сущность (генерирует таблицу, если она еще не создана) - таблица в БД с информацией о фото
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //первичный ключ

    private String telegramFileId; //идентификатор фото из telegram-объекта

    @OneToOne //связь один-к-одному - одной записи из binary_content соответствует одна запись в app_document
    private BinaryContent binaryContent; //ссылка на объект BinaryContent - поле будет хранить внешний ключ

    private Integer fileSize; //размер фото
}