package com.haihun.sdk.controller;

import com.haihun.annotation.ResponseEncrypt;
import com.haihun.comm.util.ConstFuncUtil;
import com.haihun.comm.util.ValidateUtil;
import com.haihun.sdk.pojo.Equipment;
import com.haihun.sdk.service.EquipmentService;
import com.haihun.sdk.vo.result.EquipmentResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 移动端设备管理控制器
 *
 * @author kaiser·von·d
 * @version 2018/5/24
 */
@Api(tags = "equipment")
@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @PostMapping("/init")
    @ResponseEncrypt
    @ApiOperation(value = "初始化sdk", notes = "双向加密")
    @ApiResponses({
            @ApiResponse(code = 200, message = "登录成功！"), @ApiResponse(code = 300, message = "参数错误！"),
            @ApiResponse(code = 400, message = "没有对应记录！"), @ApiResponse(code = 500, message = "系统错误！")})
    public EquipmentResult init(@RequestBody Equipment equipment) {
        try {
            EquipmentResult result = ValidateUtil.validate(equipment, EquipmentResult.class);
            if (ConstFuncUtil.checkResult(result)) return result;
            return equipmentService.init(equipment);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e, EquipmentResult.class);
        }
    }
}
