package com.njrsun.modules.om.service;

import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 销售订单Service接口
 * 
 * @author njrsun
 * @date 2021-08-28
 */
public interface IOmOrderMasterService 
{
    /**
     * 查询销售订单
     * 
     * @param uniqueId 销售订单ID
     * @return 销售订单
     */
    public OmOrderMaster selectOmOrderMasterByCode(OmOrderMaster omOrderMaster);

    /**
     * 查询销售订单列表
     * 
     * @param omOrderMaster 销售订单
     * @return 销售订单集合
     */
    public List<OmOrderMaster> selectOmOrderMasterList(OmOrderMaster omOrderMaster);

    /**
     * 新增销售订单
     * 
     * @param omOrderMaster 销售订单
     * @return 结果
     */
    public int insertOmOrderMaster(OmOrderMaster omOrderMaster);

    /**
     * 修改销售订单
     * 
     * @param omOrderMaster 销售订单
     * @return 结果
     */
    public int updateOmOrderMaster(OmOrderMaster omOrderMaster);

    /**
     * 批量删除销售订单
     * 
     * @param assist 需要删除的销售订单ID
     * @return 结果
     */
    public int deleteOmOrderMasterByCodes(List<OmOrderMaster> list);

    /**
     * 删除销售订单信息
     * 
     * @param uniqueId 销售订单ID
     * @return 结果
     */
    public int deleteOmOrderMasterById(Long uniqueId);

    OmOrderMaster getNextOrLast(OmOrderMaster omOrderMaster);

    List<Map<String, String>> getDetail(OmOrderMaster omOrderMaster);

    OmOrderMaster query(Long uniqueId);

    Boolean check(List<OmOrderMaster> list);

    OmOrderMaster batchCheck(List<OmOrderMaster> list);

    OmOrderMaster batchAntiCheck(List<OmOrderMaster> list);

    List<ExportOrder> export(OmOrderMaster omOrderMaster);

    List<Map<String, String>> chainDetail(OmOrderSalve omOrderSalve);

    List<OmOrderReport> orderReport(OmOrderReport omOrderReport);

    List<OmOrderRank> rank(OmOrderRank omOrderRank);

    List<OmWorkStaffStatistics> statistics(OmWorkStaffStatistics omWorkStaffStatistics);

    Boolean antiCheck(List<OmOrderMaster> list);

    void changeWorkStatus(OmOrderMaster omOrderMaster);

    List<Map<String, Object>> selecDetailByWorkType(Map<String, Object> map);

    void changeDate(OmOrderMaster omOrderMaster);

    List<OmOrderDetail> orderDetail(String supplyType);

    List<Map<String, String>> selectOmOrderMasterByMap(String ppNumber);

    List<OmOrderMpReport> orderDetailMp(OmOrderMpReport omOrderMpReport);

    List<Map<String, String>> leadMp(OmOrderReport omOrderSalve);
    List<Map<String, String>> leadIntoMp(OmOrderReport omOrderSalve);

    List<Map<String, String>> leadPrs(OmOrderReport omOrderSalve);
    List<Map<String, String>> leadIntoPrs(OmOrderReport omOrderSalve);

    OmOrderMaster selectOmOrderMasterForUpdate(String omCode);

    void changeQuantityPrs(Long woUniqueId, BigDecimal quantity);
    void changeQuantityMp(Long woUniqueId, BigDecimal quantity);

    OmOrderSalve selectOmOrderSalveById(Long woUniqueId);

    OmOrderSalve selectOrderSalveBySalve(OmOrderSalve salve);

    Map<String ,String> pumpCustomerByPpNumber(String ppNumber);

    Map<String, String> queryOrderSalveByUniqueId(Long woUniqueId);
}
