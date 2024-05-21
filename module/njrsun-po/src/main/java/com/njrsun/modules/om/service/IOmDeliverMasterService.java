package com.njrsun.modules.om.service;

import com.njrsun.modules.om.domain.ExportDeliver;
import com.njrsun.modules.om.domain.OmDeliverMaster;
import com.njrsun.modules.om.domain.OmDeliverReport;
import com.njrsun.modules.om.domain.OmDeliverSalve;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 销售订单主Service接口
 * 
 * @author njrsun
 * @date 2021-08-31
 */
public interface IOmDeliverMasterService 
{
    /**
     * 查询销售订单主
     * 
     * @param uniqueId 销售订单主ID
     * @return 销售订单主
     */
    public OmDeliverMaster selectOmDeliverMasterByCode(OmDeliverMaster omDeliverMaster);

    /**
     * 查询销售订单主列表
     * 
     * @param omDeliverMaster 销售订单主
     * @return 销售订单主集合
     */
    public List<OmDeliverMaster> selectOmDeliverMasterList(OmDeliverMaster omDeliverMaster);

    /**
     * 新增销售订单主
     * 
     * @param omDeliverMaster 销售订单主
     * @return 结果
     */
    public int insertOmDeliverMaster(OmDeliverMaster omDeliverMaster);

    /**
     * 修改销售订单主
     * 
     * @param omDeliverMaster 销售订单主
     * @return 结果
     */
    public int updateOmDeliverMaster(OmDeliverMaster omDeliverMaster);

    /**
     * 批量删除销售订单主
     * 
     * @param uniqueIds 需要删除的销售订单主ID
     * @return 结果
     */
    public int deleteOmDeliverMasterByCodes(List<OmDeliverMaster> list);

    /**
     * 删除销售订单主信息
     * 
     * @param uniqueId 销售订单主ID
     * @return 结果
     */
    public int deleteOmDeliverMasterById(Long uniqueId);

    OmDeliverMaster getNextOrLast(OmDeliverMaster omDeliverMaster);

    OmDeliverMaster batchCheck(List<OmDeliverMaster> list);

    List<Map<String, String>> getDetail(OmDeliverMaster omDeliverMaster);

    OmDeliverMaster batchAntiCheck(List<OmDeliverMaster> list);

    List<ExportDeliver> export(OmDeliverMaster omDeliverMaster);

    List<Map<String,String>> linkDetail(Long uniqueId);

    List<OmDeliverReport> reportDeliver(OmDeliverReport omDeliverReport);

    OmDeliverMaster selectOmDeliverMasterForUpdate(String woCode);

    List<Map<String,String>> chainDetail(OmDeliverSalve omDeliverSalve);

    void changeWorkStatus(OmDeliverMaster omDeliverMaster);

    public OmDeliverSalve selectOmDeliverSalveByIdForUpdate(Long batchUniqueId);

    public void changeQuantity(BigDecimal amount, String woUniqueId);

    List<Map<String, String>> leadInto(OmDeliverMaster omDeliverMaster);

    List<Map<String, String>> leadIntoDetail(Map<String, String> map);

    void systemOpenClose(String woCode, String i);
}
