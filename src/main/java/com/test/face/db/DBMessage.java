package com.test.face.db;

import com.test.face.message.MessageIdentification;

/**
 * ibatis消息
 *
 * @param <DBType>
 */
public interface DBMessage<DBType> extends MessageIdentification {
    DBType getDBMessage();
}
