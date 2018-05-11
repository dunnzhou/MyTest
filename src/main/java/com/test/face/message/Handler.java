package com.test.face.message;

import java.util.List;

public interface Handler<Message> extends MessageIdentification {
    int process(List<Message> messageList);
}
