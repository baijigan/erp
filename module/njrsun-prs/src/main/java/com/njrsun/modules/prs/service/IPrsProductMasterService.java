package com.njrsun.modules.prs.service;

import java.util.List;
import java.util.Map;

import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.domain.*;

/**
 * 生产完工单主Service接口
 * 
 * @author njrsun
 * @date 2021-11-15
 */
public interface IPrsProductMasterService 
{
    /**
     * 查询生产完工单主
     * 
     * @param uniqueId 生产完工单主ID
     * @return 生产完工单主
     */
    public PrsProductMaster selectPrsProductMasterById(PrsProductMaster uniqueId);

    /**
     * 查询生产完工单主列表
     * 
     * @param prsProductMaster 生产完工单主
     * @return 生产完工单主集合
     */
    public List<PrsProductMaster> selectPrsProductMasterList(PrsProductMaster prsProductMaster);

    /**
     * 新增生产完工单主
     * 
     * @param prsProductMaster 生产完工单主
     * @return 结果
     */
    public int insertPrsProductMaster(PrsProductMaster prsProductMaster);

    /**
     * 修改生产完工单主
     * 
     * @param prsProductMaster 生产完工单主
     * @return 结果
     */
    public int updatePrsProductMaster(PrsProductMaster prsProductMaster);

    /**
     * 批量删除生产完工单主
     * 
     * @param uniqueIds 需要删除的生产完工单主ID
     * @return 结果
     */
    public int deletePrsProductMasterByIds(List<PrsProductMaster> list);

    /**
     * 删除生产完工单主信息
     * 
     * @param uniqueId 生产完工单主ID
     * @return 结果
     */
    public int deletePrsProductMasterById(Long uniqueId);

    void changeWorkStatus(PrsProductMaster prsProductMaster);

    List<PrsProductExport> getDetail(PrsProductExport prsProductSalve);

    List<Map<String, String>> leadInto(PrsOrderMaster prsOrderMaster);

    PrsProductMaster batchCheck(List<PrsProductMaster> list);

    PrsProductMaster batchAntiCheck(List<PrsProductMaster> list);

    List<String> selectPrsCodeList(PrsProductMaster prsProductMaster);

    List<Map<String, String>> chain(PrsProductExport prsProductExport);

    List<PrsOrderProductExport> report(PrsOrderProductExport prsProductExport);

    List<PrsProduceReport> produceReport(PrsOrderProductExport prsProductExport);

    List<PrsOutputReport> output(PrsProductMaster prsProductMaster);

    List<Map<String, Object>> lead(Map<String, String> map);
}
