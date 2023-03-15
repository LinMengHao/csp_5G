package com.xzkj.apiService.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 模板信息e_model_infoMapper接口
 * 
 * @author ruoyi
 * @date 2022-11-24
 */
@Mapper
@Component
public interface ServiceMapper
{

    /**
     * 查询模板信息e_model_info列表
     * 
     * @param map 模板信息e_model_info
     * @return 模板信息e_model_info集合
     */
    @SuppressWarnings("MybatisXMapperMethodInspection")
    public List<Map<String,String>> selectOrderList(@Param("map") Map<String,String> map);


    /**
     * 查询模板信息e_model_info列表
     * @param map 模板信息e_model_info
     * @return 模板信息e_model_info集合
     */
    @SuppressWarnings("MybatisXMapperMethodInspection")
    public List<Map<String,String>> selectModelList(@Param("map") Map<String,String> map);

    /**
     * 查询模板信息e_model_info_new列表
     * @param map 模板信息e_model_info
     * @return 模板信息e_model_info集合
     */
    @SuppressWarnings("MybatisXMapperMethodInspection")
    public List<Map<String,String>> selectModelNewList(@Param("map") Map<String,String> map);

    /**
     * 获取模板表的最大id值
     * @param tableName 表名
     * @param field 主键名称
     * @return 最大id值
     */
    public int selectMaxId(@Param("tableName") String tableName,@Param("field") String field);

    List<Map<String, String>> selectSignList(@Param("map")Map<String, String> map);

    public List<Map<String,String>> selectAppByCode(@Param("map") Map<String,String> map);
}
