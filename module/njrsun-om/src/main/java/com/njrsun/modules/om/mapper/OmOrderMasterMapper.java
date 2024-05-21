package com.njrsun.modules.om.mapper;

import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.domain.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 销售订单Mapper接口
 * 
 * @author njrsun
 * @date 2021-08-28
 */
public interface OmOrderMasterMapper 
{
    /**
     * 查询销售订单
     * 
     * @param uniqueId 销售订单ID
     * @return 销售订单
     */
    public OmOrderMaster selectOmOrderMasterByCode(String omCode);

    public OmOrderMaster selectOmOrderMasterByMaster(OmOrderMaster omOrderMaster);

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
     * 删除销售订单
     * 
     * @param uniqueId 销售订单ID
     * @return 结果
     */
    public int deleteOmOrderMasterById(Long uniqueId);

    /**
     * 批量删除销售订单
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteOmOrderMasterByCodes(List<OmOrderMaster> list);

    /**
     * 批量删除销售订单从
     * 
     * @param customerIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteOmOrderSalveByOmCodes(List<String> omCodes);
    
    /**
     * 批量新增销售订单从
     * 
     * @param omOrderSalveList 销售订单从列表
     * @return 结果
     */
    public int batchOmOrderSalve(List<OmOrderSalve> omOrderSalveList);
    

    /**
     * 通过销售订单ID删除销售订单从信息
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    public int deleteOmOrderSalveByOmCode(Long uniqueId);

    ArrayList<Long> selectomOrderSalveByCode(String omCode);

    void deleteOmOrderSalveIds(Long[] longs);

    int updateOmOrderSlave(ArrayList<OmOrderSalve> editData);

    ArrayList<Long> selectomOrderSalveByCodes(String[] omCode);

    ArrayList<Long> selectOrderMasterId(OmOrderMaster omOrderMaster1);

    OmOrderMaster selectOmOrderMasterById(Long id);

    List<Map<String, String>> getDetail( OmOrderMaster omOrderMaster);

    OmOrderMaster query(Long uniqueId);

    OmOrderSalve selectOrderSalveBySalve(OmOrderSalve omOrderSalve);

    Map<String, String> selectNum(String omCode);

    ArrayList<String> selectInvoiceStatusByCode(List<String> omCode);

    void batchCheck(@Param("list") List<OmOrderMaster> list,@Param("userName")String userName);

    void batchAntiCheck(List<OmOrderMaster> list);

    OmOrderSalve selectOmOrderSalveById(Long woUniqueId);

    ArrayList<OmOrderMaster> selectOmOrderMasterByCodes(String[] omCode);

    List<ExportOrder> export(OmOrderMaster omOrderMaster);

    List<Map<String, String>> linkDetail(OmOrderSalve omOrderSalve);

    List<OmOrderReport> selectOrderReport(OmOrderReport omOrderReport);

    List<OmOrderRank> rank(OmOrderRank omOrderRank);

    List<OmWorkStaffStatistics> statistics(OmWorkStaffStatistics omWorkStaffStatistics);

    OmOrderMaster selectOmOrderMasterByWoCodeForUpdate(String woCode);

    BigDecimal selectSumQuantityByCode(String lastCode);

    void updateWorkStatus(@Param("lastCode") String lastCode, @Param("status") String s);

    Integer updateWorkStatus_1(@Param("value") String value, @Param("item") OmOrderMaster omOrderMaster);

    List<Map<String, Object>> selectDetailByWorkType(Map<String, Object> map);

    void insertOpertion(@Param("omCode") String omCode, @Param("newStatus") String newStatus, @Param("deliverDate") Date deliverDate, @Param("deliverStatus") String deliverStatus,@Param("name") String name);

    int changeDate(OmOrderMaster omOrderMaster);

    List<OmOrderDetail> orderDetail(String supplyType);

    List<Map<String,String>> selectOmOrderMasterByMap(String map);

    void changeQuantity( @Param("id") Long woUniqueId, @Param("quantity") BigDecimal quantity);
    void changeQuantityMp(@Param("id") Long woUniqueId,@Param("quantity") BigDecimal quantity);
    void changeQuantityPrs(@Param("id") Long woUniqueId,@Param("quantity") BigDecimal quantity);

    List<Map<String, String>> leadIntoMp(OmOrderReport omOrderSalve);
    List<Map<String, String>> leadMp(OmOrderReport omOrderSalve);

    List<Map<String, String>> leadIntoPrs(OmOrderReport omOrderSalve);
    List<Map<String, String>> leadPrs(OmOrderReport omOrderSalve);

    List<OmOrderMpReport> selectOrderDetailMp(OmOrderMpReport omOrderMpReport);

    Map<String ,String> selectCustomerNameByPpNumber(String ppNumber);

    OmOrderSalve queryOrderSalveByUniqueId(@Param("uniqueId") Long woUniqueId);

}
