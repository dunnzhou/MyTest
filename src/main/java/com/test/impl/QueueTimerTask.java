package com.test.impl;

import com.test.face.message.Handler;
import com.test.factory.QueueFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class QueueTimerTask {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    QueueFactory queueFactory;
    @Autowired
    HandlerManager handlerManager;

    /**
     * fixedDelay单位毫秒
     */
    @Scheduled(fixedDelay = 1 * 1000)
    public void processAllMessage() {
        if (queueFactory == null || MapUtils.isEmpty(queueFactory.getQueueCache())) {
            logger.warn("no message must be processed");
            return;
        }
        Collection<MessageQueue> queues = queueFactory.getQueueCache().values();
        for (MessageQueue queue : queues) {
            List messages = queue.takeAll();
            if (CollectionUtils.isNotEmpty(messages)) {
                Object message = messages.get(0);
                Handler handler = handlerManager.getHandler(message);
                handler.process(messages);
            }
        }
    }
}
