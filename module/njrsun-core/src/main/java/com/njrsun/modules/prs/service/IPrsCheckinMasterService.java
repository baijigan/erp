package com.njrsun.modules.prs.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.njrsun.modules.prs.domain.PrsCheckinExport;
import com.njrsun.modules.prs.domain.PrsCheckinMaster;
import com.njrsun.modules.prs.domain.PrsCheckinReport;
import com.njrsun.modules.prs.domain.PrsCheckinSalve;

/**
 * 生产入库单主Service接口
 * 
 * @author njrsun
 * @date 2022-01-18
 */
public interface IPrsCheckinMasterService 
{
    /**
     * 查询生产入库单主
     * 
     * @param uniqueId 生产入库单主ID
     * @return 生产入库单主
     */
    public PrsCheckinMaster selectPrsCheckinMasterById(PrsCheckinMaster prsCheckinMaster);

    /**
     * 查询生产入库单主列表
     * 
     * @param prsCheckinMaster 生产入库单主
     * @return 生产入库单主集合
     */
    public List<PrsCheckinMaster> selectPrsCheckinMasterList(PrsCheckinMaster prsCheckinMaster);

    /**
     * 新增生产入库单主
     * 
     * @param prsCheckinMaster 生产入库单主
     * @return 结果
     */
    public int insertPrsCheckinMaster(PrsCheckinMaster prsCheckinMaster);

    /**
     * 修改生产入库单主
     * 
     * @param prsCheckinMaster 生产入库单主
     * @return 结果
     */
    public int updatePrsCheckinMaster(PrsCheckinMaster prsCheckinMaster);

    /**
     * 批量删除生产入库单主
     * 
     * @param uniqueIds 需要删除的生产入库单主ID
     * @return 结果
     */
    public int deletePrsCheckinMasterByIds(List<PrsCheckinMaster> list);

    /**
     * 删除生产入库单主信息
     * 
     * @param uniqueId 生产入库单主ID
     * @return 结果
     */
    public int deletePrsCheckinMasterById(Long uniqueId);

    PrsCheckinMaster batchCheck(List<PrsCheckinMaster> list);

    PrsCheckinMaster batchAntiCheck(List<PrsCheckinMaster> list);

    List<String> selectPrsCode(PrsCheckinMaster prsCheckinMaster);

    List<PrsCheckinExport> getDetail(PrsCheckinExport prsCheckinExport);

    List<Map<String, String>> chain(PrsCheckinExport prsCheckinExport);

    void changeWorkStatus(PrsCheckinMaster prsCheckinMaster);

    List<PrsCheckinReport> report(PrsCheckinReport prsCheckinReport);

    PrsCheckinMaster selectPrsCheckinMasterForUpdate(String woCode);

    PrsCheckinSalve selectPrsCheckinSalveById(Long uniqueId);

    void changeQuantity(Long uniqueId, BigDecimal quantity);

    List<Map<String, String>> lead(Map<String, String> query);

    List<Map<String, String>> leadInto(Map<String, String> map);
}
