package com.haihun.sdk.pojo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@ApiModel("游戏信息")
@Table(name = "tb_game")
public class Game {
    /**
     * 游戏应用ID
     */
    @Id
    @Column(name = "app_id")
    private String appId;

    /**
     * 游戏名称
     */
    @Column(name = "game_name")
    private String gameName;

    /**
     * 加密方式
     */
    @Column(name = "appSecret")
    private String appSecret;

    /**
     * 充值回调路径
     */
    @Column(name = "pay_callback_Url")
    private String payCallBackUrl;


    @Column(name = "game_icon")
    private String gameIcon;


}