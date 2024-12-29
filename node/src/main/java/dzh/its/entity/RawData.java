package dzh.its.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "raw_data")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class RawData { //для сохранения всех входящих апдейтов и присваивания им уникальных идентификаторов
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //первичный ключ

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Update event; //объект апдейта из телеграма
}