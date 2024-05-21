package com.njrsun.modules.prs.service;

import com.njrsun.modules.prs.domain.PrsUnPickExport;
import com.njrsun.modules.prs.domain.PrsUnPickMaster;
import com.njrsun.modules.prs.domain.PrsUnPickSalve;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 生产领料单Service接口
 * 
 * @author njrsun
 * @date 2021-11-18
 */
public interface IPrsUnPickMasterService 
{
    /**
     * 查询生产领料单
     * 
     * @param uniqueId 生产领料单ID
     * @return 生产领料单
     */
    public PrsUnPickMaster selectPrsUnPickMasterById(PrsUnPickMaster prsUnPickMaster);

    /**
     * 查询生产领料单列表
     * 
     * @param prsUnPickMaster 生产领料单
     * @return 生产领料单集合
     */
    public List<PrsUnPickMaster> selectPrsUnPickMasterList(PrsUnPickMaster prsUnPickMaster);

    /**
     * 新增生产领料单
     * 
     * @param prsUnPickMaster 生产领料单
     * @return 结果
     */
    public int insertPrsUnPickMaster(PrsUnPickMaster prsUnPickMaster);

    /**
     * 修改生产领料单
     * 
     * @param prsUnPickMaster 生产领料单
     * @return 结果
     */
    public int updatePrsUnPickMaster(PrsUnPickMaster prsUnPickMaster);

    /**
     * 批量删除生产领料单
     * 
     * @param uniqueIds 需要删除的生产领料单ID
     * @return 结果
     */
    public int deletePrsUnPickMasterByIds(List<PrsUnPickMaster> list);

    /**
     * 删除生产领料单信息
     * 
     * @param uniqueId 生产领料单ID
     * @return 结果
     */
    public int deletePrsUnPickMasterById(Long uniqueId);

    void changeWorkStatus(PrsUnPickMaster prsUnPickMaster);

    List<PrsUnPickExport> getDetail(PrsUnPickExport prsUnPickExport);

    List<Map<String, String>> lead(Map<String,String> query);

    List<Map<String, String>> leadInto(Map<String, String> query);

    PrsUnPickMaster batchCheck(List<PrsUnPickMaster> list);

    PrsUnPickMaster batchAntiCheck(List<PrsUnPickMaster> list);

    List<String> selectPrsCode(PrsUnPickMaster prsUnPickMaster);

    List<Map<String, String>> chain(PrsUnPickMaster prsProductExport);

    PrsUnPickMaster selectPrsUnPickMasterForUpdate(String code);

    PrsUnPickSalve selectPrsUnPickSalveById(Long uniqueId);

    void changeQuantity(Long uniqueId, BigDecimal quantity);
}
