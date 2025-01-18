package dzh.its;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//точка входа в микросервис для работы с электронной почтой
@SpringBootApplication
public class MailApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailApplication.class); //запуск микросервиса
    }
}