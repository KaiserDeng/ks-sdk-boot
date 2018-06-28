package com.haihun.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户行为枚举类型
 *
 * @author kaiser·von·d
 * @version 2018/6/8
 */

public enum UserBehaviorEnum {


    login("0", "用户{ [u] } 在 { [d] } 进行了登录操作");

    public static final Map<String,String> USER_BEHAVIOR_MAP = new HashMap<>();


    static {
        UserBehaviorEnum[] values = UserBehaviorEnum.values();
        if (values.length>0) {
            for (UserBehaviorEnum value : values) {
                USER_BEHAVIOR_MAP.put(value.getKey(),value.getVal());
            }
        }
    }


    private String key;

    private String val;

    UserBehaviorEnum(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
