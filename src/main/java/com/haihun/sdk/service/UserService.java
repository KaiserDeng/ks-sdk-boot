package com.haihun.sdk.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.StreamUtil;
import com.haihun.comm.security.MD5;
import com.haihun.comm.sms.SmsUtil;
import com.haihun.comm.util.BeanUtil;
import com.haihun.comm.util.ConstFuncUtil;
import com.haihun.comm.util.ValidateUtil;
import com.haihun.config.redis.JedisService;
import com.haihun.enums.UserBehaviorEnum;
import com.haihun.sdk.mapper.UserMapper;
import com.haihun.sdk.pojo.*;
import com.haihun.sdk.service.base.BaseService;
import com.haihun.sdk.vo.args.BindMobileArgs;
import com.haihun.sdk.vo.args.LoginArgs;
import com.haihun.sdk.vo.args.RealNameArgs;
import com.haihun.sdk.vo.args.ResetPwdArgs;
import com.haihun.sdk.vo.result.LoginResult;
import com.haihun.sdk.vo.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关逻辑层
 *
 * @author kaiser·von·d
 * @version 2018/4/20
 */
@Transactional(value = "masterTransactionManager", rollbackFor = {Exception.class, RuntimeException.class})
@Service
@Slf4j
public class UserService extends BaseService<User> {


    @Autowired
    private EquipmentService equipmentService;


    @Autowired
    private UserBehaviorService userBehaviorService;


    /**
     * 游戏协议名
     */
    public static final String GAME_PROTOCOL_FILE_NAME = "海魂网络游戏使用协议.txt";

    /**
     * 游戏登录状态列表前缀
     */
    public static final String GAME_STATUS_PREFIX = "game_status_prefix_";

    /**
     * toeken redis key前缀
     */
    private static final String TOKEN_PREFIX = "token_prefix_";

    /**
     * toeken 登录验证码 key前缀
     */
    private static final String LOGIN_VERIFY_CODE_PREFIX = "login_verify_code_prefix_";

    /**
     * 验证码类型为注册，时效为30分钟
     */
    private static final String REGISTER_VERIFY_CODE_PREFIX = "register_verify_code_prefix_";

    /**
     * redis 游戏协议前缀
     */
    private static final String GAME_PROTOCOL = "game_protocol_prefix_";

    /**
     * redis 普通验证码前缀
     */
    private static final String STANDARD_VERIFY_CODE_PREFIX = "standard_verify_code_prefix_";

    /**
     * 注册验证码失效时间单位(秒)
     **/
    private static final int REGISTER_VERIFY_CODE_TIME_OUT = 1800;

    /**
     * 普通验证码失效时间单位(秒)
     **/
    private static final int STANDARD_VERIFY_CODE_TIME_OUT = 300;

    /**
     * 游戏协议重新加载间隔(秒)
     */
    private static final int GAME_PROTOCOL_TIME_OUT = 604800;

    /**
     * token超时时间(秒)
     */
    public static final int TOKEN_TIME_OUT = 3600;

    @Autowired
    private GameService gameService;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private JedisService jedisService;


    /**
     * 根据手机号获取用户
     *
     * @param mobile 手机号
     * @return 用户实体
     */
    public User findByMobile(String mobile) {
        return mapper.findByMobile(mobile);
    }

    /**
     * 根据用户名获取用户
     *
     * @param userName 用户名
     * @return 用户实体
     */
    public User findByUserName(String userName) {
        try {
            return mapper.findByUserName(userName);
        } catch (Exception e) {
            log.error("findByUserName error ! detail : {}", e.getMessage());
            return null;
        }
    }

    /**
     * 注册账号
     *
     * @return 返回消息
     */
    public Result register(User newUser, Result result) {
        try {
            String verifyCode = newUser.getCode();
            // 2. 如果持有验证码，则是手机
            if (checkVerifyCode(verifyCode, newUser.getUserName(), result, REGISTER_VERIFY_CODE_PREFIX)) return result;
            User user;
            // 验证手机号是否已被绑定账号
            if (StringUtils.isNotBlank(verifyCode)) {
                user = findByMobile(String.valueOf(newUser.getMobile()));
                if (user != null) {
                    // 给予微信等三方渠道放行
                    if (StringUtils.isBlank(newUser.getWechatId()) && StringUtils.isBlank(newUser.getSinaId())) {
                        result.warn("The phone number has been used！");
                        return result;
                    }
                }
                // 将手机号绑定到账号
                newUser.setMobile(Long.valueOf(newUser.getUserName()));
                newUser.setAccountType("1");

            }
            // 3. 验证该用户是否存在？如果存在的话，则返回失败信息。
            user = findByUserName(newUser.getUserName());
            if (user != null) {
                // 放行第三方登录账号
                if (StringUtils.isNotBlank(newUser.getWechatId()) || StringUtils.isNotBlank(newUser.getSinaId())) {
                    BeanUtil.copyPropIgnoreNull(user, newUser);
                    return result;
                }
                // 是否为访客账号
                if (newUser.getCustomerAcc() == null || !newUser.getCustomerAcc().equals(user.getCustomerAcc())) {
                    result.warn("The user has been register！");
                    return result;
                }
            }

            // 4. 对用户的密码进行MD5加密。
            String newPwd = encryptPwd(newUser.getUserName(), newUser.getPwd());
            newUser.setPwd(newPwd);
            newUser.setNickName(newUser.getUserName());

 /*           // 5. 如果持有身份证，则证明需要验证是否为实名用户
            String cardId = newUser.getCardId();
            if (StringUtils.isNotBlank(cardId)) {
                int length = cardId.length();
                if ((length == 15 || length == 18) && newUser.getCardId().matches("^[0-9]*[1-9][0-9]*$")) {
                    //TODO do somethin .. 验证身份证是否伪造
                } else {
                    result.warn("Incorrect Card ID！");
                    return result;
                }
            }*/
            newUser.setCreateTime(new Date());

            // 6. 将用户新建信息存储到数据库。
            save(newUser);
            // 清空验证码
            jedisService.expire(REGISTER_VERIFY_CODE_PREFIX + newUser.getUserName(), -1);

            return result;
        } catch (Exception e) {
            log.error("register User Error Message : " + e.getMessage());
            throw new RuntimeException("regsiter User Error Message :" + e.getMessage());
        }

    }

    /**
     * 用户登录
     */
    public LoginResult login(LoginArgs info) {
        try {
            LoginResult result = ValidateUtil.validate(info, LoginResult.class);
            if (ConstFuncUtil.checkResult(result)) return result;

            // 1. 根据appId查询是游戏否已接入
            Game game = gameService.findById(info.getAppId());
            // 2. 判断是否存在该游戏
            if (game == null) {
                result.notFound("Game Not Found！");
                return result;
            }
            EquipmentChannel ec = equipmentService.findEcByEcId(info.getDuid());

            if (ec == null) {
                result.notFound("Illegal Device!");
                return result;
            }

            // 3. 查询出该用户
            User player = findUserByUserNameOrMobile(info.getUserName());
            if (player == null) {
                result.notFound("User Not Found！");
                return result;
            }
            // 4. 验证登录
            if (StringUtils.isBlank(info.getCode())) {
                // 用户是否来源第三方渠道。
                if (StringUtils.isBlank(info.getOriginType())) {
                    // 不是第三方渠道，验证密码
                    String pwd = encryptPwd(info.getUserName(), info.getPwd());
                    if (!pwd.equals(player.getPwd())) {
                        result.warn("Incorrect Password！");
                        return result;
                    }
                }
            } else {
                //  快速登录，核对验证码
                String key = LOGIN_VERIFY_CODE_PREFIX + info.getUserName();
                String verifyCode = jedisService.get(key);
                if (StringUtils.isBlank(verifyCode) || !info.getCode().equalsIgnoreCase(verifyCode)) {
                    result.warn("Invalid verification Code！");
                    return result;
                }
                // 清空验证码
                jedisService.del(key);
            }

            // 查看当前玩家是否已登录
            String statusKey = GAME_STATUS_PREFIX + info.getAppId();
            String token = jedisService.hget(statusKey, info.getUserName());

            if (StringUtils.isBlank(token) || StringUtils.isBlank(queryUserByToken(token))) {
                // 5. 生成tocken到后台。
                token = getToken(player);
            }

            //记录当前游戏登录状态
            jedisService.hset(statusKey, info.getUserName(), token);

            // 记录玩家登录行为
            UserBehavior userBehavior = new UserBehavior();
            userBehavior.setBehaviorType(UserBehaviorEnum.login.getKey());
            userBehavior.setDescription(UserBehaviorEnum.login.getVal());
            userBehavior.setAppId(game.getAppId());
            userBehavior.setChannelId(ec.getChannelId());
            userBehavior.setTriggerTime(new Date());
            userBehavior.setUserName(info.getUserName());
            userBehaviorService.save(userBehavior);

            // 记录当前token对应的游戏账号与应用
            String key = TOKEN_PREFIX + token;
            JSONObject userInfo = new JSONObject();
            userInfo.put("id", "hh" + player.getId());
            userInfo.put("appId", info.getAppId());
            userInfo.put("userName", info.getUserName());
            userInfo.put("mobile", player.getMobile());
            userInfo.put("duid", info.getDuid());
            // 查询设备初始化

            userInfo.put("channel", ec.getChannelId());

            jedisService.setex(key, TOKEN_TIME_OUT, userInfo.toJSONString());

            Map<String, Object> data = new HashMap<>();
            data.put("userName", player.getUserName());
            data.put("userId", "hh" + player.getId());
            data.put("mobile", player.getMobile());
            data.put("token", token);

            data.put("expire", TOKEN_TIME_OUT);

            result.setData(JSONObject.toJSONString(data));
            return result;
        } catch (Exception e) {
            log.error("login failed error info :" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private User findUserByUserNameOrMobile(@NotBlank(message = "账号不能为空！") String userName) {
        try {
            return mapper.findUserByUserNameOrMobile(userName);
        } catch (Exception e) {
            log.error("findUserByusernameOrMobile error info :" + e.getMessage());
            return null;
        }
    }

    /**
     * 生成token
     *
     * @param player 玩家
     * @return 用户
     */
    private String getToken(User player) {
        return DigestUtils.md5Hex(player.getUserName() + "" + System.currentTimeMillis()).toUpperCase();
    }

    /**
     * 重置密码
     *
     * @return 返回消息
     */
    public Result resetPwd(ResetPwdArgs param, Result result) {
        try {
            String userName = param.getUserName();
            // 验证验证码。
            if (StringUtils.isNotBlank(param.getCode())) {
                if (checkVerifyCode(param.getCode(), userName, result, STANDARD_VERIFY_CODE_PREFIX)) {
                    return result;
                }
            }
            User user = findByUserName(userName);
            if (user == null) {
                result.notFound("User not found！");
                return result;
            }
            String newPwd = param.getNewPwd();
            if (StringUtils.isNotBlank(param.getOldPwd()) && StringUtils.isNotBlank(newPwd)) {
                String oldPwd = param.getOldPwd();
                if (user.getPwd().equals(encryptPwd(userName, oldPwd))) {
                    user.setPwd(encryptPwd(userName, newPwd));
                } else {
                    result.warn("Incorrect Password！");
                    return result;
                }
            } else if (StringUtils.isNotBlank(param.getNewPwd()) && StringUtils.isNotBlank(param.getCode())) {
                user.setPwd(encryptPwd(userName, param.getNewPwd()));
            }
            // 保存修改
            updateSelective(user);

            // 清空验证码
            jedisService.expire(STANDARD_VERIFY_CODE_PREFIX + user.getUserName(), -1);

            return result;
        } catch (Exception e) {
            log.error("reset password error Meaage : " + e.getMessage());
            throw new RuntimeException("reset password error Meaage : " + e.getMessage());
        }
    }

    /**
     * 对密码进行盐值加密
     *
     * @param userName 用户名
     * @param pwd      密码
     * @return 加密后的密码
     * @throws Exception
     */
    private String encryptPwd(String userName, String pwd) throws Exception {
        return MD5.getMD5(pwd + "/" + userName);
    }


    /**
     * 检验验证码
     *
     * @param code   客户端传入的验证码
     * @param mobile 手机号
     * @param type   验证码类型
     * @return 是否无效
     */
    private boolean checkVerifyCode(String code, String mobile, Result result, String type) {
        if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(mobile)) {
            // 检验，验证码是否非法
            String verifyCode = jedisService.get(type + mobile);
            if (StringUtils.isBlank(verifyCode) || !code.equalsIgnoreCase(verifyCode)) {
                result.warn("Invalid Verification Code！");
                return true;
            }
        }
        return false;
    }


    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     * @param status 状态
     * @return 返回消息
     */
    public Result smsVerifyCode(String mobile, String status) {

        Result retResult = new Result();
        /** 随机生成四位数字的验证码 */
        String code = String.valueOf(ConstFuncUtil.generateRandomNumber(4));
        String template = "";
        String key = "";
        int minutes = STANDARD_VERIFY_CODE_TIME_OUT / 60;
        if ("1".equalsIgnoreCase(status)) {
            // 注册用户模板
            template = SmsUtil.SMS_TEMPLATE_REGISTER;
            key = REGISTER_VERIFY_CODE_PREFIX + mobile;
            minutes = REGISTER_VERIFY_CODE_TIME_OUT / 60;

        } else if ("2".equalsIgnoreCase(status)) {
            //  用户登录模板
            template = SmsUtil.SMS_TEMPLATE_LOGIN;
            key = LOGIN_VERIFY_CODE_PREFIX + mobile;
        } else {
            // 普通模板
            template = SmsUtil.SMS_TEMPLATE_RESET_PWD;
            key = STANDARD_VERIFY_CODE_PREFIX + mobile;
        }
        if (jedisService.setnx(key, code) == 0) {
            retResult.warn("Text Message cannot be repeated within " + minutes + " minutes !");
            return retResult;
        } else {
            if ("1".equalsIgnoreCase(status)) {
                // 注册
                jedisService.setex(key, REGISTER_VERIFY_CODE_TIME_OUT, code);
            } else if ("2".equalsIgnoreCase(status)) {
                // 登录
                jedisService.setex(key, STANDARD_VERIFY_CODE_TIME_OUT, code);
            } else {
                jedisService.setex(key, STANDARD_VERIFY_CODE_TIME_OUT, code);
            }
        }
        // 发送短信
        boolean result = SmsUtil.send(code, mobile, template);
        if (!result) {
            jedisService.del(key);
            retResult.warn("Send Text Message failed !");
        }
        return retResult;
    }

    /**
     * 登出账号
     *
     * @param token token
     */
    public Result logout(String token) {
        try {
            Result result = new Result();
            // 获取到token对应的用户信息
            String userInfo = queryUserByToken(token);

            if (StringUtils.isBlank(userInfo)) {
                result.notFound("invalid token ！");
                return result;
            }
            JSONObject json = JSONObject.parseObject(userInfo);
            String appId = json.getString("appId");
            String userName = json.getString("userName");

            // 从游戏状态列表移除
            jedisService.hdel(GAME_STATUS_PREFIX + appId, userName);
            // 移除token对应的用户信息
            jedisService.del(TOKEN_PREFIX + token);
            return result;
        } catch (Exception e) {
            log.error("User logout error ！ detail : " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 根据token查询用户信息
     *
     * @param token token
     * @return json格式的用户信息
     */
    public String queryUserByToken(String token) {
        String userInfo = jedisService.get(TOKEN_PREFIX + token);
        jedisService.expire(TOKEN_PREFIX + token, 3600);
        return userInfo;
    }


    /**
     * 快速注册用户
     *
     * @return 返回信息
     */
    public LoginArgs quickRegister(String duid) {
        try {
            User user = new User();
            Map<String, String> keyPair = generatorNameAndPwd();
            String userName = keyPair.get("userName");
            String pwd = keyPair.get("pwd");

            user.setUserName(userName);
            user.setPwd(encryptPwd(userName, pwd));
            user.setCreateTime(new Date());
            user.setCustomerAcc(userName);
            user.setNickName(userName);
            EquipmentChannel ec = equipmentService.findEcByEcId(duid);
            Equipment equipment = equipmentService.findByDuid(ec.getDuid());
            user.setEquipmentId(equipment.getId());

            LoginArgs vo = new LoginArgs();
            vo.setUserName(userName);
            vo.setPwd(pwd);


            save(user);

            vo.setUserId("hh" + findByUserName(vo.getUserName()).getId());
            return vo;
        } catch (Exception e) {
            log.error("quickregsiter error ! detail : {} ", e.getMessage());
            throw new RuntimeException("quickregsiter error ! detail : " + e.getMessage());
        }
    }

    /**
     * 生成账号，密码
     *
     * @return
     */
    public Map<String, String> generatorNameAndPwd() {
        String userName = "hh" + ConstFuncUtil.generateRandomNumber(6);
        while (findByUserName(userName) != null) {
            userName = "hh" + ConstFuncUtil.generateRandomNumber(6);
        }
        String pwd = ConstFuncUtil.getUUID().substring(0, 8);
        Map<String, String> keyPair = new HashMap<>();
        keyPair.put("userName", userName);
        keyPair.put("pwd", pwd);
        return keyPair;
    }


    public User findUserByWeChatIdOrQqId(User user) {
        try {
            return mapper.findUserByWeChatIdorSinaId(user);
        } catch (Exception e) {
            log.error("find User By WeChatId Or QqId Error ! detail : " + e.getMessage());
        }
        return null;
    }

    /**
     * 获取游戏协议
     *
     * @return
     */
    public Result getGameProtocol() {
        try {
            // 每一行都写入到List中
            String gameProtocol = jedisService.get(GAME_PROTOCOL);
            Result result = new Result();
            if (StringUtils.isBlank(gameProtocol)) {
                ClassPathResource resource = new ClassPathResource("/static/" + GAME_PROTOCOL_FILE_NAME);
                gameProtocol = StreamUtil.readText(resource.getInputStream());

                // 加入缓存
                jedisService.setex(GAME_PROTOCOL, GAME_PROTOCOL_TIME_OUT, gameProtocol);
            }
            result.setData(gameProtocol);
            return result;
        } catch (Exception e) {
            log.error("Get Game Protocol Error ! detail : " + e.getMessage());
            throw new RuntimeException("Get Game Protocol Error ! detail : " + e.getMessage());
        }
    }


    /**
     * 进行实名认证
     *
     * @param param 实名参数
     * @return 验证结果
     */
    public Result checkRealName(RealNameArgs param) {
        try {
            Result result = new Result();
            String userInfo = queryUserByToken(param.getToken());
            if (StringUtils.isBlank(userInfo)) {
                result.warn("invalid token !");
                return result;
            }
            JSONObject json = JSONObject.parseObject(userInfo);
            String userName = json.getString("userName");
            User user = findByUserName(userName);
            if (user == null) {
                result.notFound("not found user !");
                return result;
            }
            user.setIsRealName("1");
            user.setCardId(param.getCardId());
            user.setRealName(param.getRealName());
            updateSelective(user);
            return result;
        } catch (Exception e) {
            log.error("check realName error detail : {} ", e.getMessage());
            throw new RuntimeException("check realName error detail : {} " + e.getMessage());
        }
    }

    /**
     * 绑定手机
     *
     * @param args 绑定手机
     */
    public Result bindMobile(BindMobileArgs args) {
        try {
            Result result = new Result();
            String userInfoJson = queryUserByToken(args.getToken());
            if (StringUtils.isBlank(userInfoJson)) {
                result.notFound("invalid token !");
            }
            JSONObject userInfo = JSONObject.parseObject(userInfoJson);
            String userName = userInfo.getString("userName");


            User user = findByUserName(userName);

            // 如果根据手机号找出了则表示被绑定
            User checkUser = findByMobile(args.getMobile());

            // 如果手机号已被绑定 并且与 toeken持有用户账户不一致
            if (checkUser != null && !checkUser.getUserName().equals(user.getUserName())) {
                result.warn("The phone has been bundled !");
                return result;
            }

            // 检查手机验证码
            if (checkVerifyCode(args.getSmsVerifyCode(), args.getMobile(), result, STANDARD_VERIFY_CODE_PREFIX))
                return result;

            // 用户是否已绑定手机，如果绑定则请先解绑
            if (user.getMobile() != null && args.getMobile().equals(user.getMobile())) {
                result.ok("successful !");
            }

            // 设置手机
            user.setMobile(Long.valueOf(args.getMobile()));

            // 密钥加盐
            user.setPwd(encryptPwd(userName, args.getPwd()));

            // 更新用户
            updateSelective(user);

            // 清空验证码
            String key = STANDARD_VERIFY_CODE_PREFIX + args.getMobile();
            jedisService.expire(key, -1);
            return result;
        } catch (Exception e) {
            log.error("bind mobile error! detail : {}", e.getMessage());
            throw new RuntimeException("bind mobile error! detail : " + e.getMessage());
        }
    }

    /**
     * 手机解绑
     *
     * @param code  验证码
     * @param token token
     * @return 结果
     */
    public Result unBindMobile(String code, String token) {
        Result result = new Result();
        String userInfoJson = queryUserByToken(token);

        if (StringUtils.isBlank(userInfoJson)) {
            result.notFound("invalid token !");
            return result;
        }

        JSONObject userInfo = JSONObject.parseObject(userInfoJson);
        String mobile = userInfo.getString("mobile");

        // 查看token中是否存在手机
        if (StringUtils.isBlank(mobile)) {
            result.notFound("The user has not bound the phone ！");
            return result;
        }

        User user = findByMobile(mobile);

        if (user == null) {
            result.notFound("The user has not bound the phone ！");
            return result;
        }

        // 检查验证码
        if (checkVerifyCode(code, mobile, result, STANDARD_VERIFY_CODE_PREFIX)) return result;

        // 更新用户信息
        Integer ret = updateMobile(null, user.getId());
        if (!ret.equals(1)) {
            result.error("unBindMobile failed ！");
            return result;
        }

        // 清空验证码

        String key = STANDARD_VERIFY_CODE_PREFIX + mobile;
        jedisService.expire(key, -1);

        return result;
    }

    /**
     * 根据用户名和手机号查询用户
     *
     * @param userName 用户名
     * @param mobile   手机号
     * @return 用户信息
     */
    public User findUserByUserNameAndMobile(String userName, String mobile) {
        try {
            return mapper.findUserByUserNameAndMobile(userName, mobile);
        } catch (Exception e) {
            log.error("findUserByUserNameAndMobile error detail : {} userNmae： {} mobile： {}", e.getMessage(), userName, mobile);
        }
        return null;
    }

    public Integer updateMobile(String mobile, Integer id) {
        try {
            return mapper.updateMobile(mobile, id);
        } catch (Exception e) {
            log.error("updateMobile error ! detail : {} ", e.getMessage());
            return 0;
        }
    }

}
