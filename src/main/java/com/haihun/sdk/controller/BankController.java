package com.haihun.sdk.controller;

import com.haihun.comm.util.ConstFuncUtil;
import com.haihun.sdk.pojo.Bank;
import com.haihun.sdk.service.BankService;
import com.haihun.sdk.vo.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kaiser·von·d
 * @version 2018/5/23
 */
@Api(tags = "bank")
@RestController
@RequestMapping("/bankManager")
public class BankController {

    @Autowired
    private BankService bankService;

    @PostMapping("/add")
    @ApiOperation(value = "添加银行信息,并绑定游戏应用")
    public Result add(@RequestBody Bank bank) {
        try {
            return bankService.add(bank);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }

    }
}
