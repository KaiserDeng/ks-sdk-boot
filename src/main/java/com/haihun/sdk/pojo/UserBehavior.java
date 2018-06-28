package com.haihun.sdk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "tb_user_behavior")
public class UserBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="user_name")
    private String userName;

    @Column(name="description")
    private String description;

    @Column(name="behavior_type")
    private String behaviorType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="trigger_time")
    private Date triggerTime;

    @Column(name="app_id")
    private String appId;

    @Column(name="channel_Id")
    private Integer channelId;

}