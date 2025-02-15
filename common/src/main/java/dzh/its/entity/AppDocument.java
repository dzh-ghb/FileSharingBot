package dzh.its.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_document") //название таблицы в БД
@Entity
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppDocument that = (AppDocument) o;
        return telegramFileId != null && Objects.equals(telegramFileId, that.telegramFileId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}