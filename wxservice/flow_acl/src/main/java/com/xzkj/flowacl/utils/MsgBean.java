package com.xzkj.flowacl.utils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author dashan
 * @date 2018/9/17 下午5:35
 */
public class MsgBean extends HashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 1L;

    public MsgBean() {
        put("code", 0);
        put("msg", "success");
    }

    public static MsgBean error() {
        return error(500, "未知异常，请联系后端开发人员");
    }

    public static MsgBean error(String msg) {
        return error(500, msg);
    }

    public static MsgBean error(int code, String msg) {
        MsgBean msgBean = new MsgBean();
        msgBean.put("code", code);
        msgBean.put("msg", msg);
        return msgBean;
    }

    public static MsgBean ok() {
        return new MsgBean();
    }

    public static MsgBean ok(String msg) {
        MsgBean msgBean = new MsgBean();
        msgBean.put("msg", msg);
        return msgBean;
    }

    public static MsgBean ok(int code, String msg) {
        MsgBean msgBean = new MsgBean();
        msgBean.put("code", code);
        msgBean.put("msg", msg);
        return msgBean;
    }


    @Override
    public MsgBean put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public MsgBean putData(String key, Object value) {

        HashMap<String, Object> dataMap = null;
        if ( super.containsKey("data") ) {
            dataMap = (HashMap<String, Object>) this.get("data");
        } else {
            dataMap = new HashMap<String, Object>();
        }
        dataMap.put(key, value);
        super.put("data", dataMap);
        return this;
    }


    public MsgBean putData(Object value) {
        super.put("data", value);
        return this;
    }

}
