package com.test.test.sub;

import com.test.JsonUtil;
import com.test.impl.AbstractMessage;

public class SubMessage extends AbstractMessage<String, Sub> {
    @Override
    public String identification() {
        return "subMessage";
    }

    @Override
    public Sub map(String s) {
        return JsonUtil.toObject(s, Sub.class);
    }
}
