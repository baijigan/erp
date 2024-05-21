package com.njrsun.modules.prs.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.domain.*;
import org.apache.ibatis.annotations.Param;

/**
 * 生产入库单主Mapper接口
 * 
 * @author njrsun
 * @date 2022-01-18
 */
public interface PrsCheckinMasterMapper 
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
     * 删除生产入库单主
     * 
     * @param uniqueId 生产入库单主ID
     * @return 结果
     */
    public int deletePrsCheckinMasterById(Long uniqueId);

    /**
     * 批量删除生产入库单主
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsCheckinMasterByIds(List<PrsCheckinMaster> prsCheckinSalveList);

    /**
     * 批量删除生产入库单从
     * 
     * @param customerIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsCheckinSalveByPrsCodes(List<PrsCheckinMaster> prsCheckinSalveList);
    
    /**
     * 批量新增生产入库单从
     * 
     * @param prsCheckinSalveList 生产入库单从列表
     * @return 结果
     */
    public int batchPrsCheckinSalve(List<PrsCheckinSalve> prsCheckinSalveList);
    

    /**
     * 通过生产入库单主ID删除生产入库单从信息
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    public int deletePrsCheckinSalveByPrsCode(Long uniqueId);

    PrsCheckinMaster selectPrsCheckinForUpdate(String prsCode);

    ArrayList<Long> selectPrsSalveByCode(String prsCode);

    void deletePrsCheckinSlave(Long[] longs);

    int updateCheckinSalve(ArrayList<PrsCheckinSalve> editData);

    ArrayList<PrsCheckinMaster> selectPrsInvoice(List<PrsCheckinMaster> list);

    List<String> selectPrsCode(PrsCheckinMaster prsCheckinMaster);

    Integer changeStatus(@Param("check") String check, @Param("username") String username, @Param("item") PrsCheckinMaster prsCheckinMaster);

    List<PrsCheckinExport> getDetail(PrsCheckinExport prsCheckinExport);

    List<Map<String, String>> downData(PrsProductExport prsProductExport);

    List<Map<String, String>> upData(PrsCheckinExport prsCheckinExport);

    Integer updateWorkStatus(@Param("status") String value, @Param("item") PrsCheckinMaster prsCheckinMaster);

    List<PrsCheckinReport> report(PrsCheckinReport prsCheckinReport);

    PrsCheckinSalve selectPrsCheckinSalveById(@Param("id") Long uniqueId);

    void changeQuantity(@Param("id") Long uniqueId, @Param("quantity") BigDecimal quantity);

    List<Map<String, String>> lead(Map<String, String> query);

    List<Map<String, String>> leadInto(Map<String, String> map);
}
