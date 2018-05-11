package com.test.face.kafka;


/**
 * kafka消息接口
 *
 * @param <TMessage>
 */
public interface KafkaMessage<TMessage> {
    void put(TMessage message);

    TMessage getKafkaMessage();
}
