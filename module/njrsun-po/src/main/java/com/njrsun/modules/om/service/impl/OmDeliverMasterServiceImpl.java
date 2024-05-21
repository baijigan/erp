package com.njrsun.modules.om.service.impl;

import com.njrsun.common.constant.Constants;
import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.core.domain.entity.SysDept;
import com.njrsun.common.core.domain.entity.SysDictData;
import com.njrsun.common.dto.InvSortDTO;
import com.njrsun.common.dto.WmWarehouseDTO;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.inv.mapper.InvItemsMapper;
import com.njrsun.modules.inv.service.impl.InvSortServiceImpl;
import com.njrsun.modules.om.constant.OmConstant;
import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.enums.WorkStatus;
import com.njrsun.modules.om.mapper.OmDeliverMasterMapper;
import com.njrsun.modules.om.mapper.OmOrderMasterMapper;
import com.njrsun.modules.om.service.IOmDeliverMasterService;
import com.njrsun.system.service.ISysCoderService;
import com.njrsun.system.service.ISysDeptService;
import com.njrsun.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 销售订单主Service业务层处理
 * 
 * @author njrsun
 * @date 2021-08-31
 */
@Service
public class OmDeliverMasterServiceImpl implements IOmDeliverMasterService
{
    @Autowired
    private OmDeliverMasterMapper omDeliverMasterMapper;
    @Autowired
    private ISysDictTypeService iSysDictTypeService;
    @Autowired
    private ISysDeptService iSysDeptService;
    @Autowired
    private OmOrderMasterMapper omOrderMasterMapper;
    @Autowired
    private InvSortServiceImpl invSortService;
    @Autowired
    private ISysCoderService iSysCoderService;
    @Autowired
    private OmRejectMasterServiceImpl omRejectMasterService;
    @Autowired
    private InvItemsMapper invItemsMapper;

    /**
     * 查询销售订单主
     * 
     * @param omCode 销售订单主ID
     * @return 销售订单主
     */
    @Override
    public OmDeliverMaster selectOmDeliverMasterByCode(OmDeliverMaster omDeliverMaster)
    {
        return omDeliverMasterMapper.selectOmDeliverMasterByMaster(omDeliverMaster);
    }

    /**
     * 查询销售订单主列表
     * 
     * @param omDeliverMaster 销售订单主
     * @return 销售订单主
     */
    @Override
    public List<OmDeliverMaster> selectOmDeliverMasterList(OmDeliverMaster omDeliverMaster)
    {

        for (OmDeliverMaster omDeliverMaster1 : omDeliverMasters) {
            Map<String, Object> params = omDeliverMaster1.getParams();
            params.put(OmConstant.INVOICE,poInvoiceType.get(omDeliverMaster1.getInvoiceType()));
            params.put(OmConstant.DELIVER,hashMap.get(omDeliverMaster1.getWorkType()));
            params.put("sys_work_status",hashMap1.get(omDeliverMaster1.getWorkStatus()));
            params.put("sys_invoice_status",hashMap12.get(omDeliverMaster1.getInvoiceStatus()));
            params.put("deptName",deptMap.get(omDeliverMaster1.getWorkDept()));
        }
        return omDeliverMasters;
    }

    /**
     * 新增销售订单主
     * 
     * @param omDeliverMaster 销售订单主
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertOmDeliverMaster(OmDeliverMaster omDeliverMaster)
    {
        omDeliverMaster.setOmCode(iSysCoderService.generate(OmConstant.DELIVER,omDeliverMaster.getWorkType()));
        omDeliverMaster.setCreateBy(SecurityUtils.getUsername());
        int rows = omDeliverMasterMapper.insertOmDeliverMaster(omDeliverMaster);
        insertOmDeliverSalve(omDeliverMaster);
        return rows;
    }

    /**
     * 修改销售订单主
     * 
     * @param omDeliverMaster 销售订单主
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateOmDeliverMaster(OmDeliverMaster omDeliverMaster)
    {

        List<OmDeliverMaster> omDeliverMasters = omDeliverMasterMapper.selectOmDeliverMasterList(omDeliverMaster);
        List<SysDictData> po_invoice_type = iSysDictTypeService.selectDictDataByType(OmConstant.INVOICE);
        List<SysDictData> po_plan_type = iSysDictTypeService.selectDictDataByType(OmConstant.DELIVER);
        List<SysDictData> sys_work_status = iSysDictTypeService.selectDictDataByType("sys_work_status");
        List<SysDictData> sys_invoice_status = iSysDictTypeService.selectDictDataByType("sys_invoice_status");
        SysDept sysDept = new SysDept();
        List<SysDept> sysDepts = iSysDeptService.selectDeptList(sysDept);
        HashMap<String, String> deptMap = new HashMap<>();
        for (SysDept dept : sysDepts) {
            deptMap.put(dept.getDeptId().toString(),dept.getDeptName());
        }
        HashMap<String, String> hashMap = new HashMap<>();
        for (SysDictData sysDictData : po_plan_type) {
            hashMap.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> poInvoiceType = new HashMap<>();
        for (SysDictData sysDictData : po_invoice_type) {
            poInvoiceType.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> hashMap1 = new HashMap<>();
        for (SysDictData sysDictData : sys_work_status) {
            hashMap1.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }

        OmDeliverMaster omDeliverMaster1 = omDeliverMasterMapper.selectOmDeliverMasterByCodeForUpdate(omDeliverMaster.getOmCode());
        if(omDeliverMaster1.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("审核单据,无法删除");
        }
        /** 老Id */
        ArrayList<Long> oldId = omDeliverMasterMapper.selectOmDeliverSalveByCode(omDeliverMaster.getOmCode());
        /**  新数据  */
        List<OmDeliverSalve> omDeliverSalveList = omDeliverMaster.getOmDeliverSalveList();
        /** 新ID */
        ArrayList<Long> newId = new ArrayList<>();
        ArrayList<OmDeliverSalve> newData = new ArrayList<>();
        ArrayList<OmDeliverSalve> editData = new ArrayList<>();
        for (OmDeliverSalve omDeliverSalve : omDeliverSalveList) {
            if(omDeliverSalve.getUniqueId() == null){
                //新增数据
                omDeliverSalve.setCreateBy(SecurityUtils.getUsername());
                omDeliverSalve.setOmCode(omDeliverMaster.getOmCode());
                newData.add(omDeliverSalve);
            }
            else{
                newId.add(omDeliverSalve.getUniqueId());
            }
        }
        omDeliverMaster.setUpdateBy(SecurityUtils.getUsername());
        // 删除ID
        List<Long> delId = oldId.stream().filter(t -> !newId.contains(t)).collect(Collectors.toList());

        //修改ID
        oldId.retainAll(newId);

        for (Long aLong : oldId) {
            for (OmDeliverSalve omDeliverSalve : omDeliverSalveList) {
                if(omDeliverSalve.getUniqueId().equals(aLong)){
                    omDeliverSalve.setUpdateBy(omDeliverMaster.getUpdateBy());
                    editData.add(omDeliverSalve);
                    break;
                }
            }
        }
        Long[] longs = delId.toArray(new Long[delId.size()]);
        if(longs.length != 0){
            omDeliverMasterMapper.deleteOmDeliverSalveIds(longs);
        }
        if(newData.size() != 0){
            omDeliverMasterMapper.batchOmDeliverSalve(newData);
        }
        int j=1;
        int i = omDeliverMasterMapper.updateOmDeliverMaster(omDeliverMaster);
        if(i == 0 || j == 0){
            throw  new CommonException("修改失败");
        }

        return i;
    }

    /**
     * 批量删除销售订单主
     * 
     * @param deletes 需要删除的销售订单主ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteOmDeliverMasterByCodes(List<OmDeliverMaster> deletes)
    {
        List<String> collect = deletes.stream().map(OmDeliverMaster::getOmCode).collect(Collectors.toList());
        String[] strings = collect.toArray(new String[collect.size()]);
        ArrayList<OmDeliverMaster> list =  omDeliverMasterMapper.selectOmDeliverMasterByCodes(strings);
        for (OmDeliverMaster omDeliverMaster : list) {
            String invoiceStatus = omDeliverMaster.getInvoiceStatus();
            if(invoiceStatus.equals(UserConstants.NOT_UNIQUE)){
                throw  new CommonException(omDeliverMaster.getOmCode() + "：审核单据无法删除");
            }
        }
        omDeliverMasterMapper.deleteOmDeliverSalveByOmCodes(strings);
        return omDeliverMasterMapper.deleteOmDeliverMasterByCodes(deletes);
    }

    /**
     * 删除销售订单主信息
     * 
     * @param uniqueId 销售订单主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOmDeliverMasterById(Long uniqueId)
    {
        omDeliverMasterMapper.deleteOmDeliverSalveByOmCode(uniqueId);
        return omDeliverMasterMapper.deleteOmDeliverMasterById(uniqueId);
    }

    @Override
    public OmDeliverMaster getNextOrLast(OmDeliverMaster omDeliverMaster) {
        Long uniqueId = omDeliverMaster.getUniqueId();
        Boolean type = omDeliverMaster.getType();
       ArrayList<Long> ids =   omDeliverMasterMapper.selectOmDeliverId(omDeliverMaster);
        for (int i = 0; i < ids.size(); i++) {
            Long aLong = ids.get(i);
            if (aLong.equals(uniqueId) && ids.get(ids.size()-1).equals(uniqueId) && type) {
                throw  new CommonException("已经是最后的单据");
            }else if (aLong.equals(uniqueId) && ids.get(0).equals(uniqueId) && !type){
                throw  new CommonException("已经是最后的单据");
            }else if(aLong.equals(uniqueId)){
                return  type?  omDeliverMasterMapper.selectOmDeliverMasterById(ids.get(i+1)) :  omDeliverMasterMapper.selectOmDeliverMasterById(ids.get(i-1));
            }
        }
        return null;
    }

    public void check(List<OmDeliverMaster> list) {
        List<String> collect = list.stream().map(OmDeliverMaster::getOmCode).collect(Collectors.toList());
        String[] strings = collect.toArray(new String[collect.size()]);
        ArrayList<String> status =  omDeliverMasterMapper.selectInvoiceStatus(strings);
            if(status.contains(UserConstants.NOT_UNIQUE) || status.contains(UserConstants.YELLOW)){
                throw  new CommonException("单据需为开立状态");
            }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmDeliverMaster batchCheck(List<OmDeliverMaster> list) {
        String lastCode = null;
        check(list);
        for (OmDeliverMaster s : list) {
            OmDeliverMaster omDeliverMaster = omDeliverMasterMapper.selectOmDeliverMasterByCodeForUpdate(s.getOmCode());
            List<OmDeliverSalve> omDeliverSalveList = omDeliverMasterMapper.selectOmDeliverSalveByOmCode(s.getOmCode());
            if(omDeliverSalveList.size() !=0){
                 lastCode = omDeliverSalveList.get(0).getWoCode();
            }
            for (OmDeliverSalve omDeliverSalve : omDeliverSalveList) {
                if(StringUtils.isNotNull(omDeliverSalve.getWoUniqueId()) && !omDeliverSalve.getWoUniqueId().equals(0L)){
                    BigDecimal amount = omDeliverSalve.getQuantity();
                    OmOrderMaster omOrderMaster =  omOrderMasterMapper.selectOmOrderMasterByWoCodeForUpdate(omDeliverSalve.getWoCode());
                    if(!omOrderMaster.getInvoiceStatus().equals(UserConstants.NOT_UNIQUE)) {
                        throw  new CommonException("审核失败，上游单据非审核状态");
                    }
                    OmOrderSalve omOrderSalve =   omOrderMasterMapper.selectOmOrderSalveById(omDeliverSalve.getWoUniqueId());
                    BigDecimal quantity = omOrderSalve.getQuantity();
                    BigDecimal woQuantity = omOrderSalve.getWiQuantity();
                    BigDecimal subtract = quantity.subtract(woQuantity);
                    if(amount.compareTo(subtract) > 0)
                    {
                        throw  new CommonException(omDeliverMaster.getOmCode() + " : 审核失败");
                    }
                    omOrderMasterMapper.changeQuantity(omDeliverSalve.getWoUniqueId(), amount);
                    invItemsMapper.addLockQuantity(amount,omDeliverSalve.getInvCode());
                    /* 改变上游单据 的 业务状态*/
                    if(!lastCode.equals(omDeliverSalve.getWoCode())){
                        BigDecimal bigDecimal = omOrderMasterMapper.selectSumQuantityByCode(lastCode);
                        if(bigDecimal.compareTo(BigDecimal.ZERO) == 0){

                            omOrderMasterMapper.updateWorkStatus(lastCode, WorkStatus.SYSTEM_CLOSING.getValue());
                        }
                    }
                    lastCode = omDeliverSalve.getWoCode();
                }
            }
            Integer integer = omDeliverMasterMapper.updateCheck(s, UserConstants.NOT_UNIQUE, SecurityUtils.getUsername());
            if(integer == 0){
                throw  new CommonException("审核失败");
            }
            /* 改变末尾上游单据 的 业务状态*/
            if(omDeliverSalveList.size() !=0){
                if(!omDeliverSalveList.get(omDeliverSalveList.size()-1).getWoUniqueId().equals(0L)){
                    BigDecimal bigDecimal = omOrderMasterMapper.selectSumQuantityByCode(omDeliverSalveList.get(omDeliverSalveList.size()-1).getWoCode());
                    if(bigDecimal.compareTo(BigDecimal.ZERO) == 0){
                        omOrderMasterMapper.updateWorkStatus(lastCode, WorkStatus.SYSTEM_CLOSING.getValue());
                    }
                }
            }
        }
        int length = list.size();
        if(length == 1 ){
            return omDeliverMasterMapper.selectOmDeliverMasterByCode(list.get(0).getOmCode());
        }
        return null;
    }

    @Override
    public List<Map<String, String>> getDetail(OmDeliverMaster omDeliverMaster) {
         List<Map<String,String>> result =   omDeliverMasterMapper.getDetail(omDeliverMaster);
        List<SysDictData> po_plan_type = iSysDictTypeService.selectDictDataByType(OmConstant.DELIVER);
        List<SysDictData> om_sale_type = iSysDictTypeService.selectDictDataByType("om_sale_type");
        HashMap<String, String> hashMap = new HashMap<>();
        for (SysDictData sysDictData : po_plan_type) {
            hashMap.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> hashMap1 = new HashMap<>();
        for (SysDictData sysDictData : om_sale_type) {
            hashMap1.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        for (Map<String, String> stringStringMap : result){
            String invoice_type = stringStringMap.get("work_type");
            String sale_type = stringStringMap.get("sale_type");
            stringStringMap.put("work_type",hashMap.get(invoice_type));
            stringStringMap.put("saleType",hashMap1.get(sale_type));
            stringStringMap.put("invoice_type", OmConstant.DELIVER);
        }
         return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmDeliverMaster batchAntiCheck(List<OmDeliverMaster> list) {
       antiCheck(list);
        String lastCode = null;
        for (OmDeliverMaster s : list) {
            OmDeliverMaster omDeliverMaster = omDeliverMasterMapper.selectOmDeliverMasterByCodeForUpdate(s.getOmCode());
            List<OmDeliverSalve> omDeliverSalveList = omDeliverMasterMapper.selectOmDeliverSalveByOmCode(s.getOmCode());
            if(omDeliverSalveList.size() != 0){
                 lastCode = omDeliverSalveList.get(0).getWoCode();
            }
            for (OmDeliverSalve omDeliverSalve : omDeliverSalveList) {
                if(omDeliverSalve.getWoUniqueId() != 0){
                    OmOrderMaster omOrderMaster = omOrderMasterMapper.selectOmOrderMasterByWoCodeForUpdate(omDeliverSalve.getWoCode());
                    if(!omOrderMaster.getInvoiceStatus().equals(UserConstants.NOT_UNIQUE)){
                        throw  new CommonException("反审核失败，上游单据非审核状态");
                    }
                    BigDecimal amount = omDeliverSalve.getQuantity();
                    OmOrderSalve omOrderSalve =   omOrderMasterMapper.selectOmOrderSalveById(omDeliverSalve.getWoUniqueId());
                    BigDecimal woQuantity = omOrderSalve.getWiQuantity();
                    BigDecimal wiQuantityB = omDeliverSalve.getWiQuantity();
                    BigDecimal wiQuantityR = omDeliverSalve.getWiQuantityR();
                    if(wiQuantityB.compareTo(BigDecimal.ZERO)>0  || wiQuantityR.compareTo(BigDecimal.ZERO) > 0){
                        throw  new CommonException(omDeliverMaster.getOmCode()+":反审核失败");
                    }
                    if(amount.compareTo(woQuantity) > 0 )
                    {
                        throw  new CommonException(omDeliverMaster.getOmCode() + " : 反审核失败");
                    }
                    invItemsMapper.addLockQuantity(amount.negate(),omDeliverSalve.getInvCode());
                    omOrderMasterMapper.changeQuantity(omDeliverSalve.getWoUniqueId(), amount.negate());
                    /* 改变上游单据 的 业务状态*/
                    if(!lastCode.equals(omDeliverSalve.getWoCode())){
                            omOrderMasterMapper.updateWorkStatus(lastCode, WorkStatus.NORMAl.getValue());
                    }
                    lastCode = omDeliverSalve.getWoCode();
                }
            }
            Integer integer = omDeliverMasterMapper.updateCheck(s, UserConstants.YELLOW, null);
            if(integer == 0){
                throw  new CommonException("反审核失败");
            }
            /* 改变末尾上游单据 的 业务状态*/
                omOrderMasterMapper.updateWorkStatus(lastCode, WorkStatus.NORMAl.getValue());
        }
        int length = list.size();
        if(length == 1 ){
            return omDeliverMasterMapper.selectOmDeliverMasterByCode(list.get(0).getOmCode());
        }
        return null;
    }

    private void antiCheck(List<OmDeliverMaster> list) {
        List<String> collect = list.stream().map(OmDeliverMaster::getOmCode).collect(Collectors.toList());
        String[] strings = collect.toArray(new String[collect.size()]);
        ArrayList<String> status =  omDeliverMasterMapper.selectInvoiceStatus(strings);
        if(status.contains(UserConstants.UNIQUE) || status.contains(UserConstants.YELLOW)){
            throw  new CommonException("单据需为审核状态");
        }
    }

    @Override
    public List<ExportDeliver> export(OmDeliverMaster omDeliverMaster) {
        return omDeliverMasterMapper.export(omDeliverMaster);
    }

    @Override
    public List<Map<String, String>> linkDetail(Long uniqueId) {
        List<Map<String, String>> maps = omDeliverMasterMapper.linkDetail(uniqueId);
        for (Map<String, String> map : maps) {
            map.put("type",OmConstant.DELIVER);
            map.put("invoice",OmConstant.INVOICE);
        }

        return iSysDictTypeService.transInvoiceWorkType(maps);
    }

    @Override
    public List<OmDeliverReport> reportDeliver(OmDeliverReport omDeliverReport) {
        List<OmDeliverReport> list =    omDeliverMasterMapper.reportDeliver(omDeliverReport);
        List<InvSortDTO> invSorts = invSortService.selectInvSortDTOList(new InvSortDTO());
        List<WmWarehouseDTO> wmWarehouseDTOS = omDeliverMasterMapper.selectWmWarehousesDTOList(null);
        List<SysDictData> po_invoice_type = iSysDictTypeService.selectDictDataByType(OmConstant.INVOICE);
        List<SysDictData> po_plan_type = iSysDictTypeService.selectDictDataByType(OmConstant.DELIVER);
        List<SysDictData> sys_work_status = iSysDictTypeService.selectDictDataByType("sys_work_status");
        List<SysDictData> sys_invoice_status = iSysDictTypeService.selectDictDataByType("sys_invoice_status");
        List<SysDictData> om_sale_type = iSysDictTypeService.selectDictDataByType("om_sale_type");
        List<SysDept> sysDepts = iSysDeptService.selectDeptList(new SysDept());
        HashMap<String, String> deptMap = new HashMap<>();
        for (SysDept dept : sysDepts) {
            deptMap.put(dept.getDeptId().toString(),dept.getDeptName());
        }
        HashMap<String, String> hashMap = new HashMap<>();
        for (SysDictData sysDictData : po_plan_type) {
            hashMap.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> hashMap3 = new HashMap<>();
        for (SysDictData sysDictData : om_sale_type) {
            hashMap3.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> poInvoiceType = new HashMap<>();
        for (SysDictData sysDictData : po_invoice_type) {
            poInvoiceType.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> hashMap1 = new HashMap<>();
        for (SysDictData sysDictData : sys_work_status) {
            hashMap1.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> hashMap12 = new HashMap<>();
        for (SysDictData sysDictData : sys_invoice_status) {
            hashMap12.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        for (OmDeliverReport omDeliver : list) {
            for (InvSortDTO invSort : invSorts) {
                if(omDeliver.getInvSortId().equals(invSort.getSortId().toString())){
                    omDeliver.setInvSortId(invSort.getSortName());
                }
                if(omDeliver.getInvSortRoot().equals(invSort.getSortId().toString())){
                    omDeliver.setInvSortRoot(invSort.getSortName());
                }
            }
            for (WmWarehouseDTO wmWarehouse : wmWarehouseDTOS) {
                if(wmWarehouse.getCode().equals(omDeliver.getWhCode())){
                    omDeliver.setWhCode(wmWarehouse.getName());
                }
            }
            omDeliver.setWorkDept(deptMap.get(omDeliver.getWorkDept()));
            omDeliver.setInvoiceType(poInvoiceType.get(omDeliver.getInvoiceType()));
            omDeliver.setWorkType(hashMap1.get(omDeliver.getWorkType()));
            omDeliver.setInvoiceStatus(hashMap12.get(omDeliver.getInvoiceStatus()));
            omDeliver.setWorkStatus(hashMap.get(omDeliver.getWorkStatus()));
            omDeliver.setSaleType(hashMap3.get(omDeliver.getSaleType()));
        }
        return list;
    }

    @Override
    public  List<Map<String,String>> chainDetail(OmDeliverSalve omDeliverSalve) {
        List<Map<String,String>> list =  omDeliverMasterMapper.selectOmDeliverSalveWoById(omDeliverSalve.getUniqueId());
        List<Map<String,String>> list1 =  omRejectMasterService.linkDetail(omDeliverSalve.getUniqueId());
        list.addAll(list1);
        return iSysDictTypeService.transInvoiceWorkType(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeWorkStatus(OmDeliverMaster omDeliverMaster) {

        String omCode = omDeliverMaster.getOmCode();
        String workStatus = omDeliverMaster.getWorkStatus();
        OmDeliverMaster omDeliverMaster1 = omDeliverMasterMapper.selectOmDeliverMasterByCodeForUpdate(omCode);
        if(omDeliverMaster1.getWorkStatus().equals(WorkStatus.SYSTEM_CLOSING.getValue())){
            throw  new CommonException("系统关闭状态,无法操作");
        }
        Integer i;
        if(omDeliverMaster1.getWorkStatus().equals(workStatus)){
            i = omDeliverMasterMapper.updateWorkStatus(WorkStatus.NORMAl.getValue(), omDeliverMaster);
        }else{
            i = omDeliverMasterMapper.updateWorkStatus(workStatus, omDeliverMaster);
        }
        if(i == 0 ){
            throw  new CommonException("操作失败");
        }
    }

    /**
     * 新增销售发货从信息
     * 
     * @param omDeliverMaster 销售订单主对象
     */
    public void insertOmDeliverSalve(OmDeliverMaster omDeliverMaster)
    {
        List<OmDeliverSalve> omDeliverSalveList = omDeliverMaster.getOmDeliverSalveList();
        String uniqueId = omDeliverMaster.getOmCode();
        if (StringUtils.isNotNull(omDeliverSalveList))
        {
            List<OmDeliverSalve> list = new ArrayList<OmDeliverSalve>();
            for (OmDeliverSalve omDeliverSalve : omDeliverSalveList)
            {
                omDeliverSalve.setCreateBy(omDeliverMaster.getCreateBy());
                omDeliverSalve.setOmCode(uniqueId);
                list.add(omDeliverSalve);
            }
            if (list.size() > 0)
            {
                omDeliverMasterMapper.batchOmDeliverSalve(list);
            }
        }
    }

    public OmDeliverSalve selectOmDeliverSalveByIdForUpdate(Long batchUniqueId) {
        return omDeliverMasterMapper.selectOmDeliverSalveForUpdate(batchUniqueId);
    }

    public void changeQuantity(BigDecimal amount, String woUniqueId) {
         omDeliverMasterMapper.changeQuantity(amount,woUniqueId);
    }

    public void changeWorkStatusByIds(List<String> collect1) {
         List<OmDeliverSalve> result =  omDeliverMasterMapper.selectSumQuantity(collect1);
        for (OmDeliverSalve salve : result) {
           if(salve.getQuantity().compareTo(BigDecimal.ZERO) ==0){
               omDeliverMasterMapper.changeWorkStatusByCode(salve.getOmCode(),WorkStatus.SYSTEM_CLOSING.getValue());
           }
        }
    }

    public List<Map<String, String>> leadInto(OmDeliverMaster omDeliverMaster) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.MONTH,-3);
        Date time = instance.getTime();
        String customer = omDeliverMaster.getCustomer();
        if(customer==null || customer.equals("")){
            omDeliverMaster.setEndDate(time);
        }
        List<Map<String, String>> maps = omDeliverMasterMapper.leadInto(omDeliverMaster);
        for (Map<String, String> map : maps) {
            map.put("work_type",OmConstant.DELIVER);
            map.put("invoice_type",OmConstant.INVOICE);
        }

        return maps;

    }

    @Override
    public OmDeliverMaster selectOmDeliverMasterForUpdate(String woCode) {
        return omDeliverMasterMapper.selectOmDeliverMasterForUpdate(woCode);
    }

    public OmDeliverMaster selectOmDeliverMasterByCodeForUpdate(String woCode) {
        return omDeliverMasterMapper.selectOmDeliverMasterByCodeForUpdate(woCode);
    }

    public OmDeliverSalve selectOmDeliverSalveById(Long woUniqueId) {
        return omDeliverMasterMapper.selectOmDeliverSalveById(woUniqueId);
    }

    public List<Map<String, String>> leadIntoDetail(Map<String, String> map) {
        List<Map<String, String>> maps = omDeliverMasterMapper.leadIntoDetail(map);
        for (Map<String, String> map1 : maps) {
            map1.put("work_type",OmConstant.DELIVER);
            map1.put("invoice_type",OmConstant.INVOICE);
        }
        return maps;
    }

    @Override
    public void systemOpenClose(String woCode, String i) {
        omDeliverMasterMapper.systemOpenClose(woCode, i);
    }
}
