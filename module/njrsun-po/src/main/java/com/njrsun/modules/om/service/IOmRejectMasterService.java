package com.njrsun.modules.om.service;

import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 销售订单主Service接口
 * 
 * @author njrsun
 * @date 2021-08-31
 */
public interface IOmRejectMasterService 
{
    /**
     * 查询销售订单主
     * 
     * @param uniqueId 销售订单主ID
     * @return 销售订单主
     */
    public OmRejectMaster selectOmRejectMasterByCode(OmRejectMaster omRejectMaster);

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
     * 批量删除销售订单主
     * 
     * @param uniqueIds 需要删除的销售订单主ID
     * @return 结果
     */
    public int deleteOmRejectMasterByCodes(List<OmRejectMaster> list);

    /**
     * 删除销售订单主信息
     * 
     * @param uniqueId 销售订单主ID
     * @return 结果
     */
    public int deleteOmRejectMasterById(Long uniqueId);

    OmRejectMaster batchCheck(List<OmRejectMaster> list);

    List<Map<String,String>> getDetail(OmRejectMaster omRejectMaster);

    OmRejectMaster batchAntiCheck(List<OmRejectMaster> list);

    OmRejectMaster gettNextOrLast(OmRejectMaster omRejectMaster);

    List<ExportReject> export(OmRejectMaster omRejectMaster);

    List<OmRejectReport> rejectReport(OmRejectReport omRejectReport);

    List<Map<String, String>> chainDetail(OmRejectSalve omRejectSalve);

    void changeWorkStatus(OmRejectMaster omRejectMaster);

    public OmRejectSalve selectOmRejectSalveByIdForUpdate(Long batchUniqueId);

    public void changeQuantity(BigDecimal amount, String woUniqueId);

    OmRejectMaster selectOmRejectMasterForUpdate(String woCode);

    List<Map<String, String>> linkDetail(Long uniqueId);

    OmRejectSalve selectOmRejectSalveById(Long uniqueId);

    List<Map<String, String>> lead(Map<String, String> query);

    List<Map<String, String>> leadInto(Map<String, String> map);

    void systemOpenClose(String woCode, String i);
}
