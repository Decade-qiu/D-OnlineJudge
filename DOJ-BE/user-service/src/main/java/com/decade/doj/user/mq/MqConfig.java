package com.decade.doj.user.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("doj.topic");
    }

    @Bean
    public Queue statsUpdateQueue() {
        return new Queue("stats.update.queue");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(statsUpdateQueue()).to(topicExchange()).with("problem.solved");
    }
}
