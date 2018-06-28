package com.haihun.comm.constant;

/**
 * 状态码常量类
 *
 * @author kaiser·von·d
 * @version 2018/5/5
 */
public enum CodeConst {


    /**
     * 响应成功！
     */
    OK(200),

    /**
     * 参数不正确
     */
    WARN(300),

    /**
     * 根据参数未找到
     */
    NOT_FOUND(400),

    /**
     * 系统错误
     */
    ERROR(500);

    private int value;

    CodeConst(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
