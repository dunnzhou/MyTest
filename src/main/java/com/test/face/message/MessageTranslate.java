package com.test.face.message;

/**
 * 消息转换
 *
 * @param <SourceMessage>
 * @param <TargetMessage>
 */
public interface MessageTranslate<SourceMessage, TargetMessage> {
    TargetMessage map(SourceMessage sourceMessage);
}
