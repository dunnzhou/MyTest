package com.test.test.topic;

import com.test.JsonUtil;
import com.test.impl.AbstractMessage;

public class TopicMessage extends AbstractMessage<String, Topic> {
    @Override
    public String identification() {
        return "topicMessage";
    }

    @Override
    public Topic map(String s) {
        return JsonUtil.toObject(s, Topic.class);
    }
}
