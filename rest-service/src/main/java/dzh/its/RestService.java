package dzh.its;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//точка входа в микросервис для скачивания файлов из БД по ссылке и обработки запросов на активацию учетных записей юзеров
@EnableJpaRepositories("dzh.its.*") //аннотации необходимы для подтягивания
@EntityScan("dzh.its.*") //бинов и сущностей Spring из common-модуля
@ComponentScan("dzh.its.*") //внутрь контекста микросервиса
@SpringBootApplication
public class RestService {
    public static void main(String[] args) {
        SpringApplication.run(RestService.class); //запуск микросервиса
    }
}