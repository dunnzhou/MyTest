package com;

import com.test.JsonUtil;
import com.test.test.sub.Sub;
import com.test.test.sub.SubKafkaProcess;
import com.test.test.topic.Topic;
import com.test.test.topic.TopicKafkaProcess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyApplication.class)
public class AppTest{

    @Autowired
    TopicKafkaProcess topicKafkaProcess;

    @Autowired
    SubKafkaProcess subKafkaProcess;

    @Test
    public void test() throws InterruptedException {
        Topic topic = new Topic();
        topic.setName("测试姓名");
        topicKafkaProcess.sendMessage(JsonUtil.toJson(topic));


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100000 ;i++ ) {
                    Sub sub = new Sub();
                    sub.setFp("this is lisp"+ i);
                    sub.setName("list"+ i);
                    subKafkaProcess.sendMessage(JsonUtil.toJson(sub));
                }
            }
        });
        thread.setDaemon(true);
        Thread.sleep(1000);
        thread.start();

        Thread.sleep(1000);

        Thread.sleep(1000);
    }
}
