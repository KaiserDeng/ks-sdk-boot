package com.haihun.sdk.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel("手机设备 参数模型")
@Table(name = "tb_equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true, value = "主键ID")
    private Integer id;

    @NotBlank
    @Column(name = "app_key")
    @ApiModelProperty(required = true, value = "应用ID")
    private String appKey;

    @NotBlank
    @ApiModelProperty(required = true, value = "应用名称")
    @Column(name = "app_name")
    private String appName;

    @NotBlank
    @ApiModelProperty(required = true, value = "应用包名")
    @Column(name = "app_package")
    private String appPackage;

    @NotBlank
    @ApiModelProperty(required = true, value = "手机型号")
    @Column(name = "model")
    private String model;

    @NotBlank
    @ApiModelProperty(required = true, value = "设备制造商")
    @Column(name = "factory")
    private String factory;

    @NotBlank
    @ApiModelProperty(required = true, value = "运营商编号")
    @Column(name = "carrier")
    private String carrier;

    @NotBlank
    @ApiModelProperty(required = true, value = "屏幕尺寸")
    @Column(name = "screen_size")
    private String screenSize;

    @NotBlank
    @ApiModelProperty(required = true, value = "系统版本")
    @Column(name = "sysver")
    private String sysver;

    @NotBlank
    @ApiModelProperty(required = true, value = "手机平台")
    @Column(name = "plat")
    private String plat;

    @ApiModelProperty(value = "由服务器生成的唯一设备标识符")
    @Column(name = "duid")
    private String duid;

    @ApiModelProperty(value = "手机串号")
    @Column(name = "imei")
    private String imei;

    @ApiModelProperty(value = "唯一设备号")
    @Column(name = "serial_no")
    private String serialNo;

    @ApiModelProperty(value = "安卓唯一设备号")
    @Column(name = "android_id")
    private String androidId;

    @ApiModelProperty(value = "设备mac地址")
    @Column(name = "mac")
    private String mac;

    @ApiModelProperty(value = "预留字段，透传信息")
    @Column(name = "extra")
    private String extra;


    @ApiModelProperty(hidden = true, value = "创建时间")
    @Column(name = "create_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    @ApiModelProperty(value = "推广人")
    @Column(name = "promoter")
    private String promoter;

    @NotNull
    @Column(name = "channel")
    @ApiModelProperty(required = true, value = "channel")
    private Integer channel = 1;


}