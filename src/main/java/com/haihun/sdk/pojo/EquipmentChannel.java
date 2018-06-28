package com.haihun.sdk.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "tb_equipment_channel")
public class EquipmentChannel {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 唯一设备号
     */
    @Column(name = "duid")
    private String duid;

    /**
     * 渠道接入时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 游戏应用ID
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * 游戏渠道ID
     */
    @Column(name = "channel_id")
    private Integer channelId;

    /**
     * 除ID以外所有字段拼接过后的MD5值
     */
    @Column(name = "ec_id")
    private String ecId;


}