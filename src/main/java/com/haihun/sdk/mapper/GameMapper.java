package com.haihun.sdk.mapper;

import com.haihun.comm.base.BaseMapper;
import com.haihun.sdk.pojo.Game;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GameMapper  extends BaseMapper<Game> {

    /**
     * 查询游戏列表
     * @param page  页号
     * @param rows  页大小
     * @return      游戏列表
     */
    List<Game> findGameByPage(@Param("page") Integer page, @Param("rows") Integer rows);

    /**
     * 根据游戏名称获取游戏列表
     * @param gameName  游戏名称
     * @return  游戏列表
     */
    List<Game> queryGameByName(@Param("gameName") String gameName);

    /**
     * 获取所有的游戏的id
     * @return  id列表
     */
    List<String> findAllIds();

    /**
     * 根据游戏id获取渠道信息
     * @param appId 游戏id
     * @return
     */
    List<Map<String,String>> findChInfoByAppId(String appId);


    /**
     * 绑定默认游戏渠道
     * @param appId 游戏ID
     * @param chId  游戏ID
     */
    void bindChannel(@Param("appId")String appId,@Param("chId") Integer chId);
}