package com.njrsun.modules.prs.service;

import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.domain.ProductionProgress;
import com.njrsun.modules.prs.domain.PrsOrderExport;
import com.njrsun.modules.prs.domain.PrsOrderMaster;
import com.njrsun.modules.prs.domain.PrsOverdueExport;

import java.util.List;
import java.util.Map;

/**
 * 生产订单主Service接口
 * 
 * @author njrsun
 * @date 2021-11-12
 */
public interface IPrsOrderMasterService 
{
    /**
     * 查询生产订单主
     * 
     * @param uniqueId 生产订单主ID
     * @return 生产订单主
     */
    public PrsOrderMaster selectPrsOrderMasterById(PrsOrderMaster prsOrderMaster);

    /**
     * 查询生产订单主列表
     * 
     * @param prsOrderMaster 生产订单主
     * @return 生产订单主集合
     */
    public List<PrsOrderMaster> selectPrsOrderMasterList(PrsOrderMaster prsOrderMaster);

    /**
     * 新增生产订单主
     * 
     * @param prsOrderMaster 生产订单主
     * @return 结果
     */
    public int insertPrsOrderMaster(PrsOrderMaster prsOrderMaster);

    /**
     * 修改生产订单主
     * 
     * @param prsOrderMaster 生产订单主
     * @return 结果
     */
    public int updatePrsOrderMaster(PrsOrderMaster prsOrderMaster);

    /**
     * 批量删除生产订单主
     * 
     * @param uniqueIds 需要删除的生产订单主ID
     * @return 结果
     */
    public int deletePrsOrderMasterByIds(List<PrsOrderMaster> list);

    PrsOrderMaster batchCheck(List<PrsOrderMaster> list);

    PrsOrderMaster batchAntiCheck(List<PrsOrderMaster> list);

    List<String> selectPrsCodeList(PrsOrderMaster m);

    List<Map<String, String>> linkDetail(PrsOrderExport prsOrderExport);

    void changeWorkStatus(PrsOrderMaster prsOrderMaster);

    List<PrsOverdueExport> export(PrsOverdueExport prsOverdueExport);

    ProductionProgress production(String workType, String flag);

    void updatePrsOrderMasterCheck(PrsOrderMaster prsOrderMaster);

    List<Map<String, String>> lead(PrsOrderMaster prsOrderMaster);
}
