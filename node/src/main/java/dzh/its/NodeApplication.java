package dzh.its;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//точка входа в микросервис для отработки основной логики
@EnableJpaRepositories("dzh.its.*") //аннотация Spring, которая используется на уровне конфигурации для включения репозиториев JPA
@EntityScan("dzh.its.*") //аннотация Spring, которая настраивает базовые пакеты, используемые автоконфигурацией при сканировании классов сущностей
@ComponentScan("dzh.its.*") //аннотация Spring, которая указывает контейнеру на то, какие пакеты нужно сканировать для поиска компонентов
@SpringBootApplication
public class NodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class); //запуск микросервиса
    }
}