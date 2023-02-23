package com.xzkj.flowStore.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.xzkj.flowStore.common.Constant;
import com.xzkj.flowStore.vo.OssTokenVo;
import com.xzkj.flowStore.vo.PolicyVo;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;

@Slf4j
public class AliyunUtil {

    static OSS client = new OSSClientBuilder().build(Constant.OSS_END_POINT, Constant.ALIYUN_ACCESS_KEY_ID, Constant.ALIYUN_ACCESS_KEY_SECRET);

//    static String Callback_url = "https://api.cclzzd.com/out/oss/callback";


    /**
     * 获取图片上传凭证
     *
     * @param dir
     * @return
     */
    public static PolicyVo getPolicy(String dir) {
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");


            System.out.println(new String(binaryData));
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);


            String postSignature = client.calculatePostSignature(postPolicy);


            PolicyVo vo = new PolicyVo();
            vo.setAccessId(Constant.ALIYUN_ACCESS_KEY_ID);
            vo.setHost(Constant.FILE_HOST);
            vo.setSignature(postSignature);
            vo.setPolicy(encodedPolicy);
            vo.setExpire(expiration.getTime());
            vo.setDir(dir);
//            JSONObject jasonCallback = new JSONObject();
//            jasonCallback.put("callbackUrl", Callback_url);
//            jasonCallback.put("callbackBody", "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
//            jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
//            String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
//            vo.setCallback(base64CallbackBody);
            return vo;
        } catch (Exception e) {
            log.error("阿里云获取图片上传凭证异常", e);
            return null;
        }
    }

    /**
     * 移动端获取上传图片token
     *
     * @return
     */
    public static OssTokenVo getToken() {
        String endpoint = "sts.cn-beijing.aliyuncs.com";
        String accessKeyId = "LTAI4FyJqErQHvX3BgZ5bPiJ";
        String accessKeySecret = "rFkMrue16SGxPxpebNpOpD5CA14vq0";
        String roleArn = Constant.ALIYUN_ROLE_ARN;
        String roleSessionName = "external-username";
        String policy = "{\n" +
                "    \"Version\": \"1\", \n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"oss:*\"\n" +
                "            ], \n" +
                "            \"Resource\": [\n" +
                "                \"acs:oss:*:*:*\" \n" +
                "            ], \n" +
                "            \"Effect\": \"Allow\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        try {
            //构造default profile（参数留空，无需添加Region ID）
            IClientProfile profile = DefaultProfile.getProfile("", accessKeyId, accessKeySecret);
            //用profile构造client
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysEndpoint(endpoint);
            request.setSysMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy); // Optional
            final AssumeRoleResponse response = client.getAcsResponse(request);
            System.out.println("Expiration: " + response.getCredentials().getExpiration());
            System.out.println("Access Key Id: " + response.getCredentials().getAccessKeyId());
            System.out.println("Access Key Secret: " + response.getCredentials().getAccessKeySecret());
            System.out.println("Security Token: " + response.getCredentials().getSecurityToken());
            System.out.println("RequestId: " + response.getRequestId());
            OssTokenVo vo = new OssTokenVo();
            vo.setAccessKeyId(response.getCredentials().getAccessKeyId());
            vo.setAccessKeySecret(response.getCredentials().getAccessKeySecret());
            vo.setSecurityToken(response.getCredentials().getSecurityToken());
            vo.setExpiration(response.getCredentials().getExpiration());
            return vo;
        } catch (ClientException e) {
            System.out.println("Failed：");
            System.out.println("Error code: " + e.getErrCode());
            System.out.println("Error message: " + e.getErrMsg());
            System.out.println("RequestId: " + e.getRequestId());
        }
        return null;
    }
}
