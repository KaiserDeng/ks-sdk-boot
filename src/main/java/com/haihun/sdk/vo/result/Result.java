package com.haihun.sdk.vo.result;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haihun.comm.constant.CodeConst;
import com.haihun.sdk.serialize.DataCleanSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回数据结构体
 *
 * @author kaiser·von·d
 * @version 2018/5/5
 */
@Data
@JsonSerialize(using = DataCleanSerializer.class)
public class Result implements Serializable {


    /**
     * 状态码
     */
    @ApiModelProperty(required = true, value = "状态码")
    protected int status;

    /**
     * 返回消息
     */
    @ApiModelProperty(required = true, value = "提示信息")
    protected String msg;

    /**
     * 数据内容
     */
    @ApiModelProperty(value = "返回数据主体")
    @JsonSerialize
    protected String data;


    public Result() {
        this.msg = "成功！";
        this.status = CodeConst.OK.getValue();
    }


    public Result(int status, String msg, String data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }


    public void ok(String msg) {
        this.status = CodeConst.OK.getValue();
        this.msg = msg;
    }

    public void warn(String msg) {
        this.status = CodeConst.WARN.getValue();
        this.msg = msg;
    }

    public void error(String msg) {
        this.status = CodeConst.ERROR.getValue();
        this.msg = msg;
    }


    public void notFound(String msg) {
        this.status = CodeConst.NOT_FOUND.getValue();
        this.msg = msg;
    }


}