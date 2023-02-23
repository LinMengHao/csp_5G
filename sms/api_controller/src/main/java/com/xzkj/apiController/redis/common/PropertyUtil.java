package com.xzkj.apiController.redis.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 属性文件
 * @author admin
 */
public class PropertyUtil {
	/**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
	private Properties properties;

	/**
	 * 构造
	 * @param fileName 属性文件绝对地址
	 */
	public PropertyUtil(String fileName) {
		properties = new Properties();
		//第一种，通过类加载器进行获取properties文件流
        //InputStream inputStream = PropertyUtil.class.getClassLoader().getResourceAsStream(fileName);
		//第二种，通过类进行获取properties文件流
        InputStream inputStream = PropertyUtil.class.getResourceAsStream(fileName);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }finally{
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
	}

	/**
	 * 根据属性名取值
	 * @param name 属性名
	 * @return 值
	 */
	public String getProper(String name) {
		if (properties.isEmpty()) {
			return null;
		}
		String property = properties.getProperty(name);
		if (property == null) {
			return null;
		} else {
			return property.trim();
		}
	}

	/**
	 * 取所有的键值
	 * @return 所有的值
	 */
	public ConcurrentHashMap<String, String> getProperties() {
		if (properties.isEmpty()) {
			return null;
		}
		ConcurrentHashMap<String, String> p = new ConcurrentHashMap<String, String>();
		Enumeration names = properties.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			p.put(name, getProper(name));
		}
		return p;
	}

	/**
	 * 根据属性名取值，无值返回默认值
	 * @param name 属性名
	 * @param defaultV 默认值
	 * @return 值
	 */
	public String getProper(String name, String defaultV) {
		if (properties.isEmpty()) {
			return null;
		}
		String property = properties.getProperty(name);
		if (property == null) {
			return defaultV;
		} else {
			return property.trim();
		}
	}
}