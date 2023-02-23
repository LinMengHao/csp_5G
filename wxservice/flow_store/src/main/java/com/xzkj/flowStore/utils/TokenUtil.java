package com.xzkj.flowStore.utils;


import com.xzkj.flowStore.entity.SysUsers;
import com.xzkj.flowStore.entity.User;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

public class TokenUtil {


    public static String makeToken(Long uid, String password) {
        if(!StringUtils.hasText(password)){
            password="";
        }
        StringBuilder sb = new StringBuilder().append(uid).append(":").append(password);
        String token = Base64Utils.encodeToUrlSafeString((uid + ":" + MD5Util.MD5(sb.toString())).getBytes());
        return token;
    }


    public static String makeToken(User users) {
        return makeToken(users.getId(), users.getPassword());
    }

    public static Long getUIDFromToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            String t = new String(Base64Utils.decodeFromUrlSafeString(token));
            String[] a = t.split(":");
            return Long.parseLong(a[0]);
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean checkToken(User user, String token) {

        String token1 = makeToken(user.getId(), user.getPassword());
        if (token1.equals(token)) {
            return true;
        }
        return false;
    }


    public static boolean checkToken(SysUsers user, String token) {
        String token1 = makeToken(user.getId(), user.getPassword());
        if (token1.equals(token)) {
            return true;
        }
        return false;
    }
}
