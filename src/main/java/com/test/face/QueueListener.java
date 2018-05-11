package com.test.face;

import com.test.impl.MessageQueue;

public interface QueueListener {
    <Message> void fireNext(MessageQueue<Message> queue, Message message, boolean state);

    <Message> void fireFail(MessageQueue<Message> queue, Message message, Throwable throwable);
}
