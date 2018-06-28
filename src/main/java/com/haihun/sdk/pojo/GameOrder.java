package com.haihun.sdk.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Table(name = "tb_order")
@ApiModel("订单 参数模型")
public class GameOrder {
    /**
     * 订单号
     */
    @Id
    @Column(name = "order_Id")
    @ApiModelProperty(hidden = true)
    private String orderId;

    /**
     * 用户名
     */
    @Column(name = "user_name")
    @ApiModelProperty(hidden = true)
    private String userName;

    /**
     * 支付机构订单号,流水号
     */
    @Column(name = "out_trade_no")
    @ApiModelProperty(hidden = true)
    private String outTradeNo;


    /**
     * 状态：1、未付款，2、已付款5、交易成功，6、交易关闭
     */
    @Column(name = "status")
    @ApiModelProperty(hidden = true)
    private String status = "1";

    /**
     * 支付类型
     */
    @NotNull(message = "支付类型不能为空！")
    @ApiModelProperty(value = "支付类型", required = true, notes = "0.支付宝 1.微信")
    @Column(name = "payment_type")
    private String paymentType;

    /**
     * 游戏ID
     */
    @NotBlank(message = "游戏ID不能为空！")
    @ApiModelProperty(value = "游戏ID", required = true)
    @Column(name = "app_id")
    private String appId;


    /**
     * 订单创建时间
     */
    @ApiModelProperty(hidden = true)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 订单更新时间
     */
    @ApiModelProperty(hidden = true)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss", serialize = false)
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 订单结束时间
     */
    @ApiModelProperty(hidden = true)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_time")
    private Date endTime;

    /**
     * 订单关闭时间
     */
    @ApiModelProperty(hidden = true)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss", serialize = false)
    @Column(name = "close_time")
    private Date closeTime;

    /**
     * 订单支付时间
     */

    @ApiModelProperty(hidden = true)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "payment_time")
    private Date paymentTime;


    /**
     * 商品标题
     */
    @NotBlank(message = "商品标题不能为空")
    @ApiModelProperty(value = "商品标题", required = true)
    @Column(name = "title")
    private String title;

    /**
     * 总金额
     */
    @NotNull
    @ApiModelProperty(value = "商品金额", required = true)
    @Column(name = "total_fee")
    private String totalFee;


    @ApiModelProperty(value = "额外的")
    @Column(name = "extra")
    private String extra;


    /**
     * 银行，支付回调地址，由充值机构回调
     */
    @Transient
    @JSONField(serialize = false)
    @ApiModelProperty(hidden = true)
    private String notifyUrl;


    @Transient
    @JSONField(serialize = false)
    @ApiModelProperty(value = "token")
    private String token;

    /**
     * 微信参数字段
     */
    @Transient
    @JSONField(serialize = false)
    @ApiModelProperty(hidden = true)
    private String spbillCreateIp;

    @Transient
    @ApiModelProperty(value = "支付渠道 1： App 2：手机网页")
    private String payChannel = "1";


    /**
     * 支付状态内部枚举类
     */
    public enum GameOrderTypeEnum {
        // 未付款
        WAIT_BUYER_PAY("1"),
        // 已付款
        TRADE_SUCCESS("2"),
        // 已取消或者关闭
        TRADE_CLOSED("3");

        private String val;

        GameOrderTypeEnum(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }
    }

    /**
     * 获取支付类型名称
     *
     * @return 名称
     */
    public String getPaymentTypeName() {
        if ("0".equalsIgnoreCase(this.getPaymentType())) {
            return "Alipay";
        }
        if ("1".equalsIgnoreCase(this.getPaymentType())) {
            return "Wechatpay";
        } else {
            throw new IllegalArgumentException("error paymentType ! " + this.paymentType);
        }
    }

    /**
     * 获取订单金额。单位(元)
     * @return  订单金额
     */
    public String getTotalFeeFormat() {
        return String.format("%.2f",Double.valueOf(totalFee) / 100);
    }
}