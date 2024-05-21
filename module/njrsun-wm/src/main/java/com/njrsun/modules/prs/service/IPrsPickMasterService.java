package com.njrsun.modules.prs.service;

import com.njrsun.modules.prs.domain.PrsPickExport;
import com.njrsun.modules.prs.domain.PrsPickMaster;
import com.njrsun.modules.prs.domain.PrsPickSalve;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 生产领料单Service接口
 * 
 * @author njrsun
 * @date 2021-11-18
 */
public interface IPrsPickMasterService 
{
    /**
     * 查询生产领料单
     * 
     * @param uniqueId 生产领料单ID
     * @return 生产领料单
     */
    public PrsPickMaster selectPrsPickMasterById(PrsPickMaster prsPickMaster);

    /**
     * 查询生产领料单列表
     * 
     * @param prsPickMaster 生产领料单
     * @return 生产领料单集合
     */
    public List<PrsPickMaster> selectPrsPickMasterList(PrsPickMaster prsPickMaster);

    /**
     * 新增生产领料单
     * 
     * @param prsPickMaster 生产领料单
     * @return 结果
     */
    public int insertPrsPickMaster(PrsPickMaster prsPickMaster);

    /**
     * 修改生产领料单
     * 
     * @param prsPickMaster 生产领料单
     * @return 结果
     */
    public int updatePrsPickMaster(PrsPickMaster prsPickMaster);

    /**
     * 批量删除生产领料单
     * 
     * @param uniqueIds 需要删除的生产领料单ID
     * @return 结果
     */
    public int deletePrsPickMasterByIds(List<PrsPickMaster> list);

    /**
     * 删除生产领料单信息
     * 
     * @param uniqueId 生产领料单ID
     * @return 结果
     */
    public int deletePrsPickMasterById(Long uniqueId);

    void changeWorkStatus(PrsPickMaster prsPickMaster);

    List<PrsPickExport> getDetail(PrsPickExport prsPickExport);

    List<Map<String, String>> lead(Map<String,String> query);

    List<Map<String, String>> leadInto(Map<String,String> query);

    void changeQuantity(String uniqueId, BigDecimal quantity);

    void changeQuantity_r(String uniqueId, BigDecimal quantity);

    PrsPickMaster batchCheck(List<PrsPickMaster> list);

    PrsPickMaster batchAntiCheck(List<PrsPickMaster> list);

    List<String> selectPrsCode(PrsPickMaster prsPickMaster);

    List<Map<String, String>> chain(PrsPickMaster prsProductExport);

    PrsPickMaster selectPrsPickMasterForUpdate(String woCode);

    PrsPickSalve selectPrsPickSalveById(Long valueOf);

    List<PrsPickSalve> selectQuantity(String code);
}
