package dzh.its.configuration;

import dzh.its.CryptoTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestServiceConfiguration { //подключение CryptoTool в качестве Spring-bean
    @Value("${salt}") //получение salt из конфига для передачи в конструктор CryptoTool
    private String salt;

    //возвращение объекта CryptoTool, который Spring преобразует в bean
    @Bean
    public CryptoTool getCryptoTool() {
        return new CryptoTool(salt);
    }
}