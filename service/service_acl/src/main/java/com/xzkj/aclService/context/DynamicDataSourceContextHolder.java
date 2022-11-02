package com.xzkj.aclService.context;


import com.xzkj.aclService.constants.DataSourceConstants;

/**
 * 动态数据源上下文处理
 */
public class DynamicDataSourceContextHolder {
    /**
     * 使用ThreadLocal保存副本，使得每个线程拥有自己的副本，可以自行修改
     */
    private static final ThreadLocal<String> DATASOURCE_CONTEXT_KEY_HOLDER=new ThreadLocal<>();
    /**
     * 设置数据源
     * @param key
     */
    public static void setContextKey(String key){
        DATASOURCE_CONTEXT_KEY_HOLDER.set(key);
    }

    /**
     * 获取数据源名称 默认主数据源
     * @return
     */
    public static String getContextKey(){
        String key= DATASOURCE_CONTEXT_KEY_HOLDER.get();
        return key==null? DataSourceConstants.DS_KEY_MASTER:key;
    }

    /**
     * 及时删除数据源名称副本，避免ThreadLocal内存泄漏
     */
    public static void removeContextKey(){
        DATASOURCE_CONTEXT_KEY_HOLDER.remove();
    }
}
