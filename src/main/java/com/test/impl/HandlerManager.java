package com.test.impl;

import com.google.common.collect.Maps;
import com.test.face.message.Handler;
import com.test.face.message.MessageIdentification;

import java.util.List;
import java.util.Map;

public class HandlerManager {
    public Map<String, Handler> handlerMap = Maps.newConcurrentMap();

    public HandlerManager(List<Handler> handlerList) {
        for (Handler handler : handlerList) {
            // 同一个Message不能有多个处理器
            if (handlerMap.get(handler.identification()) != null) {
                throw new RuntimeException("more than 1 handler for message");
            }
            handlerMap.put(handler.identification(), handler);
        }
    }

    /**
     * 获取消息处理器
     *
     * @param message
     * @param <Message>
     * @return
     */
    public <Message> Handler<Message> getHandler(Message message) {
        if (message instanceof MessageIdentification) {
            Handler<Message> handler = handlerMap.get(((MessageIdentification) message).identification());
            if (handler != null) {
                return handler;
            }
            throw new RuntimeException("not found message handler");
        }
        throw new RuntimeException("not support message type");
    }
}
