package dzh.its;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//точка входа в микросервис для работы с электронной почтой
@ComponentScan("dzh.its.*") //аннотация Spring, которая указывает контейнеру на то, какие пакеты нужно сканировать для поиска компонентов
@SpringBootApplication
public class MailApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailApplication.class); //запуск микросервиса
    }
}