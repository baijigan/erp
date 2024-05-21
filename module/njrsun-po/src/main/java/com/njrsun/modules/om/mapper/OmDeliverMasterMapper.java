package com.njrsun.modules.om.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.njrsun.common.dto.WmWarehouseDTO;
import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.domain.ExportDeliver;
import com.njrsun.modules.om.domain.OmDeliverMaster;
import com.njrsun.modules.om.domain.OmDeliverReport;
import com.njrsun.modules.om.domain.OmDeliverSalve;
import org.apache.ibatis.annotations.Param;

/**
 * 销售订单主Mapper接口
 * 
 * @author njrsun
 * @date 2021-08-31
 */
public interface OmDeliverMasterMapper 
{
    /**
     * 查询销售订单主
     * 
     * @param omCode 销售订单主ID
     * @return 销售订单主
     */
    public OmDeliverMaster selectOmDeliverMasterByCode(String omCode);

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
     * 删除销售订单主
     * 
     * @param uniqueId 销售订单主ID
     * @return 结果
     */
    public int deleteOmDeliverMasterById(Long uniqueId);

    /**
     * 批量删除销售订单主
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteOmDeliverMasterByCodes(List<OmDeliverMaster> list);

    /**
     * 批量删除销售发货从
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteOmDeliverSalveByOmCodes(String[] omCodes);
    
    /**
     * 批量新增销售发货从
     * 
     * @param omDeliverSalveList 销售发货从列表
     * @return 结果
     */
    public int batchOmDeliverSalve(List<OmDeliverSalve> omDeliverSalveList);
    

    /**
     * 通过销售订单主ID删除销售发货从信息
     * 
     * @param uniqueId 角色ID
     * @return 结果
     */
    public int deleteOmDeliverSalveByOmCode(Long uniqueId);

    ArrayList<Long> selectOmDeliverSalveByCode(String omCode);

    void deleteOmDeliverSalveIds(Long[] longs);

    int updateOmDeliverSlave(ArrayList<OmDeliverSalve> editData);

    ArrayList<Long> selectOmDeliverId(OmDeliverMaster omDeliverMaster);

    OmDeliverMaster selectOmDeliverMasterById(Long aLong);

    ArrayList<String> selectInvoiceStatus(String[] omCode);

    Integer updateCheck(@Param("item") OmDeliverMaster s, @Param("status") String notUnique,@Param("userName") String username);

    List<Map<String, String>> getDetail(OmDeliverMaster omDeliverMaster);

    ArrayList<OmDeliverMaster> selectOmDeliverMasterByCodes(String[] omCode);

    List<ExportDeliver> export(OmDeliverMaster omDeliverMaster);

    List<Map<String, String>> linkDetail(@Param("uniqueId") Long uniqueId);

    List<OmDeliverReport> reportDeliver(OmDeliverReport omDeliverReport);

    OmDeliverMaster selectOmDeliverMasterByCodeForUpdate(String omCode);

    List<OmDeliverSalve>  selectOmDeliverSalveByOmCode(String omCode);

    Integer updateWorkStatus(@Param("value") String value, @Param("item") OmDeliverMaster omDeliverMaster);

    OmDeliverMaster selectOmDeliverMasterByMaster(OmDeliverMaster omDeliverMaster);

    List<Map<String, String>> leadInto(OmDeliverMaster omRejectMaster);

    List<Map<String, String>> leadIntoDetail(Map<String, String> map);

    List<WmWarehouseDTO> selectWmWarehousesDTOList(WmWarehouseDTO o);

    OmDeliverSalve selectOmDeliverSalveForUpdate(Long id);

    void changeQuantity(@Param("amount") BigDecimal amount, @Param("id") String woUniqueId);

    void changeQuantity_r(@Param("id") Long woUniqueId, @Param("woCode") String woCode, @Param("quantity") BigDecimal quantity);

    List<OmDeliverSalve> selectSumQuantity(List<String> collect1);

    void changeWorkStatusByCode(@Param("omCode") String omCode,@Param("status") String status);

    OmDeliverSalve selectOmDeliverSalveById(@Param("id") Long woUniqueId);

    List<Map<String, String>> selectOmDeliverSalveWoById(@Param("uniqueId") Long woUniqueId);

    OmDeliverMaster selectOmDeliverMasterForUpdate(@Param("code") String woCode);

    void systemOpenClose(@Param("code") String woCode, @Param("status") String i);
}
