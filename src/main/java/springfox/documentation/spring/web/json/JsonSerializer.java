/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web.json;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haihun.annotation.ExampleFormat;
import io.swagger.models.Model;
import io.swagger.models.Swagger;
import io.swagger.models.properties.Property;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * swagger2序列化
 *
 * @author kaiser·von·d
 * @version 2018/6/4
 */
public class JsonSerializer {
    private ObjectMapper objectMapper = new ObjectMapper();

    public JsonSerializer(List<JacksonModuleRegistrar> modules) {
        for (JacksonModuleRegistrar each : modules) {
            each.maybeRegisterModule(objectMapper);
        }
    }

    public Json toJson(Object toSerialize) {
        try {
            setExample(toSerialize);
            return new Json(objectMapper.writeValueAsString(toSerialize));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not write JSON", e);
        }
    }

    /**
     * 拦截 @ApiModel的example属性使其json格式化
     *
     * @param toSerialize swagger对象
     */
    private void setExample(Object toSerialize) {
        if (toSerialize instanceof Swagger) {
            Swagger swagger = (Swagger) toSerialize;
            Map<String, Model> definitions = swagger.getDefinitions();
            definitions.forEach((x, v) -> {
                // 获取声明在 Description 属性上的 类路径
                String path = v.getDescription();
                if (StringUtils.isNotBlank(path)) {
                    try {
                        Class<?> clazz = ClassUtils.getClass(path);
                        Field[] fields = clazz.getDeclaredFields();
                        for (Field field : fields) {
                            ExampleFormat exampleFormat = field.getAnnotation(ExampleFormat.class);
                            if (exampleFormat != null) {
                                field.setAccessible(true);
                                Map<String, Property> prop = v.getProperties();
                                Property data = prop.get("data");
                                data.setExample(field.get(JSONObject.class));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
