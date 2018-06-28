package com.haihun.sdk.serialize;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.haihun.sdk.vo.result.Result;

import java.io.IOException;

/**
 * Result 清除空data字段
 * @author kaiser·von·d
 * @version 2018/6/26
 */
public class DataCleanSerializer extends JsonSerializer {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value instanceof Result) {
            value = JSONObject.parseObject(JSONObject.toJSONString(value));
            gen.writeObject(value);
            gen.flush();
        }
    }
}
