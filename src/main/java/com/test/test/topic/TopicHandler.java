package com.test.test.topic;

import com.test.face.message.Handler;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TopicHandler implements Handler<TopicMessage> {
    @Override
    public int process(List<TopicMessage> topics) {
        for (TopicMessage t : topics) {
            System.out.println(t.getDBMessage().name);
        }
        return 0;
    }

    @Override
    public String identification() {
        return "topicMessage";
    }
}
