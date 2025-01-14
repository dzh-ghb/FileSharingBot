package dzh.its;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//точка входа в микросервис для скачивания файлов из БД по ссылке и обработки запросов на активацию учетных записей юзеров
@SpringBootApplication
public class RestService {
    public static void main(String[] args) {
        SpringApplication.run(RestService.class); //запуск микросервиса
    }
}