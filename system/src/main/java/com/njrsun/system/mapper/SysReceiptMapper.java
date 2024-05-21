package com.njrsun.system.mapper;

import com.njrsun.system.domain.SysReceipt;

import java.util.List;
import java.util.Map;


/**
 * 单据列Mapper接口
 * 
 * @author njrsun
 * @date 2021-07-30
 */
public interface SysReceiptMapper 
{
    /**
     * 查询单据列
     * 
     * @param code 单据列ID
     * @return 单据列
     */
    public SysReceipt selectSysReceiptByCode(String code);

    /**
     * 查询单据列列表
     * 
     * @param query 单据列
     * @return 单据列集合
     */
    public List<SysReceipt> selectSysReceiptList(Map<String,String> query);

    /**
     * 新增单据列
     * 
     * @param sysReceipt 单据列
     * @return 结果
     */
    public int insertSysReceipt(SysReceipt sysReceipt);

    /**
     * 修改单据列
     * 
     * @param sysReceipt 单据列
     * @return 结果
     */
    public int updateSysReceipt(SysReceipt sysReceipt);

    /**
     * 删除单据列
     * 
     * @param uniqueId 单据列ID
     * @return 结果
     */
    public int deleteSysReceiptById(Long uniqueId);

    /**
     * 批量删除单据列
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysReceiptByIds(Long[] uniqueIds);
}
