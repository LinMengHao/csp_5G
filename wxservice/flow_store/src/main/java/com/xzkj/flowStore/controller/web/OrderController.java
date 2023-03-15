package com.xzkj.flowStore.controller.web;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xzkj.flowStore.common.Constant;
import com.xzkj.flowStore.controller.web.bo.MyOrderBo;
import com.xzkj.flowStore.controller.web.bo.NewOrderBo;
import com.xzkj.flowStore.controller.web.vo.OrderVo;
import com.xzkj.flowStore.entity.Order;
import com.xzkj.flowStore.entity.Product;
import com.xzkj.flowStore.entity.User;
import com.xzkj.flowStore.service.OrderService;
import com.xzkj.flowStore.service.ProductService;
import com.xzkj.flowStore.service.UserService;
import com.xzkj.flowStore.utils.MsgBean;
import com.xzkj.flowStore.utils.OrderUtil;
import com.xzkj.flowStore.utils.TokenUtil;
import com.xzkj.flowStore.utils.wechat.utils.WXPayConstants;
import com.xzkj.flowStore.utils.wechat.utils.WXPayUtil;
import com.xzkj.flowStore.utils.wx.WXUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author dashan
 * @since 2020-10-02
 */
@Slf4j
@Api(tags = "005.订单")
@RestController
@RequestMapping("web/order")
public class OrderController extends BaseController {


    private String apiUrl = "http://vpn-api.mbqingyun.com";

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;


    @PostMapping("/myOrders")
    @ApiOperation("首页商品列表")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "首页商品列表", response = Order.class)})
    public MsgBean myOrders(@ApiParam("token") @RequestHeader(value = "token", required = false) String token,
                            @RequestBody MyOrderBo bo
    ) {
        Long userId = TokenUtil.getUIDFromToken(token);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        if (bo.getSyncState() != null) {
            if (bo.getSyncState() == 0 || bo.getSyncState() == 1 || bo.getSyncState() == 3) {
                wrapper.in("sync_state", 0, 1, 3);
            } else if (bo.getSyncState() == 2) {
                wrapper.eq("sync_state", 2);
            }
        }

        wrapper.eq("state", 1);
        wrapper.orderByDesc("id");
        List<Order> list = orderService.list(wrapper);


        List<OrderVo> voList = Lists.newArrayList();
        for (Order order : list) {
            OrderVo vo = new OrderVo();
            BeanUtil.copyProperties(order, vo);
            Product product = productService.getById(order.getProductId());
            vo.setProductPic(product.getPic());
            vo.setProductService(product.getService());
            voList.add(vo);
        }
        return MsgBean.ok().putData(voList);
    }


    @PostMapping("/newOrder")
    @ApiOperation("下单接口")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "首页商品列表", response = MsgBean.class)})
    public MsgBean newOrder(@ApiParam("token") @RequestHeader(value = "token", required = false) String token,
                            HttpServletRequest request,
                            @RequestBody NewOrderBo bo
    ) {
        if (StrUtil.isBlank(token)) {
            return MsgBean.error("token为空！");
        }
        if (bo == null) {
            return MsgBean.error("请求参数不能为空！");
        } else if (bo.getProductId() == null || bo.getProductId() <= 0) {
            return MsgBean.error("产品id不能为空！");
        } else if (bo.getAmount() <= 0) {
            return MsgBean.error("支付金额不能为空！");
        } else if (bo.getPayType() == null || bo.getPayType() < 1 || bo.getPayType() > 3) {
            return MsgBean.error("支付方式不正确，1-支付宝，2-微信");
        }

        Long userId = TokenUtil.getUIDFromToken(token);
        User user = userService.getById(userId);
        if (user == null || user.getState() == -1) {
            return MsgBean.error("用户不正确！");
        }

        Product product = productService.getById(bo.getProductId());
        if (product == null || product.getState() == -1) {
            return MsgBean.error("商品不存在！");
        } else if (product.getState() == 0) {
            return MsgBean.error("商品还未上架！");
        }
        if (product.getAmount().equals(bo.getAmount())) {
            return MsgBean.error("价格不一致！");
        }

        Order order = new Order();
        order.setAmount(product.getAmount());
        order.setProductId(product.getId());
        order.setOrderName(product.getTitle());
        order.setOrderNo(OrderUtil.createOrderNo(userId));
        order.setUserId(userId);
        order.setPayType(bo.getPayType());
        order.setIp(getIp(request));

        boolean save = orderService.save(order);
        if (!save) {
            return MsgBean.error("下单失败，请重试！");
        }

        //TODO 测试1分
        order.setAmount(1);

        switch (bo.getPayType()) {
            case 1:
                return MsgBean.error("暂不支持！");
            case 2:
                //微信支付
                Map<String, String> result = WXUtil.weixinMinPay(order, getIp(request), user.getMinOpenId());
                //生成支付订单
                if (result != null) {
                    //生成签名
                    Map<String, String> parameterMap = Maps.newHashMap();
                    parameterMap.put("appId", Constant.WX_MIN_APP_ID);
                    parameterMap.put("timeStamp", System.currentTimeMillis() / 1000 + "");
                    parameterMap.put("nonceStr", WXPayUtil.generateNonceStr());
                    parameterMap.put("package", "prepay_id=" + result.get("prepay_id"));
                    parameterMap.put("signType", "MD5");
                    parameterMap.put("paySign", WXPayUtil.generateSignature(parameterMap, Constant.appKey, WXPayConstants.SignType.MD5));
                    return MsgBean.ok().putData(parameterMap);
                }
                break;
            case 3:
                Map<String, String> result2 = WXUtil.weixinH5Pay(order, getIp(request));
                //生成支付订单
                if (result2 != null && result2.containsKey("mweb_url")) {
                    String mwebUrl = result2.get("mweb_url");
                    return MsgBean.ok().putData(mwebUrl);
                } else {
                    log.info("微信支付失败{}", result2);
                    return MsgBean.error("支付失败!");
                }
        }
        return MsgBean.error("生成订单失败！");
    }


    /**
     * 微信支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/weixin/notify")
    public void weixinNotify(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> paramMap = null;
        Order userOrder = null;
        try {
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String result = new String(outSteam.toByteArray(), "utf-8");// 获取微信调用我们notify_url的返回信息

            log.info("微信支付回调: {}", result);
            paramMap = WXPayUtil.xmlToMap(result);

            if (paramMap != null && paramMap.containsKey("result_code") && paramMap.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
                if (WXPayUtil.isSignatureValid(paramMap, Constant.appKey)) {

                    String orderNo = paramMap.get("out_trade_no");
                    String outRefundNo = paramMap.get("out_refund_no");
                    if (StringUtils.isNotBlank(outRefundNo)) {
                        log.info("微信退款通知：{}", orderNo);
                    } else if (orderNo != null) {
                        String outTradeNo = paramMap.get("transaction_id").toString();
                        Long payMoney = Long.parseLong(paramMap.get("total_fee").toString());
                        boolean updateResult = orderService.paySuccess(orderNo, outTradeNo, payMoney.intValue());
                        log.info("修改订单结果 支付：{}", updateResult);
                        if (updateResult) {
                            output(response, weixinPaySuccess());
                        }
                    }

                }
            } else {
                log.info("微信签名错误{}", result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("微信支付回调 处理异常 ：", ex);
        } finally {
            //TODO 新增日志
        }
        output(response, weixinPayFail());
    }

    protected void output(HttpServletResponse response, String msg) {
        try {
            PrintWriter writer = response.getWriter();
            writer.print(msg);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String weixinPaySuccess() {
        return setXML("SUCCESS", "OK");
    }


    private String weixinPayFail() {
        return setXML("FAIL", "FAIL");
    }

    public static String setXML(String success, String ok) {
        return "<xml><return_code><![CDATA[" + success + "]]></return_code><return_msg><![CDATA[" + ok + "]]></return_msg></xml>";
    }


    @PostMapping("/xinJi/OrderStatus")
    public String xinJiNotify(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> paramMap = null;
        Order userOrder = null;
        try {
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String result = new String(outSteam.toByteArray(), "utf-8");// 获取微信调用我们notify_url的返回信息
            log.info("星际通知接口: {}", result);
            // {"parJson":{"expire_time":1603190247906,"orderStat":"10000","userid":"08012020071847070039"},"outTradeNo":"21"}

            JSONObject jsonObject = JSONUtil.parseObj(result);

            String orderStat = jsonObject.getJSONObject("parJson").getStr("orderStat");

            if ("10000".equals(orderStat)) {
                log.info("星际通知接口 充值成功", result);
                Long orderId = jsonObject.getLong("outTradeNo");
                Order order = orderService.getById(orderId);
                if (order != null && order.getSyncState() != 2) {
                    Order update = new Order();
                    update.setSyncState(2);
                    update.setId(orderId);
                    update.setSyncTime(LocalDateTime.now());
                    orderService.updateById(update);
                    return "{\"code\":\"1000\",\"message\":\"成功\"}";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("星际通知接口 e={}", e);
        }
        return "{\"code\":\"10002\",\"message\":\"失败\"}";
    }
}

