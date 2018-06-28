package com.haihun.sdk.pojo;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@ApiModel("用户信息")
@Table(name = "tb_user")
public class User {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JSONField(serialize = false)
    @ApiModelProperty(hidden = true, value = "主键ID")
    private Integer id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Column(name = "user_name")
    @Length(min = 6, max = 20, message = "用户名最小为6位，最大为20位!")
    @ApiModelProperty(value = "用户名", required = true)
    private String userName;

    /**
     * 密码
     */
    @JSONField(serialize = false)
    @Column(name = "pwd")
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码最小为6位,最大为20位！")
    @ApiModelProperty(value = "密码", required = true)
    private String pwd;

    /**
     * 微信ID = openId
     */
    @Column(name = "wechat_id")
    @ApiModelProperty(value = "微信ID")
    private String wechatId;

    /**
     * QQID
     */
    @Column(name = "sina_id")
    @ApiModelProperty(value = "sinaId")
    private String sinaId;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;


    /**
     * 用户昵称
     */
    @Column(name = "nick_name")
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    /**
     * 手机号
     */
    @Column(name = "mobile")
    @ApiModelProperty(value = "手机号")
    private Long mobile;

    /**
     * 电话
     */
    @Column(name = "tel")
    @ApiModelProperty(value = "电话")
    private String tel;

    /**
     * 身份证
     */
    @Column(name = "card_id")
    @ApiModelProperty(value = "身份证")
    private String cardId;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    @Column(name = "address")
    private String address;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    @Column(name = "gender")
    private String gender;

    /**
     * qq
     */
    @ApiModelProperty(value = "QQ")
    @Column(name = "qq")
    private Integer qq;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @Email
    @Column(name = "email")
    private String email;

    /**
     * 账号类型0.普通 1. 手机
     */
    @ApiModelProperty(hidden = true)
    @Column(name = "account_type")
    private String accountType;

    /**
     * 创建时间
     */
    @ApiModelProperty(hidden = true)
    @Column(name = "create_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    private Date createTime;

    /**
     * 访客账户
     */
    @Column(name = "customer_acc")
    @ApiModelProperty(value = "访客账号")
    private String customerAcc;

    /**
     * 生日
     */
    @Column(name = "birthday")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "生日", example = "2018-4-20 18:30:42")
    private Date birthday;

    /**
     * 是否已实名
     */
    @Column(name = "is_real_name")
    @ApiModelProperty(value = "是否为实名用户")
    private String isRealName;

    /**
     * 真实姓名
     */
    @Column(name = "real_name")
    @ApiModelProperty(value = "真实姓名")
    private String realName;


    // 以下为参数字段，数据库无对应字段

    //设备授权ID
    @NotBlank(message = "duid cannot empty!")
    @ApiModelProperty(required = true, value = "设备授权ID")
    @Transient
    private String duid;

    //
    @ApiModelProperty(hidden = true)
    @Column(name = "equipment_id")
    private Integer equipmentId;

    /**
     * 应用ID
     */
    @Transient
    @JSONField(serialize = false)
    @ApiModelProperty(hidden = true)
    private String appId;

    /**
     * 验证码
     */
    @Transient
    @JSONField(serialize = false)
    @ApiModelProperty(value = "验证码")
    private String code;


}