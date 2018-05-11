package com.test.impl;

import com.test.face.QueueListener;
import com.test.face.message.Handler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DefaultQueueListener implements QueueListener {
    Logger logger = LoggerFactory.getLogger(getClass());
    QueueListener next = null;
    private HandlerManager manager;
    // 默认触发的数量
    int trigger = 1;

    public DefaultQueueListener(HandlerManager manager) {
        this.manager = manager;
    }

    /**
     * 正常场景
     * 每写入一条记录就会触发
     * 代码没有做线程安全处理，所以可能会出现多线程的问题
     *
     * @param queue
     * @param message
     * @param state
     * @param <Message>
     */
    @Override
    public <Message> void fireNext(MessageQueue<Message> queue, Message message, boolean state) {
        if (queue.size() > trigger) {
            List<Message> processMessage = queue.tryTakeWithSize(1000, 1, TimeUnit.SECONDS);
            if (CollectionUtils.isNotEmpty(processMessage)) {
                Handler<Message> handler = manager.getHandler(message);
                handler.process(processMessage);
            }
        }
        // 距离当前取数的时间
        long timeMillis = System.currentTimeMillis() - queue.lastTake();
        if (timeMillis > 100000) {
            List<Message> processMessage = queue.takeAll();
            if (CollectionUtils.isNotEmpty(processMessage)) {
                Handler<Message> handler = manager.getHandler(message);
                handler.process(processMessage);
            }
        }
        // 如果还有下一个
        if (next != null) {
            next.fireNext(queue, message, state);
        }
    }

    /**
     * 异常场景触发
     *
     * @param queue
     * @param message
     * @param throwable
     * @param <Message>
     */
    @Override
    public <Message> void fireFail(MessageQueue<Message> queue, Message message, Throwable throwable) {
        // do something
        logger.error("write message:[{}]failed,error", message.toString(), throwable);
        // 如果还有下一个
        if (next != null) {
            next.fireFail(queue, message, throwable);
        }
    }
}
