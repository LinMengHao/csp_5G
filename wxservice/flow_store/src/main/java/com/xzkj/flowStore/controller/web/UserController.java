package com.xzkj.flowStore.controller.web;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.xzkj.flowStore.common.Constant;
import com.xzkj.flowStore.controller.web.bo.*;
import com.xzkj.flowStore.controller.web.vo.UserRightsVo;
import com.xzkj.flowStore.controller.web.vo.UserVo;
import com.xzkj.flowStore.entity.Product;
import com.xzkj.flowStore.entity.User;
import com.xzkj.flowStore.entity.UserRights;
import com.xzkj.flowStore.service.ProductService;
import com.xzkj.flowStore.service.UserRightsService;
import com.xzkj.flowStore.service.UserService;
import com.xzkj.flowStore.utils.*;
import com.xzkj.flowStore.utils.bo.MinInfoBo;
import com.xzkj.flowStore.utils.wx.WXDecryptDataUtil;
import com.xzkj.flowStore.utils.wx.WXUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author dashan
 * @since 2020-10-02
 */
@Api(tags = "004.用户")
@Slf4j
@RestController
@RequestMapping("/web/user")
public class UserController extends BaseController {


    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserRightsService userRightsService;

    @Autowired
    private ProductService productService;


    @PostMapping("/xj/sendCode")
    @ApiOperation("星际发送验证码")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "星际发送验证码", response = MsgBean.class)})
    public MsgBean xjSendCode(
            @RequestHeader(value = "token", required = false) String token,
            @RequestBody XjSmsCodeBo bo) {
        if (StrUtil.isBlank(token)) {
            return MsgBean.error(505, "token不能为空！");
        }

        Long userId = TokenUtil.getUIDFromToken(token);
        if (userId == null || userId <= 0) {
            return MsgBean.error(505, "token不正确！");
        }

        User user = userService.getById(userId);
        if (user == null) {
            return MsgBean.error(505, "token不正确！");
        }

        if (bo == null) {
            return MsgBean.error("请求参数不能为空！");
        } else if (!PhoneUtil.isPhone(bo.getPhone())) {
            return MsgBean.error("手机号不正确！");
        }

        if (!user.getPhone().equals(bo.getPhone())) {
            return MsgBean.error("手机和当前用户绑定的不一致！");
        }

        boolean checkPhone = XingJiUtil.checkPhone(bo.getPhone());
        if (checkPhone) {
            return MsgBean.error("该手机号已经在星际注册过了！");
        }

        boolean sendCodeResult = XingJiUtil.getSMSCode(bo.getPhone());
        if (sendCodeResult) {
            return MsgBean.ok("发送成功！");
        }
        return MsgBean.error("发送失败，请重试！");
    }


    @PostMapping("/xj/register")
    @ApiOperation("注册星际用户")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "注册星际用户", response = MsgBean.class)})
    public MsgBean xjRegister(
            HttpServletRequest request,
            @RequestHeader(value = "token", required = false) String token,
            @RequestBody XjRegisterBo bo) {

        if (bo == null) {
            return MsgBean.error("请求参数不能为空！");
        } else if (!PhoneUtil.isPhone(bo.getPhone())) {
            return MsgBean.error("手机号不正确！");
        } else if (StrUtil.isBlank(bo.getCode())) {
            return MsgBean.error("验证码不能为空！");
        } else if (StrUtil.isBlank(bo.getPassword())) {
            return MsgBean.error("密码不能为空！");
        } else if (!checkPassword(bo.getPassword())) {
            return MsgBean.error("请设置6位以上字母和数字的密码！");
        }

        if (StrUtil.isBlank(token)) {
            return MsgBean.error(505, "token不能为空！");
        }

        Long userId = TokenUtil.getUIDFromToken(token);
        if (userId == null || userId <= 0) {
            return MsgBean.error(505, "token不正确！");
        }

        User user = userService.getById(userId);
        if (user == null) {
            return MsgBean.error(505, "token不正确！");
        }

        XingJiUtil.RegisterUserBo registerUserBo = XingJiUtil.registerUser(bo.getPhone(), bo.getCode(), bo.getPassword(), getIp(request));

        if (registerUserBo == null) {
            return MsgBean.error("请求失败，请重试！");
        } else if (!registerUserBo.getResult()) {
            return MsgBean.error(registerUserBo.getMessage());
        } else if (StrUtil.isBlank(registerUserBo.getUserId())) {
            return MsgBean.error("注册失败，请重试！");
        }


        User update = new User();
        update.setId(user.getId());
        update.setXjUserId(registerUserBo.getUserId());

        boolean updateResut = userService.updateById(update);

        if (updateResut) {
            return MsgBean.ok("注册成功！");
        }
        return MsgBean.error("注册失败，请重试！");
    }


    public static boolean checkPassword(String password) {

        if (StrUtil.isBlank(password)) {
            return false;
        } else if (password.length() < 6) {
            return false;
        }

        boolean isZiMu = false;
        boolean isNum = false;
        for (char c : password.toCharArray()) {
            if (c >= '0' && c <= '9') {
                isNum = true;
            } else if (c >= 'a' && c <= 'z') {
                isZiMu = true;
            } else if (c >= 'A' && c <= 'Z') {
                isZiMu = true;
            }
        }

        return isZiMu && isNum;
    }

    @PostMapping("/sendCode")
    @ApiOperation("发送验证码")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "发送验证码", response = MsgBean.class)})
    public MsgBean wxMinUpdate(@RequestBody SmsCodeBo bo) {

        if (bo == null) {
            return MsgBean.error("请求参数不能为空！");
        } else if (!PhoneUtil.isPhone(bo.getPhone())) {
            return MsgBean.error("手机号不正确！");
        } else if (bo.getType() == null) {
            return MsgBean.error("短信类型不正确！");
        }

        Integer code = RandomUtil.randomInt(1000, 10000);

        boolean sendResult = false;
        switch (bo.getType()) {
            case 1:
                sendResult = SmsUtil.send(SmsUtil.SmsType.Login, bo.getPhone().trim(), code.toString());
                if (sendResult) {
                    redisUtil.set("vpn_login_code_" + bo.getPhone().trim(), code.toString(), 1200L);
                    return MsgBean.ok("发送成功！");
                }
            case 2:
                break;

            case 3:
                break;
            default:
                return MsgBean.error("类型未匹配！");
        }
        return MsgBean.error("发送失败，请重试！");
    }

    @PostMapping("/login")
    @ApiOperation("登陆")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "登陆", response = MsgBean.class)})
    public MsgBean login(
            HttpServletRequest request,
            @RequestBody LoginBo bo) {

        if (bo == null) {
            return MsgBean.error("请求参数不能为空！");
        } else if (!PhoneUtil.isPhone(bo.getPhone())) {
            return MsgBean.error("手机号不正确！");
        } else if (StrUtil.isBlank(bo.getCode())) {
            return MsgBean.error("验证码不能为空！");
        }


        String redisCode = redisUtil.get("vpn_login_code_" + bo.getPhone().trim(), String.class);
        if (StrUtil.isBlank(redisCode)) {
            return MsgBean.error("验证码已失效！");
        }
        if (!redisCode.equals(bo.getCode().trim())) {
            return MsgBean.error("验证码不正确！");
        }

        User user = userService.getByPhone(bo.getPhone().trim());
        if (user == null) {
            user = new User();
            user.setPhone(bo.getPhone());
            user.setNick(userService.getAutoNick());
            user.setIp(getIp(request));
            boolean save = userService.save(user);
            if (save) {
                String token = TokenUtil.makeToken(user);
                return MsgBean.ok().putData(token);
            }
        } else {
            String token = TokenUtil.makeToken(user);
            return MsgBean.ok().putData(token);
        }
        return MsgBean.error("登陆失败，请重试！");
    }


    @PostMapping("/getInfo")
    @ApiOperation("根据token获取用户信息")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "根据token获取用户信息", response = User.class)})
    public MsgBean getInfoByToken(
            @RequestHeader(value = "token", required = false) String token) {
        Long userId = TokenUtil.getUIDFromToken(token);
        User user = userService.getById(userId);
        if (user == null || user.getState() == -1) {
            return MsgBean.error(505, "用户不存在！");
        }
        user.setPassword("");
        if (StrUtil.isBlank(user.getCover())) {
            user.setCover("http://file.fangao.cc/1583748839598834");
        }

        //查询会员权益


        LocalDateTime now = LocalDateTime.now();
        QueryWrapper<UserRights> wrapper = new QueryWrapper<>();
        wrapper.gt("end_date", now);
        wrapper.eq("state", 0);
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("end_date", "id");
        List<UserRights> list = userRightsService.list(wrapper);

        List<UserRightsVo> voList = Lists.newArrayList();

        for (UserRights ur : list) {
            UserRightsVo vo = new UserRightsVo();
            vo.setProductId(ur.getProductId());

            Product product = productService.getById(ur.getProductId());
            vo.setProductTitle(product.getTitle());
            vo.setStartTime(LocalDateTimeUtil.format(ur.getStartDate(), DatePattern.NORM_DATETIME_PATTERN));
            vo.setEndTime(LocalDateTimeUtil.format(ur.getEndDate(), DatePattern.NORM_DATETIME_PATTERN));
            voList.add(vo);
        }

        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user, userVo);
        userVo.setUserRightsList(voList);
        return MsgBean.ok().putData(userVo);
    }


    @PostMapping("/wxMinUpdate")
    @ApiOperation("微信小程序同步用户昵称和头像")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "微信小程序同步用户昵称和头像", response = MsgBean.class)})
    public MsgBean wxMinUpdate(
            @RequestHeader(value = "token", required = false) String token,
            @RequestBody WxMinUpdateInfoBo bo) {
        if (bo == null) {
            return MsgBean.error("请提供用户信息！");
        }
        if (StringUtils.isBlank(bo.getCover())) {
            return MsgBean.error("用户头像不能为空！");
        } else if (StrUtil.isBlank(bo.getNick())) {
            return MsgBean.error("用户昵称不能为空！");
        }
        Long userId = TokenUtil.getUIDFromToken(token);
        User user = userService.getById(userId);
        if (user == null || user.getState() == -1) {
            return MsgBean.error(505, "用户不存在！");
        }

        User update = new User();
        update.setNick(bo.getNick());
        update.setCover(bo.getCover());
        update.setId(userId);
        boolean save = userService.updateById(update);
        if (save) {
            return MsgBean.ok("操作成功！");
        }
        return MsgBean.error("操作失败，请重试！");
    }

    @PostMapping("/wxMinUpdatePhone")
    @ApiOperation("微信小程序获取手机号")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "微信小程序获取手机号", response = MsgBean.class)})
    public MsgBean wxMinUpdatePhone(HttpServletRequest request, @RequestBody UpdatePhoneBo bo) {
        if (bo == null) {
            return MsgBean.error("请提供用户信息！");
        }
        if (StrUtil.isBlank(bo.getPhoneData())) {
            return MsgBean.error("手机号加密数据不能为空！");
        } else if (StrUtil.isBlank(bo.getIv())) {
            return MsgBean.error("iv不能为空！");
        } else if (StrUtil.isBlank(bo.getOpenId())) {
            return MsgBean.error("openId不能为空！");
        }

        String sessionKey = redisUtil.get("wx_session_key_" + bo.getOpenId(), String.class);
        if (StrUtil.isBlank(sessionKey)) {
            return MsgBean.error("sessionKey不存在！");
        }
        log.info("微信小程序获取手机号 bo={},sessionKey={}", bo, sessionKey);
        String result = WXDecryptDataUtil.decryptData(bo.getPhoneData(), sessionKey, bo.getIv());
        log.info("手机号解密结果：phoneData = {} ,ivData={},result={}", bo.getPhoneData(), bo.getIv(), result);
        if (StrUtil.isBlank(result)) {
            return MsgBean.error("解析失败");
        }

        String phone = "";
        JSONObject phoneJson = JSONUtil.parseObj(result);
        if (phoneJson != null && phoneJson.containsKey("phoneNumber")) {
            phone = phoneJson.getStr("phoneNumber");
            if (StrUtil.isBlank(phone)) {
                return MsgBean.error("该用户未绑定手机号！");
            }
        }

        User user = userService.getByPhone(phone);

        if (user == null) {

            //创建新账号
            user = new User();
            String nick = userService.getAutoNick();
            user.setNick(nick);
            user.setPhone(phone);
            user.setMinOpenId(bo.getOpenId());
            user.setUnionId(bo.getUnionId());
            user.setMinSessionKey(sessionKey);
            user.setIp(getIp(request));

            boolean saveResult = userService.save(user);
            if (saveResult) {
                String token = genTokenForMin(user);
                return MsgBean.ok().putData(token);
            }
        } else {
            User update = new User();
            update.setId(user.getId());
            update.setMinOpenId(bo.getOpenId());
            update.setMinSessionKey(sessionKey);
            update.setUnionId(bo.getUnionId());
            userService.updateById(update);
            String token = genTokenForMin(user);
            return MsgBean.ok().putData(token);
        }
        return MsgBean.error("操作失败，请重试！");
    }


    @PostMapping("/wxMinLogin")
    @ApiOperation("微信小程序登陆")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "微信小程序登陆", response = MsgBean.class)})
    public MsgBean wxMinLogin(@RequestBody WxLoginBo bo) {
        if (bo == null) {
            return MsgBean.error("微信授权登录码不能为空！");
        }
        if (StringUtils.isBlank(bo.getCode())) {
            return MsgBean.error("微信授权登录码不能为空！");
        }

        MinInfoBo minInfoBo = WXUtil.getMinInfoByLoginCode(Constant.WX_MIN_APP_ID, Constant.WX_MIN_APP_SECRET, bo.getCode());
        if (minInfoBo == null || StrUtil.isBlank(minInfoBo.getOpenid())) {
            return MsgBean.error("微信未成功授权，请重试！");
        }

        MsgBean msg = new MsgBean();
        msg.putData("openId", minInfoBo.getOpenid());
        msg.putData("unionId", minInfoBo.getUnionid());
        redisUtil.set("wx_session_key_" + minInfoBo.getOpenid(), minInfoBo.getSession_key(), 30 * 24 * 60 * 60L);
        return msg;
    }

    public String genTokenForMin(User users) {
        String token = TokenUtil.makeToken(users);
        return token;
    }


}

