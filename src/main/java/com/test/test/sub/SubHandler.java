package com.test.test.sub;

import com.test.JsonUtil;
import com.test.face.message.Handler;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubHandler implements Handler<SubMessage>{
    @Override
    public int process(List<SubMessage> subMessages) {
        for (SubMessage t : subMessages) {
            System.out.println(JsonUtil.toJsonNode(t.getDBMessage()));
        }
        return 0;
    }

    @Override
    public String identification() {
        return "subMessage";
    }
}
