package com.haihun.comm.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * xml 工具类
 *
 * @author kaiser·von·d
 * @version 2018/5/14
 */
public class XMLUtils {

    private static final XmlMapper XML_MAPPER = new XmlMapper();


    /**
     * 将json对象转换成一个<xml><xx>aaa</xx></xml>格式的字符串
     * @param json json对象
     * @return  xml字符串
     * @throws JsonProcessingException json访问异常，
     * @throws NullPointerException  如果Map为空则抛出一个空指针异常。
     */
    public static String jsonToXml(Map json) throws JsonProcessingException {
        Objects.requireNonNull(json);
        return XML_MAPPER.writeValueAsString(json).replace(json.getClass().getSimpleName(), "xml");
    }


    /**
     * 将xml 转换成JSONobject对象
     * @param xml   xml字符串
     * @return      json对象
     * @throws IOException
     * @throws NullPointerException
     */
    public static JSONObject xmlToJson(String xml) throws IOException {
        if (StringUtils.isBlank(xml)) {
            throw new NullPointerException();
        }
        return XML_MAPPER.readValue(xml, JSONObject.class);
    }

}
