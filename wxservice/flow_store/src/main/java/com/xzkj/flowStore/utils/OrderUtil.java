package com.xzkj.flowStore.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 生成订单号
 */
public class OrderUtil {


    /**
     * 生成订单号，10为业务号,购买课程
     *
     * @param userId
     * @return
     */
    public static String createOrderNo(Long userId) {
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userIdNo = String.format("%010d", userId);
        String random = getRandom(4);
        return new StringBuilder().append(data).append(random).append(userIdNo).append("01").toString();
    }

    /**
     * 生成用户充值订单号，10 为业务号
     *
     * @param userId
     * @return
     */
//    public static String createRechargeNo(Long userId) {
//        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//        String userIdNo = String.format("%010d", userId);
//        String random = getRandom(4);
//        return new StringBuilder().append(data).append(random).append(userIdNo).append("10").toString();
//    }

    /**
     * 生成订单号，02为业务号，20-代表优惠卷订单
     *
     * @param userId
     * @return
     */
    public static String createCouponOrderNo(Long userId) {
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userIdNo = String.format("%010d", userId);
        String random = getRandom(4);
        return new StringBuilder().append(data).append(random).append(userIdNo).append("20").toString();
    }


    /**
     * 生成 到店支付订单号，20为业务号
     *
     * @param userId
     * @return
     */
//    public static String createFaceOrderNo(Long userId) {
//        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//        String userIdNo = String.format("%010d", userId);
//        String random = getRandom(4);
//        return new StringBuilder().append(data).append(random).append(userIdNo).append("20").toString();
//    }


    /**
     * 生成订单号，02为业务号
     *
     * @param userId
     * @return
     */
    public static String createRefundNo(Long userId) {
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userIdNo = String.format("%010d", userId);
        String random = getRandom(4);
        return new StringBuilder().append(data).append(random).append(userIdNo).append("02").toString();
    }

    /**
     * 生成12位核销码
     *
     * @return
     */
    public static String createOrderVerify() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            builder.append((int) (Math.random() * 10));
        }
        return builder.toString();
    }


    public static String getRandom(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((int) (Math.random() * 10));
        }
        return builder.toString();
    }

    /**
     * 从订单号中取userId
     *
     * @param orderNo
     * @return
     */
    public static Long getUserId(String orderNo) {
        if (StringUtils.isNotBlank(orderNo)) {
            try {
                String userId = orderNo.substring(orderNo.length() - 12, orderNo.length() - 2);
                return Long.parseLong(userId);
            } catch (Exception e) {
            }
        }
        return null;
    }


    public static void main(String[] args) {


        long userId = 123123L;

        System.out.println(Math.random());


//        String orderNo = createOrderNo(userId);
//
//        System.out.println(orderNo.length());
//
//
//        System.out.println(getUserId(orderNo));
    }


    /**
     * 判断是否是充值订单
     *
     * @param orderNo
     * @return
     */
    public static boolean isCouponOrder(String orderNo) {
        if (StringUtils.isNotBlank(orderNo) && orderNo.endsWith("20")) {
            return true;
        }
        return false;
    }


    /**
     * 判断该订单号是否是 当面支付订单
     *
     * @param orderNo
     * @return
     */
    public static boolean isFaceOrder(String orderNo) {
        if (StringUtils.isNotBlank(orderNo) && orderNo.endsWith("20")) {
            return true;
        }
        return false;
    }
}
