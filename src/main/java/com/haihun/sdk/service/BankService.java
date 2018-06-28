package com.haihun.sdk.service;

import com.alibaba.fastjson.JSONObject;
import com.haihun.sdk.mapper.BankMapper;
import com.haihun.sdk.pojo.Bank;
import com.haihun.sdk.pojo.Game;
import com.haihun.sdk.service.base.BaseService;
import com.haihun.sdk.vo.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author kaiser·von·d
 * @version 2018/5/10
 */
@Slf4j
@Service
@Transactional(value = "masterTransactionManager", rollbackFor = {Exception.class, RuntimeException.class})
public class BankService extends BaseService<Bank> {

    @Autowired
    private BankMapper bankMapper;

    @Autowired
    private GameService service;

    /**
     * 替添加银行
     *
     * @param bank
     * @return
     */
    public Result add(Bank bank) {
        Result result = new Result();
        try {
            Game game = service.findById(bank.getGameId());
            if (game == null) {
                result.notFound("not found game !");
                return result;
            }
            // 查看是否有重复添加。
            Bank bankInfo = findBankByGameIdAndPayType(bank.getGameId(), String.valueOf(bank.getPayType()));

            if (bankInfo != null) {
                result.warn("The game cannot have the same payment type !");
                return result;
            }

            // 保存银行信息
            save(bank);

            // 保存游戏与银行对应信息
            bankMapper.addGameAndBank(bank);
            result.setData(JSONObject.toJSONString(bank));
            return result;
        } catch (Exception e) {
            log.error("add bank error ! detail : " + e.getMessage());
            throw new RuntimeException("add bank error !");
        }
    }


    /**
     * {@link com.haihun.sdk.mapper.BankMapper#findBankByGameIdAndPayType }
     */
    public Bank findBankByGameIdAndPayType(String gameId, String payType) {
        return bankMapper.findBankByGameIdAndPayType(gameId, payType);
    }



}
