package dzh.its.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static dzh.its.model.RabbitQueue.*;

@Configuration //аннотация, обязательная для конфигурационных классов
public class RabbitConfiguration { //конфигурационный класс для интеграции с RabbitMQ (объекты класса вручную создаваться не будут)
    @Bean //каждый метод будет возвращать bean, который будет использован Spring
    public MessageConverter jsonMessageConverter() { //для конвертации отправляемых в брокер апдейтов в JSON и обратного преобразования ответов в Java-объекты
        return new Jackson2JsonMessageConverter();
    }

    //методы, возвращающие beans, соответствующие очередям в брокере
    @Bean
    public Queue textMessageQueue() {
        return new Queue(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Queue docMessageQueue() {
        return new Queue(DOC_MESSAGE_UPDATE);
    }

    @Bean
    public Queue photoMessageQueue() {
        return new Queue(PHOTO_MESSAGE_UPDATE);
    }

    @Bean
    public Queue answerMessageQueue() { //очередь с ответами из node для dispatcher
        return new Queue(ANSWER_MESSAGE);
    }
}