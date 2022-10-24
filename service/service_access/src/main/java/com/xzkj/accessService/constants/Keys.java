package com.xzkj.accessService.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

//密钥实体类
@Component
@PropertySource("classpath:conf/keys.properties")
public class Keys {
    @Value("${YD.YY.PUBLICKEY}")
    private String ydyyPublicKey;
    @Value("${XZ.CSP.PUBLICKEY}")
    private String xzcspPublicKey;
    @Value("${XZ.CSP.PRIVATEKEY}")
    private String xzcspPrivateKey;

    @Value("${XZ.CSP.APPID}")
    private String xzcspAppId;

    @Value("${XZ.CSP.PASSWORD}")
    private String xzkjPassword;
    @Value("${XZ.CSP.ENCRYPT}")
    private String isEncrypt;

    public Keys() {
    }

    public String getXzcspAppId() {
        return xzcspAppId;
    }

    public void setXzcspAppId(String xzcspAppId) {
        this.xzcspAppId = xzcspAppId;
    }

    public String getXzkjPassword() {
        return xzkjPassword;
    }

    public void setXzkjPassword(String xzkjPassword) {
        this.xzkjPassword = xzkjPassword;
    }

    public String getYdyyPublicKey() {
        return ydyyPublicKey;
    }

    public void setYdyyPublicKey(String ydyyPublicKey) {
        this.ydyyPublicKey = ydyyPublicKey;
    }

    public String getXzcspPublicKey() {
        return xzcspPublicKey;
    }

    public void setXzcspPublicKey(String xzcspPublicKey) {
        this.xzcspPublicKey = xzcspPublicKey;
    }

    public String getXzcspPrivateKey() {
        return xzcspPrivateKey;
    }

    public void setXzcspPrivateKey(String xzcspPrivateKey) {
        this.xzcspPrivateKey = xzcspPrivateKey;
    }

    public String getIsEncrypt() {
        return isEncrypt;
    }

    public void setIsEncrypt(String isEncrypt) {
        this.isEncrypt = isEncrypt;
    }
}
