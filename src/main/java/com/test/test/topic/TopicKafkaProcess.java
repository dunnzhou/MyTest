package com.test.test.topic;

import com.test.factory.QueueFactory;
import com.test.impl.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopicKafkaProcess {

    @Autowired
    QueueFactory queueFactory;

    public void sendMessage(String message) {
        MessageQueue<TopicMessage> topicMessageQueue = queueFactory.getOrCreate(TopicMessage.class);
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.put(message);
        topicMessageQueue.offer(topicMessage);
    }
}
