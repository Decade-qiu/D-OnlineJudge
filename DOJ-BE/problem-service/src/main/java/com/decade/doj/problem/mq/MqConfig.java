package com.decade.doj.problem.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("doj.topic");
    }

    @Bean
    public Queue problemStatsUpdateQueue() {
        return new Queue("problem.stats.update.queue");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(problemStatsUpdateQueue()).to(topicExchange()).with("submission.created");
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
