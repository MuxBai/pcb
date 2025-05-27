package com.work.commonconfig.config;

import com.work.commonconfig.factory.YamlPropertySourceFactory;
import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource(value = "classpath:RabbitMQ.yml" , factory = YamlPropertySourceFactory.class)
public class RabbitConfig {

    @Value("${requestQueue}")
    private String requestQueue;

    @Value("${requestExchange}")
    private String requestExchange;

    @Value("${requestRoutingKey}")
    private String requestRoutingKey;

    @Value("${resultQueue}")
    private String resultQueue;

    @Value("${resultExchange}")
    private String resultExchange;

    @Value("${resultRoutingKey}")
    private String resultRoutingKey;

    @Bean
    public Queue requestQueue() {
        return new Queue(requestQueue, true);
    }


    @Bean
    public DirectExchange  requestExchange() {
        return new DirectExchange(requestExchange, true,  false);
    }

    @Bean
    public Binding  requestBinding() {
        return BindingBuilder
                .bind(requestQueue())
                .to(requestExchange())
                .with(requestRoutingKey);
    }

    @Bean
    public Queue resultQueue(){
        return new Queue(resultQueue, true);
    }

    @Bean
    public DirectExchange  resultExchange() {
        return new DirectExchange(resultExchange, true,  false);
    }

    @Bean
    public Binding  resultBinding() {
        return BindingBuilder
                .bind(resultQueue())
                .to(resultExchange())
                .with(resultRoutingKey);
    }
}
