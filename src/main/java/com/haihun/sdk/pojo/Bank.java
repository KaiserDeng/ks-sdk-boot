package com.haihun.sdk.pojo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Table(name = "tb_banks")
@ApiModel("银行实体 参数模型")
public class Bank {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    private Integer id;

    /**
     * 支付类型 0 支付宝，1，微信，3银联
     */
    @NotNull
    @Column(name = "pay_type")
    @Length(min = 1, max = 2, message = "支付类型最小为1位，最大为2位！")
    @ApiModelProperty(value = "支付类型", required = true)
    private Integer payType;

    /**
     * 商家自定义ID，支付宝，微信生成
     */
    @NotBlank
    @Column(name = "app_id")
    @ApiModelProperty(value = "支付机构应用ID")
    private String appId;

    /**
     * 应用公钥
     */
    @Column(name = "app_public_key")
    @ApiModelProperty(value = "应用公钥")
    private String appPublicKey;

    /**
     * 应用私钥
     */
    @Column(name = "app_private_key")
    @ApiModelProperty(value = "应用私钥")
    private String appPrivateKey;

    /**
     * hmac 微信密钥
     */
    @Column(name = "appSecret")
    @ApiModelProperty(value = "微信密钥")
    private String appsecret;

    /**
     * 微信 apiKey
     */
    @Column(name = "api_key")
    @ApiModelProperty(value = "微信key")
    private String apiKey;

    /**
     * 微信商家号
     */
    @Column(name = "mch_id")
    @ApiModelProperty(value = "微信商家号")
    private String mchId;

    /**
     * 支付宝应用公钥
     */
    @Column(name = "alipay_public_Key")
    @ApiModelProperty(value = "支付宝应用公钥")
    private String alipayPublicKey;

    /**
     * 游戏AppId由海魂sdk生成
     */
    @Transient
    @NotBlank
    @ApiModelProperty(value = "游戏AppId", required = true)
    private String gameId;


}