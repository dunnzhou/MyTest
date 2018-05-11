package com.test.config;

import com.test.SpringContextUtils;
import com.test.face.message.Handler;
import com.test.factory.QueueFactory;
import com.test.impl.DefaultQueueListener;
import com.test.impl.HandlerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class QueueConfig {

    @Autowired
    SpringContextUtils springContextUtils;

    @Value("${queue_size:10000}")
    int queueCapacity;

    @Bean
    public QueueFactory queueFactory(DefaultQueueListener defaultQueueListener) {
        return new QueueFactory(queueCapacity, defaultQueueListener);
    }

    @Bean
    public HandlerManager handlerManager() {
        List<Handler> handlerList = springContextUtils.getBeanForType(Handler.class);
        HandlerManager handlerManager = new HandlerManager(handlerList);
        return handlerManager;
    }

    @Bean
    public DefaultQueueListener defaultQueueListener(HandlerManager handlerManager) {
        return new DefaultQueueListener(handlerManager);
    }
}
