package com.haihun.sdk.mapper;

import com.haihun.comm.base.BaseMapper;
import com.haihun.sdk.pojo.Bank;
import org.apache.ibatis.annotations.Param;

public interface BankMapper extends BaseMapper<Bank> {

    void addGameAndBank(Bank bank);

    /**
     * 根据应用AppId和支付类型查询 应用对应银行
     * @param gameId    游戏应用ID
     * @param payType   支付类型
     * @return
     */
    Bank findBankByGameIdAndPayType(@Param("gameId") String gameId, @Param("payType") String payType);
}