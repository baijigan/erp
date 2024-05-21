package com.njrsun.modules.om.mapper;

import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.domain.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 销售订单主Mapper接口
 * 
 * @author njrsun
 * @date 2021-08-31
 */
public interface OmRejectMasterMapper 
{
    /**
     * 查询销售订单主
     * 
     * @param omCode 销售订单主ID
     * @return 销售订单主
     */
    public OmRejectMaster selectOmRejectMasterByCode(String omCode);

    /**
     * 查询销售订单主列表
     * 
     * @param omRejectMaster 销售订单主
     * @return 销售订单主集合
     */
    public List<OmRejectMaster> selectOmRejectMasterList(OmRejectMaster omRejectMaster);

    /**
     * 新增销售订单主
     * 
     * @param omRejectMaster 销售订单主
     * @return 结果
     */
    public int insertOmRejectMaster(OmRejectMaster omRejectMaster);

    /**
     * 修改销售订单主
     * 
     * @param omRejectMaster 销售订单主
     * @return 结果
     */
    public int updateOmRejectMaster(OmRejectMaster omRejectMaster);

    /**
     * 删除销售订单主
     * 
     * @param uniqueId 销售订单主ID
     * @return 结果
     */
    public int deleteOmRejectMasterById(Long uniqueId);

    /**
     * 批量删除销售订单主
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteOmRejectMasterByIds(List<OmRejectMaster> list);

    /**
     * 批量删除销售退货从
     * 
     * @param customerIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteOmRejectSalveByOmCodes(String[] uniqueIds);
    
    /**
     * 批量新增销售退货从
     * 
     * @param omRejectSalveList 销售退货从列表
     * @return 结果
     */
    public int batchOmRejectSalve(List<OmRejectSalve> omRejectSalveList);
    

    /**
     * 通过销售订单主ID删除销售退货从信息
     * 
     * @param uniqueId 角色ID
     * @return 结果
     */
    public int deleteOmRejectSalveByOmCode(Long uniqueId);

    ArrayList<Long> selectomRejectSalveByCode(String omCode);

    void deleteOmRejectSalveByIds(Long[] longs);

    int updateOmRejectSlave(ArrayList<OmRejectSalve> editData);

    List<OmRejectMaster> selectOmRejectMasterByCodes(String[] omCode);

    List<String> selectOmRejectInvoiceStatusByCodes(String[] omCode);

    List<Map<String, String>> getDetail(OmRejectMaster omRejectMaster);

    int batchCheck(@Param("item") OmRejectMaster s, @Param("userName") String userName,@Param("status") String status);

    ArrayList<Long> selectOmRejectMasterId(OmRejectMaster omRejectMaster);

    OmRejectMaster selectOmRejectMasterById(Long i);

    List<ExportReject> export(OmRejectMaster omRejectMaster);

    List<OmRejectReport> rejectReport(OmRejectReport omRejectReport);

    List<Map<String, String>> linkDetail(Long uniqueId);

    OmRejectMaster selectOmRejectMasterByCodeForUpdate(String omCode);

    List<OmRejectSalve> selectOmOrderSalveByOmCode(String omCode);

    Integer updateWorkStatus(@Param("value") String value, @Param("item") OmRejectMaster omRejectMaster);

    OmRejectMaster selectOmRejectMasterByMaster(OmRejectMaster omRejectMaster);

    List<Map<String, String>> selectOmRejectSalveWoById(Long uniqueId);

    List<Map<String, String>> lead(Map<String, String> query);

    List<Map<String, String>> leadInto(Map<String, String> map);

    OmRejectSalve selectOmRejectSalveForUpdate(Long id);

    void changeQuantity(@Param("amount") BigDecimal amount, @Param("id") String woUniqueId);

    OmRejectMaster selectOmRejectMasterForUpdate(@Param("code") String woCode);

    OmRejectSalve selectOmRejectSalveById(@Param("id") Long uniqueId);

    void systemOpenClose(@Param("code") String woCode, @Param("status") String i);
}
