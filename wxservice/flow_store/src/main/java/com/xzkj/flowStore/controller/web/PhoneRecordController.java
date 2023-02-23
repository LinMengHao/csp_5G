package com.xzkj.flowStore.controller.web;


import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.xzkj.flowStore.controller.web.bo.SavePhoneBo;
import com.xzkj.flowStore.controller.web.bo.SendCodeBo;
import com.xzkj.flowStore.entity.PhoneRecord;
import com.xzkj.flowStore.service.PhoneRecordService;
import com.xzkj.flowStore.utils.MsgBean;
import com.xzkj.flowStore.utils.RedisUtil;
import com.xzkj.flowStore.utils.SmsUtil;
import com.xzkj.flowStore.utils.wx.AccessToken;
import com.xzkj.flowStore.utils.wx.Ticket;
import com.xzkj.flowStore.utils.wx.WXShareVo;
import com.xzkj.flowStore.utils.wx.WXUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 手机记录表 前端控制器
 * </p>
 *
 * @author dashan
 * @since 2020-09-27
 */
@Slf4j
@Api(tags = "002.手机记录")
@CrossOrigin
@RestController
@RequestMapping("/web/phone")
public class PhoneRecordController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PhoneRecordService phoneRecordService;

    @PostMapping("/sendCode")
    @ApiOperation("发送验证码")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "发送验证码", response = MsgBean.class)})
    public MsgBean sendCode(
            @RequestBody SendCodeBo bo) {

        if (bo == null) {
            return MsgBean.error("请提交正确的参数！");
        } else if (StrUtil.isBlank(bo.getPhone())) {
            return MsgBean.error("手机号不能为空！");
        } else if (!PhoneUtil.isPhone(bo.getPhone())) {
            return MsgBean.error("手机号不正确！");
        }

        int code = RandomUtil.randomInt(1000, 9999);

        boolean sendResult = SmsUtil.send(SmsUtil.SmsType.Login, bo.getPhone(), code + "");
        if (!sendResult) {
            return MsgBean.error("短信发送失败，请重试！");
        }

        boolean result = redisUtil.set("vpn_phone_code_" + bo.getPhone(), code + "", 10 * 60L);
        if (result) {
            return MsgBean.ok("发送成功！");
        }
        return MsgBean.error("发送失败，请重试！");
    }


    @PostMapping("/submit")
    @ApiOperation("提价手机号")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "提价手机号", response = MsgBean.class)})
    public MsgBean sasubmitve(
            @RequestBody SavePhoneBo bo) {
        if (bo == null) {
            return MsgBean.error("请提交正确的参数！");
        } else if (StrUtil.isBlank(bo.getPhone())) {
            return MsgBean.error("手机号不能为空！");
        } else if (!PhoneUtil.isPhone(bo.getPhone())) {
            return MsgBean.error("手机号不正确！");
        } else if (StrUtil.isBlank(bo.getCode())) {
            return MsgBean.error("验证码不能为空！");
        }

        String code = redisUtil.get("vpn_phone_code_" + bo.getPhone(), String.class);

        if (StrUtil.isBlank(code)) {
            return MsgBean.error("验证码不存在！");
        } else if (!code.equals(bo.getCode())) {
            return MsgBean.error("验证码不正确！");
        }
        PhoneRecord phoneRecord = phoneRecordService.getByPhone(bo.getPhone());
        if (phoneRecord == null) {
            phoneRecord = new PhoneRecord();
            phoneRecord.setPhone(bo.getPhone());
            phoneRecordService.save(phoneRecord);
        }
        return MsgBean.ok("提交成功！");
    }

    @GetMapping("/wxShare")
    @ApiOperation("微信分享")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "微信分享", response = MsgBean.class)})
    public MsgBean wxShare(
            @ApiParam("分享url") @RequestParam(value = "url", required = true) String url) {
        if (StrUtil.isBlank(url)) {
            return MsgBean.error("请提供要分享的链接！");
        }
        String accessToken = null;

        String key = "access_token";

        Object redisAccessToken = redisUtil.get(key);
        if (redisAccessToken != null) {
            accessToken = redisAccessToken.toString();
        }
        if (StrUtil.isBlank(accessToken)) {
            AccessToken ac = WXUtil.getAccessToken();
            if (ac != null) {
                accessToken = ac.getToken();
                boolean result = redisUtil.set(key, ac.getToken(), ac.getExpiresIn() - 1000);
                log.info("重新获取accessToken,更新redis结果：{}", result);
            }
        }

        String ticket = null;
        String ticketKey = "wx_ticket";

        Object redisTicket = redisUtil.get(ticketKey);
        if (redisTicket != null) {
            ticket = redisTicket.toString();
        }

        if (StrUtil.isBlank(ticket)) {
            Ticket tt = WXUtil.getTicket(accessToken);
            if (tt != null) {
                ticket = tt.getTicket();
                boolean result = redisUtil.set(ticketKey, tt.getTicket(), tt.getExpiresIn() - 1000);
                log.info("重新获取ticket,更新redis结果：{}", result);
            }
        }

        if (StrUtil.isBlank(ticket)) {
            return MsgBean.error("分享获取ticket失败，请重试！");
        }
        WXShareVo shareVo = WXUtil.getShareSign(ticket, url);
        return MsgBean.ok().putData(shareVo);
    }
}

