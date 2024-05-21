package com.njrsun.system.service.impl;

import java.util.List;
import java.util.Map;

import com.njrsun.system.domain.SysReceipt;
import com.njrsun.system.mapper.SysReceiptMapper;
import com.njrsun.system.service.ISysReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 单据列Service业务层处理
 * 
 * @author njrsun
 * @date 2021-07-30
 */
@Service
public class SysReceiptServiceImpl implements ISysReceiptService
{
    @Autowired
    private SysReceiptMapper sysReceiptMapper;

    /**
     * 查询单据列
     * 
     * @param code 单据列ID
     * @return 单据列
     */
    @Override
    public SysReceipt selectSysReceiptByCode(String code)
    {
        return sysReceiptMapper.selectSysReceiptByCode(code);
    }

    /**
     * 查询单据列列表
     * 
     * @param query 单据列
     * @return 单据列
     */
    @Override
    public List<SysReceipt> selectSysReceiptList(Map<String,String> query)
    {
        return sysReceiptMapper.selectSysReceiptList(query);
    }

    /**
     * 新增单据列
     * 
     * @param sysReceipt 单据列
     * @return 结果
     */
    @Override
    public int insertSysReceipt(SysReceipt sysReceipt)
    {
        return sysReceiptMapper.insertSysReceipt(sysReceipt);
    }

    /**
     * 修改单据列
     * 
     * @param sysReceipt 单据列
     * @return 结果
     */
    @Override
    public int updateSysReceipt(SysReceipt sysReceipt)
    {
        return sysReceiptMapper.updateSysReceipt(sysReceipt);
    }

    /**
     * 批量删除单据列
     * 
     * @param uniqueIds 需要删除的单据列ID
     * @return 结果
     */
    @Override
    public int deleteSysReceiptByIds(Long[] uniqueIds)
    {
        return sysReceiptMapper.deleteSysReceiptByIds(uniqueIds);
    }

    /**
     * 删除单据列信息
     * 
     * @param uniqueId 单据列ID
     * @return 结果
     */
    @Override
    public int deleteSysReceiptById(Long uniqueId)
    {
        return sysReceiptMapper.deleteSysReceiptById(uniqueId);
    }
}
