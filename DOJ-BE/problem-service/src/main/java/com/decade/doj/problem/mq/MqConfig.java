package com.decade.doj.problem.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    // --- 用于题目统计更新的队列 ---
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("doj.topic");
    }

    @Bean
    public Queue problemStatsUpdateQueue() {
        return new Queue("problem.stats.update.queue");
    }

    @Bean
    public Binding statsBinding() {
        return BindingBuilder.bind(problemStatsUpdateQueue()).to(topicExchange()).with("submission.created");
    }

    // --- 用于多级缓存同步的广播队列 ---
    @Bean
    public FanoutExchange cacheUpdateExchange() {
        return new FanoutExchange("cache.update.exchange");
    }

    @Bean
    public Queue problemCacheUpdateQueue() {
        // 匿名队列：每个消费者实例都有自己的、临时的、独占的队列
        return new AnonymousQueue();
    }

    @Bean
    public Binding cacheBinding() {
        return BindingBuilder.bind(problemCacheUpdateQueue()).to(cacheUpdateExchange());
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
