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
@Table(name = "app_document") //название таблицы в БД
public class AppDocument { //класс-сущность (генерирует таблицу, если она еще не создана) - таблица в БД с информацией о файлах
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //первичный ключ

    private String telegramFileId; //идентификатор файла из telegram-объекта
    private String docName; //имя файла

    @OneToOne //связь один-к-одному - одной записи из binary_content соответствует одна запись в app_document
    private BinaryContent binaryContent; //ссылка на объект BinaryContent - поле будет хранить внешний ключ

    private String mimeType; //формат файла
    private Long fileSize; //размер файла
}