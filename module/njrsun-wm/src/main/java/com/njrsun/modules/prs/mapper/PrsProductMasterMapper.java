package com.njrsun.modules.prs.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.domain.*;
import org.apache.ibatis.annotations.Param;

/**
 * 生产完工单主Mapper接口
 * 
 * @author njrsun
 * @date 2021-11-15
 */
public interface PrsProductMasterMapper 
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
     * 删除生产完工单主
     * 
     * @param uniqueId 生产完工单主ID
     * @return 结果
     */
    public int deletePrsProductMasterById(Long uniqueId);

    /**
     * 批量删除生产完工单主
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsProductMasterByIds(List<PrsProductMaster> list);

    PrsProductMaster selectPrsProductMasterForUpdate(String prsCode);

    List<PrsProductMaster> selectPrsPrductInvoiceByList(List<PrsProductMaster> list);

    Integer updateWorkStatus(@Param("status") String value, @Param("item") PrsProductMaster prsProductMaster);

    Integer changeStatus(@Param("check") String check, @Param("username") String username, @Param("item") PrsProductMaster prsProductMaster);

    List<String> selectPrsCodeList(PrsProductMaster prsProductMaster);

    List<Map<String, String>> upData(PrsProductExport prsProductExport);


    List<PrsOrderProductExport> report(PrsOrderProductExport prsProductExport);

    List<Map<String, String>> downData(PrsOrderExport prsOrderExport);

    List<PrsProduceReport> produceReport(PrsOrderProductExport prsProductExport);

    List<PrsOutputReport> output(PrsProductMaster prsProductMaster);

    void isYield(@Param("prsCode") String prsCode,@Param("status") String status);

    int updateQuantity(@Param("woCode") String woCode, @Param("quantity") BigDecimal quantity);

    void changeWorkStatus(@Param("status") String value, @Param("code") String lastCode);

    List<Map<String,Object>> lead(Map<String, String> map);
}
