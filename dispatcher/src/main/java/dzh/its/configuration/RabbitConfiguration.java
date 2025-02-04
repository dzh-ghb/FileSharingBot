package dzh.its.configuration;

import lombok.Getter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration //аннотация, обязательная для конфигурационных классов
public class RabbitConfiguration { //конфигурационный класс для интеграции с RabbitMQ (объекты класса вручную создаваться не будут)
    @Value("${spring.rabbitmq.queues.text-message-update}")
    private String textMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.doc-message-update}")
    private String docMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.photo-message-update}")
    private String photoMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessageQueue;

    @Bean //каждый метод будет возвращать bean, который будет использован Spring
    public MessageConverter jsonMessageConverter() { //для конвертации отправляемых в брокер апдейтов в JSON и обратного преобразования ответов в Java-объекты
        return new Jackson2JsonMessageConverter();
    }

    //методы, возвращающие beans, соответствующие очередям в брокере
    @Bean
    public Queue textMessageQueue() {
        return new Queue(textMessageUpdateQueue);
    }

    @Bean
    public Queue docMessageQueue() {
        return new Queue(docMessageUpdateQueue);
    }

    @Bean
    public Queue photoMessageQueue() {
        return new Queue(photoMessageUpdateQueue);
    }

    @Bean
    public Queue answerMessageQueue() { //очередь с ответами из node для dispatcher
        return new Queue(answerMessageQueue);
    }
}