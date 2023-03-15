package com.xzkj.taskProxy.redis.common;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 序列化
 * @author admin
 * @since 2017/10/10.
 */
public class SerializeUtil {
    /**
     * log
     */
    private static final Log LOGGER = LogFactory.getLog(SerializeUtil.class);

    /**
     * 序列化
     * @param object object
     * @return byte
     */
    public static byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            LOGGER.error("serialize error,", e);
        }
        return null;
    }

    /**
     * 反序列化
     * @param bytes param
     * @return object
     */
    public static Object unserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            LOGGER.error("serialize error,", e);
        }
        return null;
    }
}
