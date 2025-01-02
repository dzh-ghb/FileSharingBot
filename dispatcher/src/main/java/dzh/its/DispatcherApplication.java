package dzh.its;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//микросервис для первичной валидации входящих данных и распределения сообщений по очередям в брокере сообщений
@SpringBootApplication //стандартная аннотация
public class DispatcherApplication {
    public static void main(String[] args) {
        SpringApplication.run(DispatcherApplication.class); //запуск микросервиса
    }
}