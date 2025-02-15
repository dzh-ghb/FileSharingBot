package dzh.its.configuration;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeConfiguration { //подключение CryptoTool в качестве Spring-bean
    @Value("${salt}") //получение salt из конфига для передачи в конструктор CryptoTool
    private String salt;

    //возвращение объекта CryptoTool, который Spring преобразует в bean
    @Bean
    public Hashids getHashids() {
        var minHashLength = 10;
        return new Hashids(salt, minHashLength);
    }
}