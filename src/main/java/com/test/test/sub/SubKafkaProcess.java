package com.test.test.sub;

import com.test.factory.QueueFactory;
import com.test.impl.DefaultQueueListener;
import com.test.impl.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubKafkaProcess {
    @Autowired
    QueueFactory queueFactory;

    @Autowired
    DefaultQueueListener listener;

    public void sendMessage(String message) {
        MessageQueue<SubMessage> subMessageMessageQueue = queueFactory.createQueue(SubMessage.class, 10, listener);
        SubMessage subMessage = new SubMessage();
        subMessage.put(message);
        subMessageMessageQueue.offer(subMessage);
    }
}
