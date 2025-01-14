package dzh.its;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//точка входа в микросервис для отработки основной логики
@SpringBootApplication
public class NodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class); //запуск микросервиса
    }
}