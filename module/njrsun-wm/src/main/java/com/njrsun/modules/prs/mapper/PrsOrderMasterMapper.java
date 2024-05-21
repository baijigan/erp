package com.njrsun.modules.prs.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.domain.PrsOrderExport;
import com.njrsun.modules.prs.domain.PrsOrderMaster;
import com.njrsun.modules.prs.domain.PrsOverdueExport;
import org.apache.ibatis.annotations.Param;

/**
 * 生产订单主Mapper接口
 * 
 * @author njrsun
 * @date 2021-11-12
 */
public interface PrsOrderMasterMapper 
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
     * 删除生产订单主
     * 
     * @param uniqueId 生产订单主ID
     * @return 结果
     */
    public int deletePrsOrderMasterById(Long uniqueId);

    /**
     * 批量删除生产订单主
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsOrderMasterByIds(List<PrsOrderMaster> prsOrderSalveList);

    PrsOrderMaster selectPrsOrderMasterByCodeForUpdate(String prsCode);

    List<PrsOrderMaster> selectPrsInvoiceForCode(List<PrsOrderMaster> list);

    void changeStatus(@Param("check") String check, @Param("username") String username, @Param("item") PrsOrderMaster prsOrderMaster);

    List<String> selectPrsCodeList(PrsOrderMaster p);

    List<Map<String, String>> upData(PrsOrderExport prsOrderExport);

    Integer updateWorkStatus(@Param("status") String value, @Param("item") PrsOrderMaster prsOrderMaster);

    List<Map<String, String>> lead(PrsOrderMaster prsOrderMaster);

    List<Map<String, String>> leadInto(PrsOrderMaster prsOrderMaster);

    List<Map<String, String>> leadToPick(PrsOrderMaster prsPickMaster);

    void changeWorkStatus(@Param("value") String value, @Param("code") String lastCode);

    int changeQuantity(@Param("id") String woUniqueId, @Param("quantity") BigDecimal invQuantity1);

    List<PrsOverdueExport> export(PrsOverdueExport prsOverdueExport);

    List<Map<String, String>> production(@Param("workType") String workType);

    List<PrsOrderMaster> selectPrsOrderMasterByBeltlineId(@Param("workType") String workType,@Param("flag") String flag ,@Param("beltline_id") String beltline_id);

    List<PrsOrderMaster> selectPrsOrderMaster(@Param("workType") String workType, @Param("flag") String flag);
}
