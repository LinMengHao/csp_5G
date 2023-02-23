package com.xzkj.taskChannel.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("springBeanUtil")//使用注解
public class SpringBeanUtil implements ApplicationContextAware {
    protected final static Log logger = LogFactory.getLog(SpringBeanUtil.class);
      
    private static ApplicationContext ctx = null;  
    @Override
    public void setApplicationContext(ApplicationContext ctx)  
            throws BeansException {  
        SpringBeanUtil.ctx = ctx;  
    }

    public static Object getBean(String prop) {
        Object obj = ctx.getBean(prop);
        return obj;  
    }  

    public static ApplicationContext getApplicationContext(){
        return ctx;
    }

    public static Object getBean(Class c){
        return ctx.getBean(c);
    }
    
    /**
	 * 获取以当前日期小时为基准的文件路径
	 * @return
	 * @throws Exception 
	 */
	public static String getFilePathByDateHour() throws Exception{
		DateFormat format = new SimpleDateFormat("yyyyMMddHH");
		String dateStr = format.format(new Date());
		
		String path = dateStr.substring(0, 4)+"/"+dateStr.substring(4, 6)+"/"+dateStr.substring(6, 8)+"/"+dateStr.substring(8, 10)+"/";
		
		return path;
	}
}
