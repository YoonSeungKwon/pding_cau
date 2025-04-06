package yoon.capstone.application.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig {

    //메인 큐
    public static final String MAIN_QUEUE = "main.queue";
    public static final String MAIN_DLX_QUEUE = "main.dlx.queue";
    //롤백 큐
    public static final String ROLLBACK_QUEUE = "rollback.queue";
    public static final String ROLLBACK_DLX_QUEUE = "rollback.dlx.queue";

    public static final String MAIN_EXCHANGE = "main.exchange";
    public static final String MAIN_DLX_EXCHANGE = "main.dlx.exchange";

    public static final String ROLLBACK_EXCHANGE = "rollback.exchange";
    public static final String ROLLBACK_DLX_EXCHANGE = "rollback.dlx.exchange";

    @Value("${RABBITMQ_URL}")
    private String rabbitHost;

    @Value("${RABBITMQ_PORT}")
    private int rabbitPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;

    @Value("${RABBITMQ_ROUTING_KEY}")
    private String routingKey;


    @Bean
    Queue mainQueue(){
        return QueueBuilder.durable(MAIN_QUEUE)
                .withArgument("x-dead-letter-exchange", MAIN_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", MAIN_DLX_QUEUE)
                .build();
    }
    @Bean
    Queue mainDlxQueue(){
        return QueueBuilder.durable(MAIN_DLX_QUEUE).build();
    }
    @Bean
    Queue rollbackQueue(){
        return QueueBuilder.durable(ROLLBACK_QUEUE)
                .withArgument("x-dead-letter-exchange", ROLLBACK_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ROLLBACK_DLX_QUEUE)
                .build();
    }
    @Bean
    Queue rollbackDlxQueue(){
        return QueueBuilder.durable(ROLLBACK_DLX_QUEUE).build();
    }

    @Bean
    DirectExchange mainExchange(){
        return new DirectExchange(MAIN_EXCHANGE, true, false);
    }
    @Bean
    DirectExchange mainDlxExchange(){
        return new DirectExchange(MAIN_DLX_EXCHANGE, true, false);
    }
    @Bean
    DirectExchange rollbackExchange(){
        return new DirectExchange(ROLLBACK_EXCHANGE, true, false);
    }
    @Bean
    DirectExchange rollbackDlxExchange(){
        return new DirectExchange(ROLLBACK_DLX_EXCHANGE, true, false);
    }


    @Bean
    public Binding mainBinding(){
        return BindingBuilder.bind(mainQueue()).to(mainExchange()).with(MAIN_QUEUE);
    }
    @Bean
    public Binding mainDlxBinding(){
        return BindingBuilder.bind(mainDlxQueue()).to(mainDlxExchange()).with(MAIN_DLX_QUEUE);
    }
    @Bean
    public Binding rollbackBinding(){
        return BindingBuilder.bind(rollbackQueue()).to(rollbackExchange()).with(ROLLBACK_QUEUE);
    }
    @Bean
    public Binding rollbackDlxBinding(){
        return BindingBuilder.bind(rollbackDlxQueue()).to(rollbackDlxExchange()).with(ROLLBACK_DLX_QUEUE);
    }


    @Bean
    public CachingConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitHost);
        connectionFactory.setPort(rabbitPort);
        connectionFactory.setUsername(rabbitUsername);
        connectionFactory.setPassword(rabbitPassword);
        return connectionFactory;
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if(!ack) {
                System.out.println("Message sending failed During Async" + System.currentTimeMillis());
                log.error("메시지큐 전송 실패 payment code: " + correlationData.toString());
            }
        });

        return rabbitTemplate;
    }


    @Bean
    public MessageConverter jackson2JsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
