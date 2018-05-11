package com.test.factory;

import com.google.common.collect.Maps;
import com.test.face.QueueListener;
import com.test.impl.MessageQueue;

import java.util.Map;

public class QueueFactory {
    public volatile Map<Class, MessageQueue> queueCache = Maps.newConcurrentMap();
    volatile QueueFactory factory;
    int defaultCapacity;
    QueueListener queueListener;

    public QueueFactory(int capacity, QueueListener queueListener) {
        this.defaultCapacity = capacity;
        this.queueListener = queueListener;
    }

    /**
     * 获取队列
     *
     * @param messageClass
     * @param <Message>
     * @return
     */
    public <Message> MessageQueue<Message> getQueue(Class<Message> messageClass) {
        return queueCache.get(messageClass);
    }

    public <Message> MessageQueue<Message> getOrCreate(Class<Message> messageClass) {
        MessageQueue<Message> queue = queueCache.get(messageClass);
        if (queue != null) {
            return queue;
        }
        return createQueue(messageClass, defaultCapacity, queueListener);
    }

    /**
     * 创建队列
     *
     * @param messageClass
     * @param capacity
     * @param queueListener
     * @param <Message>
     * @return
     */
    public synchronized <Message> MessageQueue<Message> createQueue(Class<Message> messageClass, int capacity, QueueListener queueListener) {
        MessageQueue<Message> queue = queueCache.get(messageClass);
        if (queue != null) {
            return queue;
        }
        queue = new MessageQueue<Message>(capacity, queueListener);
        queueCache.put(messageClass, queue);
        return queue;
    }

    public Map<Class, MessageQueue> getQueueCache() {
        return queueCache;
    }
}
