package com.test.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.test.face.QueueListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 消息队列
 *
 * @param <Message>
 */
public class MessageQueue<Message> {
    private final ArrayBlockingQueue<Message> queue;
    private QueueListener listener;
    private volatile long lastOfferTime;
    private volatile long lastTakeTime;
    private ReentrantLock takeLock = new ReentrantLock();

    public MessageQueue(int capacity, QueueListener listener) {
        queue = Queues.newArrayBlockingQueue(capacity);
        this.listener = listener;
    }

    /**
     * 写入消息
     *
     * @param message
     * @return
     */
    public boolean offer(Message message) {
        try {
            // 如果1s还没写入，就放弃写入队列
            boolean state = queue.offer(message, 1, TimeUnit.SECONDS);
            // 如果成功，设置最后写入消息的时间
            if (state) {
                lastOfferTime = System.currentTimeMillis();
            }
            listener.fireNext(this, message, state);
            return state;
        } catch (InterruptedException e) {
            listener.fireFail(this, message, e);
            return false;
        }
    }

    /**
     * 获取指定的size的message
     *
     * @param takeSize
     * @return
     */
    public List<Message> take(int takeSize) {
        return tryTakeWithSize(takeSize, 1, TimeUnit.SECONDS);
    }

    /**
     * 获取队列所有的message
     *
     * @return
     */
    public List<Message> takeAll() {
        return tryTakeWithSize(queue.size(), 1, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取指定size的Message
     *
     * @param size
     * @param timeout
     * @param unit
     * @return
     */
    public List<Message> tryTakeWithSize(int size, long timeout, TimeUnit unit) {
        final ReentrantLock lock = this.takeLock;
        try {
            lock.tryLock(timeout, unit);
            if (queue.size() < size) {
                return Collections.emptyList();
            }
            List<Message> messageList = Lists.newArrayListWithCapacity(size);
            queue.drainTo(messageList, size);
            lastTakeTime = System.currentTimeMillis();
            return messageList;
        } catch (InterruptedException e) {
            return Collections.emptyList();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 最后写入时间
     * 单位ms
     *
     * @return
     */
    public long lastOffer() {
        return lastOfferTime;
    }

    /**
     * 最后获取数时间
     * 单位ms
     *
     * @return
     */
    public long lastTake() {
        return lastTakeTime;
    }

    /**
     * queueSize
     *
     * @return
     */
    public int size() {
        return queue.size();
    }
}
