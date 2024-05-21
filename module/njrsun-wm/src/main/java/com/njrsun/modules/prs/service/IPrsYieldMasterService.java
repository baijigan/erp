package com.njrsun.modules.prs.service;

import java.util.List;
import com.njrsun.modules.prs.domain.PrsYieldMaster;

/**
 * 生产报工单主Service接口
 * 
 * @author njrsun
 * @date 2022-01-13
 */
public interface IPrsYieldMasterService 
{
    /**
     * 查询生产报工单主
     * 
     * @param uniqueId 生产报工单主ID
     * @return 生产报工单主
     */
    public PrsYieldMaster selectPrsYieldMasterById(PrsYieldMaster prsYieldMaster);

    /**
     * 查询生产报工单主列表
     * 
     * @param prsYieldMaster 生产报工单主
     * @return 生产报工单主集合
     */
    public List<PrsYieldMaster> selectPrsYieldMasterList(PrsYieldMaster prsYieldMaster);

    /**
     * 新增生产报工单主
     * 
     * @param prsYieldMaster 生产报工单主
     * @return 结果
     */
    public int insertPrsYieldMaster(PrsYieldMaster prsYieldMaster);

    /**
     * 修改生产报工单主
     * 
     * @param prsYieldMaster 生产报工单主
     * @return 结果
     */
    public int updatePrsYieldMaster(PrsYieldMaster prsYieldMaster);

    /**
     * 批量删除生产报工单主
     * 
     * @param uniqueIds 需要删除的生产报工单主ID
     * @return 结果
     */
    public int deletePrsYieldMasterByIds(List<PrsYieldMaster> list);

    /**
     * 删除生产报工单主信息
     * 
     * @param uniqueId 生产报工单主ID
     * @return 结果
     */
    public int deletePrsYieldMasterById(Long uniqueId);

    PrsYieldMaster batchCheck(List<PrsYieldMaster> list);

    PrsYieldMaster batchAntiCheck(List<PrsYieldMaster> list);
}
