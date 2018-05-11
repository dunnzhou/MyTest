package com.test.face;

import com.test.face.message.Handler;

public interface MessageHandler<Message> {
    Handler<Message> handler();
}
