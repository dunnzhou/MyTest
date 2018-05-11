package com.test.impl;

import com.test.face.db.DBMessage;
import com.test.face.kafka.KafkaMessage;
import com.test.face.message.MessageTranslate;

/**
 * 某一种消息类型
 */
public abstract class AbstractMessage<KafkaType, DBType> implements KafkaMessage<KafkaType>, DBMessage<DBType>, MessageTranslate<KafkaType, DBType> {
    KafkaType message;

    /**
     * 添加kafka的消息
     *
     * @param message
     */
    @Override
    public void put(KafkaType message) {
        this.message = message;
    }

    /**
     * 获取kafka的消息
     *
     * @return
     */
    @Override
    public KafkaType getKafkaMessage() {
        return message;
    }

    /**
     * 获取db的消息
     *
     * @return
     */
    public DBType getDBMessage() {
        return map(getKafkaMessage());
    }
}
