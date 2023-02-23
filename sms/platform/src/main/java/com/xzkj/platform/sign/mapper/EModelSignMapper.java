package com.xzkj.platform.sign.mapper;

import com.xzkj.platform.sign.domain.EModelSign;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 签名Mapper接口
 * 
 * @author linmenghao
 * @date 2023-01-31
 */
public interface EModelSignMapper 
{
    /**
     * 查询签名
     * 
     * @param id 签名主键
     * @return 签名
     */
    public EModelSign selectEModelSignById(Long id);

    /**
     * 查询签名列表
     * 
     * @param eModelSign 签名
     * @return 签名集合
     */
    public List<EModelSign> selectEModelSignList(EModelSign eModelSign);

    /**
     * 新增签名
     * 
     * @param eModelSign 签名
     * @return 结果
     */
    public int insertEModelSign(EModelSign eModelSign);

    /**
     * 修改签名
     * 
     * @param eModelSign 签名
     * @return 结果
     */
    public int updateEModelSign(EModelSign eModelSign);

    /**
     * 删除签名
     * 
     * @param id 签名主键
     * @return 结果
     */
    public int deleteEModelSignById(Long id);

    /**
     * 批量删除签名
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEModelSignByIds(String[] ids);

    public int selectMaxId(@Param("tableName") String tableName, @Param("field") String field);

    List<EModelSign> selectPEModelSignList(EModelSign eModelSign);

    EModelSign selectEModelSignBySignId(@Param("signId") String signId);

    List<EModelSign> selectSignByAppId(EModelSign sign);
}
