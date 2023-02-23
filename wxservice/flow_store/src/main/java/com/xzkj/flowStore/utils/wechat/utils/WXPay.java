package com.xzkj.flowStore.utils.wechat.utils;


import com.xzkj.flowStore.common.Constant;

import java.util.Map;

public class WXPay {

    /**
     * 向 Map 中添加 appid、mch_id、nonce_str、sign_type、sign <br>
     * 该函数适用于商户适用于统一下单等接口，不适用于红包、代金券接口
     *
     * @param reqData
     * @return
     * @throws Exception
     */
    public Map<String, String> fillRequestData(Map<String, String> reqData) {
        reqData.put("appid", Constant.appID);
        reqData.put("mch_id", Constant.mchID);
        reqData.put("nonce_str", WXPayUtil.generateNonceStr());
        reqData.put("sign_type", WXPayConstants.SignType.MD5.name());
        reqData.put("sign", WXPayUtil.generateSignature(reqData, Constant.appKey, WXPayConstants.SignType.MD5));
        return reqData;
    }

    /**
     * 判断xml数据的sign是否有效，必须包含sign字段，否则返回false。
     *
     * @param reqData 向wxpay post的请求数据
     * @return 签名是否有效
     * @throws Exception
     */
    public boolean isResponseSignatureValid(Map<String, String> reqData) throws Exception {
        // 返回数据的签名方式和请求中给定的签名方式是一致的
        return WXPayUtil.isSignatureValid(reqData, Constant.appKey, WXPayConstants.SignType.MD5);
    }


    /**
     * 处理 HTTPS API返回数据，转换成Map对象。return_code为SUCCESS时，验证签名。
     *
     * @param xmlStr API返回的XML格式数据
     * @return Map类型数据
     * @throws Exception
     */
    public Map<String, String> processResponseXml(String xmlStr) throws Exception {
        String RETURN_CODE = "return_code";
        String return_code;
        Map<String, String> respData = WXPayUtil.xmlToMap(xmlStr);
        if ( respData.containsKey(RETURN_CODE) ) {
            return_code = respData.get(RETURN_CODE);
        } else {
            throw new Exception(String.format("No `return_code` in XML: %s", xmlStr));
        }

        if ( return_code.equals(WXPayConstants.FAIL) ) {
            return respData;
        } else if ( return_code.equals(WXPayConstants.SUCCESS) ) {
            if ( this.isResponseSignatureValid(respData) ) {
                return respData;
            } else {
                throw new Exception(String.format("Invalid sign value in XML: %s", xmlStr));
            }
        } else {
            throw new Exception(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
        }
    }

}
