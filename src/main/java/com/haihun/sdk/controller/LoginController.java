package com.haihun.sdk.controller;

import com.alibaba.fastjson.JSONObject;
import com.haihun.annotation.ResponseEncrypt;
import com.haihun.comm.constant.CodeConst;
import com.haihun.comm.util.BeanUtil;
import com.haihun.comm.util.ConstFuncUtil;
import com.haihun.comm.util.ValidateUtil;
import com.haihun.sdk.pojo.User;
import com.haihun.sdk.service.UserService;
import com.haihun.sdk.vo.args.BindMobileArgs;
import com.haihun.sdk.vo.args.LoginArgs;
import com.haihun.sdk.vo.args.RealNameArgs;
import com.haihun.sdk.vo.args.ResetPwdArgs;
import com.haihun.sdk.vo.result.LoginResult;
import com.haihun.sdk.vo.result.QuickRegResult;
import com.haihun.sdk.vo.result.Result;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author kaiser·von·d
 * @version 2018/4/20
 */
@Slf4j
@RestController
@RequestMapping("/sso")
public class LoginController {


    @Autowired
    private UserService service;


    @PostMapping("/customerLogin")
    @ResponseEncrypt
    @ApiOperation(value = "游客登录", notes = "请求/响应加密")
    @ApiImplicitParam(name = "duid", value = "设备ID")
    public QuickRegResult customerLogin(HttpServletRequest request, @RequestBody Map<String, String> duid) {
        try {
            // 用户快速注册
            String val = duid.get("duid");
            LoginArgs info = service.quickRegister(val);
            info.setDuid(val);
            info.setAppId((String) request.getAttribute("ak"));
            // 用户快速登录

            QuickRegResult result = new QuickRegResult();

            // 将登录返回值中的数据copy到响应消息体中。
            BeanUtil.copyPropIgnoreNull(service.login(info), result);

            if (result.getStatus() != CodeConst.OK.getValue()) {
                return result;
            }
            JSONObject json = JSONObject.parseObject(result.getData());
            json.put("userName", info.getUserName());
            json.put("pwd", info.getPwd());

            result.setData(json.toJSONString());
            return result;
        } catch (Exception e) {
            log.error("quickRegister faild info : " + e.getMessage());
            return ConstFuncUtil.buildErrorResult(e, QuickRegResult.class);
        }

    }

    @PostMapping("/logout")
    @ApiOperation(value = "退出游戏", notes = "不加密")
    @ApiImplicitParam(name = "token", value = "用户授权token", paramType = "form")
    @ApiResponses({
            @ApiResponse(code = 200, message = "退出成功！"),
            @ApiResponse(code = 400, message = "token无效！"),
            @ApiResponse(code = 500, message = "系统错误！")})
    public Result logout(@RequestParam("token") String token) {
        try {
            return service.logout(token);
        } catch (Exception e) {
            log.error("user logout error ! detail : " + e.getMessage());
            return ConstFuncUtil.buildErrorResult(e);
        }
    }


    @PostMapping("/login")
    @ApiOperation(value = "登录/快速登录", notes = "请求/响应加密")
    @ApiResponses({
            @ApiResponse(code = 200, message = "登录成功！"),
            @ApiResponse(code = 300, message = "参数错误！"),
            @ApiResponse(code = 400, message = "没有对应记录！"),
            @ApiResponse(code = 500, message = "系统错误！")

    })
    @ResponseEncrypt
    public LoginResult login(@RequestBody LoginArgs info) {
        try {
            return service.login(info);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e, LoginResult.class);
        }
    }


    @PostMapping("/registerAndLogin")
    @ResponseEncrypt
    @ApiOperation(value = "注册并登录", notes = "请求加密")
    public LoginResult registerAndLogin(@RequestBody User user) {
        log.warn("user : " + user.toString());
        String pwd = user.getPwd();
        try {
            LoginResult result = new LoginResult();
            LoginArgs info = new LoginArgs();
            // 如果渠道是不第三方，则进行参数检查
            if (StringUtils.isNotBlank(user.getWechatId()) || StringUtils.isNotBlank(user.getSinaId())) {


                User palyer = service.findUserByWeChatIdOrQqId(user);
                if (palyer == null) {
                    Map<String, String> keyPair = service.generatorNameAndPwd();
                    user.setUserName(keyPair.get("userName"));
                    user.setPwd(keyPair.get("pwd"));
                    // 用户注册
                    pwd = keyPair.get("pwd");
                    service.register(user, result);
                } else {
                    BeanUtil.copyPropIgnoreNull(palyer, user);
                    pwd = user.getPwd();
                }
                info.setOriginType("3rd");
            } else {
                result = ValidateUtil.validate(user, LoginResult.class);
                if (result.getStatus() != CodeConst.OK.getValue()) return result;
                // 用户注册
                service.register(user, result);
            }

            if (result.getStatus() != CodeConst.OK.getValue()) return result;
            info.setDuid(user.getDuid());
            info.setAppId(user.getAppId());
            info.setUserName(user.getUserName());
            info.setPwd(pwd);
            // 用户登录
            result = service.login(info);

            return result;
        } catch (Exception e) {
            log.error("register account failed message :" + e);
            return ConstFuncUtil.buildErrorResult(e, LoginResult.class);
        }
    }

    @PostMapping("/register")
    @ApiOperation(value = "注册用户", notes = "请求加密，明细：响应状态")
    @ApiResponses({
            @ApiResponse(code = 200, message = "注册成功！"),
            @ApiResponse(code = 300, message = "警告！"),
            @ApiResponse(code = 500, message = "系统错误！"),
    })
    public Result register(@RequestBody User user) {
        try {
            Result result = ValidateUtil.validate(user, Result.class);
            if (result.getStatus() != CodeConst.OK.getValue()) return result;
            service.register(user, result);
            return result;
        } catch (Exception e) {
            log.error("register account failed message :" + e);
            return ConstFuncUtil.buildErrorResult(e);
        }
    }

    @PostMapping("/resetPwd")
    @ApiOperation(value = "找回/忘记密码", notes = "请求加密,明细：响应状态")
    @ApiResponses({
            @ApiResponse(code = 200, message = "重置成功！"),
            @ApiResponse(code = 300, message = "没有该用户！"),
            @ApiResponse(code = 400, message = "密码错误！"),
            @ApiResponse(code = 500, message = "系统错误！"),
    })
    public Result resetPwd(@RequestBody ResetPwdArgs param) {
        try {
            Result result = ValidateUtil.validate(param);
            if (result.getStatus() != CodeConst.OK.getValue()) return result;
            return service.resetPwd(param, result);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }
    }


    @PostMapping(value = "/smsVerifyCode")
    @ApiOperation(value = "发送验证码", notes = "不加密，明细：响应状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "form"),
            @ApiImplicitParam(name = "status", value = "注册传入'1'，快速登录传入'2'，其它传入'0'", required = true, paramType = "form")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "发送成功！"),
            @ApiResponse(code = 300, message = "不能重复发送！"),
            @ApiResponse(code = 500, message = "系统错误！"),
    })
    public Result smsVerifyCode(@RequestParam("mobile") String mobile, @RequestParam("status") String status) {
        try {
            return service.smsVerifyCode(mobile, status);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }
    }

    @PostMapping("/getGameProtocol")
    @ApiOperation(value = "获取游戏协议", notes = "不加密")
    public Result getGameProtocol() {
        try {
            return service.getGameProtocol();
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }
    }

    @PostMapping("/realName")
    @ApiOperation(value = "实名认证", notes = "需要加密")
    @ApiResponses({
            @ApiResponse(code = 200, message = "认证成功！"),
            @ApiResponse(code = 300, message = "token无效！"),
            @ApiResponse(code = 400, message = "不存在的用户！"),
            @ApiResponse(code = 500, message = "系统错误！"),
    })
    @ResponseEncrypt
    public Result realName(@RequestBody RealNameArgs param) {
        try {
            Result result = ValidateUtil.validate(param);
            if (ConstFuncUtil.checkResult(result)) return result;
            return service.checkRealName(param);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }
    }


    @PostMapping("/bindMobile")
    @ApiOperation(value = "绑定手机", notes = "需要加密")
    @ApiResponses({
            @ApiResponse(code = 200, message = "绑定成功！"),
            @ApiResponse(code = 300, message = "绑定失败！"),
            @ApiResponse(code = 400, message = "认证失败！"),
            @ApiResponse(code = 500, message = "系统错误！"),
    })
    @ResponseEncrypt
    public Result bindMobile(@RequestBody BindMobileArgs args) {
        try {
            Result result = ValidateUtil.validate(args);
            if (ConstFuncUtil.checkResult(result)) return result;
            return service.bindMobile(args);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }
    }

    @PostMapping("/unBindMobile")
    @ApiOperation(value = "手机解绑")
    @ApiResponses({
            @ApiResponse(code = 200, message = "解绑成功！"),
            @ApiResponse(code = 300, message = "参数错误！"),
            @ApiResponse(code = 400, message = "解绑失败！"),
            @ApiResponse(code = 500, message = "系统错误！"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "验证码", paramType = "form"),
            @ApiImplicitParam(name = "token", value = "token", paramType = "form"),
    })
    public Result unBindMobile(@RequestParam("mobile") String mobile, @RequestParam("code") String code,@RequestParam("token") String token) {
        try {
            Result result = new Result();
            if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code)) {
                result.warn("param error ! ");
            }
            return service.unBindMobile(code,token);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }
    }


    @PostMapping("/checkToken")
    @ApiOperation(value = "根据token查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "授权token", required = true, paramType = "form"),
            @ApiImplicitParam(name = "userName", value = "用户ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "appId", value = "游戏ID", required = true, paramType = "form"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "有效的token！"),
            @ApiResponse(code = 300, message = "参数错误！"),
            @ApiResponse(code = 400, message = "无效的token！"),
            @ApiResponse(code = 500, message = "系统错误！"),
    })
    public Result queryUserByToken(@RequestParam("token") String token,
                                   @RequestParam("userId") String userId,
                                   @RequestParam("appKey") String appKey) {
        try {
            Result result = new Result();
            String userInfo = service.queryUserByToken(token);
            if (StringUtils.isBlank(userInfo)) {
                result.notFound("invalid token");
                return result;
            }
            JSONObject json = JSONObject.parseObject(userInfo);
            String appId = json.getString("appId");
            String id = json.getString("id");
            if (!userId.equals(id)) {
                result.warn("Error id param !");
                return result;
            }
            if (!appKey.equals(appId)) {
                result.warn("Error appId param !");
                return result;
            }
            return result;
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }
    }


}
