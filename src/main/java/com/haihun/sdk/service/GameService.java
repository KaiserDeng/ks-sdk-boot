package com.haihun.sdk.service;

import com.alibaba.fastjson.JSONObject;
import com.haihun.comm.util.ConstFuncUtil;
import com.haihun.config.redis.JedisService;
import com.haihun.sdk.mapper.GameMapper;
import com.haihun.sdk.pojo.Game;
import com.haihun.sdk.service.base.BaseService;
import com.haihun.sdk.vo.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author kaiser·von·d
 * @version 2018/4/26
 */
@Slf4j
@Service
@Transactional(value = "masterTransactionManager", rollbackFor = {Exception.class, RuntimeException.class})
public class GameService extends BaseService<Game> {

    public static final String GAME_ID_LIST = "game_id_list";

    /**
     * 游戏列表缓存存活时间 单位(秒)
     */
    public static final int GAME_LIST_TIME_OUT = 86400;

    @Autowired
    private JedisService jedisService;


    @Autowired
    private GameMapper gameMapper;


    /**
     * 添加游戏
     *
     * @param gameName       游戏名称
     * @param payCallBackUrl 游戏支付回调地址
     * @return 结果
     */
    public Result add(String gameName, String payCallBackUrl) {
        try {
            String appId = this.getAppId();
            //  循环判定 ID 是否appId是否有重复
            while (findById(appId) != null) {
                appId = this.getAppId();
            }
            // 设置游戏基本信息
            Game game = new Game();
            game.setAppId(appId);
            game.setGameName(gameName);
            game.setPayCallBackUrl(payCallBackUrl);
            String appSecret = ConstFuncUtil.getUUID();
            game.setAppSecret(appSecret);
            // 保存游戏
            save(game);

            // 绑定渠道

            // 绑定海魂sdk渠  道
            gameMapper.bindChannel(game.getAppId(), 1);

            // 将新增的游戏ID缓存到redis
            jedisService.sadd(GAME_ID_LIST, game.getAppId());

            Result result = new Result();
            JSONObject json = new JSONObject();
            json.put("appSecret", appSecret);
            json.put("appId", appId);
            result.setData(json.toJSONString());
            return result;
        } catch (Exception e) {
            log.error("add game error ! detail:　{} ", e.getMessage());
            throw new RuntimeException("add game error ! detail: " + e.getMessage());
        }
    }


    /**
     * 更新游戏回调地址
     *
     * @param appId          游戏应用ID
     * @param gameName       游戏名称
     * @param payCallBackUrl 回调地址
     * @return
     */
    public Result updatGame(String appId, String gameName, String payCallBackUrl) {
        try {
            Result result = new Result();
            Game game = findById(appId);
            if (game == null) {
                result.notFound("not found game !");
                return result;
            }
            game.setPayCallBackUrl(payCallBackUrl);
            game.setGameName(gameName);
            update(game);
            return result;
        } catch (Exception e) {
            log.error("update game error ! detial :  {} ", e.getMessage());
            throw new RuntimeException("update game error ! detial :  " + e.getMessage());
        }

    }


    /**
     * 根据时间点获取appId
     *
     * @return
     */
    public String getAppId() {
        long currentTime = System.currentTimeMillis();
        String head = "hh";
        if (currentTime % 2 == 0) {
            return head + ConstFuncUtil.getUUID().substring(0, 16);
        } else {
            String uuid = ConstFuncUtil.getUUID();
            return head + uuid.substring(16, uuid.length());
        }
    }

    /**
     * 查询游戏列表
     *
     * @param page 当前页号
     * @param rows 页大小
     * @return 游戏列表
     */
    public Result findGameByPage(Integer page, Integer rows) {
        Result result = new Result();
        try {
            List<Game> games = gameMapper.findGameByPage(page - 1, rows);
            if (games == null && games.size() < 1) {
                return result;
            }
            return new Result() {{
                setData(JSONObject.toJSONString(games));
            }};
        } catch (Exception e) {
            log.error("query game list error detail : {}", e.getMessage());
            throw new RuntimeException("query game list error detail : " + e.getMessage());
        }
    }

    /**
     * 根据游戏名称查询游戏
     *
     * @param gameName 游戏名称
     * @return
     */
    public Result queryGameByName(String gameName) {
        Result result = new Result();
        try {
            List<Game> games = gameMapper.queryGameByName(gameName);
            if (games == null && games.size() < 1) {
                return result;
            }
            return new Result() {{
                setData(JSONObject.toJSONString(games));
            }};
        } catch (Exception e) {
            log.error("by gameName query game list error detail : {}", e.getMessage());
            throw new RuntimeException("by gameName query game list error detail : " + e.getMessage());
        }

    }

    /**
     * @return
     * @see GameMapper#findAllIds()
     */
    public List<String> findAllIds() {
        try {
            return gameMapper.findAllIds();
        } catch (Exception e) {
            log.error("GameService findAllIds error! detail: {} ", e.getMessage());
            return new ArrayList();
        }
    }

    /**
     * @see GameMapper#findChInfoByAppId(String)
     */
    public List<Map<String, String>> findChInfoByAppId(String appId) {
        try {
            return gameMapper.findChInfoByAppId(appId);
        } catch (Exception e) {
            log.error("GameService findChInfoByAppId error! detail: {} ", e.getMessage());
            return new ArrayList();
        }
    }

}
