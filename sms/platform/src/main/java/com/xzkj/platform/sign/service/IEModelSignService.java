package com.xzkj.platform.sign.service;

import com.xzkj.platform.common.core.domain.Ztree;
import com.xzkj.platform.sign.domain.EModelSign;

import java.util.List;


/**
 * 签名Service接口
 * 
 * @author linmenghao
 * @date 2023-01-31
 */
public interface IEModelSignService 
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
     * 批量删除签名
     * 
     * @param ids 需要删除的签名主键集合
     * @return 结果
     */
    public int deleteEModelSignByIds(String ids);

    /**
     * 删除签名信息
     * 
     * @param id 签名主键
     * @return 结果
     */
    public int deleteEModelSignById(Long id);

    /**
     * 查询签名树列表
     * 
     * @return 所有签名信息
     */
    public List<Ztree> selectEModelSignTree();
    /**
     * 获取数据表的最大主键值
     * @param tableName 数据表
     * @param field 字段名
     * @return 结果
     */
    public long getMaxId(String tableName, String field);

    int copyEModelSign(EModelSign eModelSign);

    List<EModelSign> selectPEModelSignList(EModelSign eModelSign);

    EModelSign selectEModelSignBySignId(String s);

    List<EModelSign> selectSignByAppId(EModelSign sign);

}
