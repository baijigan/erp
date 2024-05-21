package com.njrsun.modules.om.service.impl;

import com.njrsun.common.constant.Constants;
import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.core.domain.entity.SysDept;
import com.njrsun.common.core.domain.entity.SysDictData;
import com.njrsun.common.dto.InvSortDTO;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.inv.service.impl.InvSortServiceImpl;
import com.njrsun.modules.om.constant.OmConstant;
import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.enums.OmSupplyType;
import com.njrsun.modules.om.enums.WorkStatus;
import com.njrsun.modules.om.mapper.OmOrderMasterMapper;
import com.njrsun.modules.om.service.IOmOrderMasterService;
import com.njrsun.system.service.ISysCoderService;
import com.njrsun.system.service.ISysDeptService;
import com.njrsun.system.service.ISysDictTypeService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 销售订单Service业务层处理
 * 
 * @author njrsun
 * @date 2021-08-28
 */
@Service
public class OmOrderMasterServiceImpl implements IOmOrderMasterService
{
    @Autowired
    private OmOrderMasterMapper omOrderMasterMapper;


    @Autowired
    private ISysDictTypeService iSysDictTypeService;

    @Autowired
    private ISysDeptService iSysDeptService;

    @Autowired
    private OmDeliverMasterServiceImpl omDeliverMasterService;

    @Autowired
    private OmRejectMasterServiceImpl omRejectMasterService;

    @Autowired
    private InvSortServiceImpl invSortService;

    @Autowired
    private ISysCoderService iSysCoderService;



    /**
     * 查询销售订单
     * 
     * @param omCode 销售订单ID
     * @return 销售订单
     */
    @Override
    public OmOrderMaster selectOmOrderMasterByCode(OmOrderMaster omOrderMaster)
    {
        return omOrderMasterMapper.selectOmOrderMasterByMaster(omOrderMaster);
    }


    public List<Map<String,String>> selectOmOrderMasterByMap(String map)
    {
        return omOrderMasterMapper.selectOmOrderMasterByMap(map);
    }

    @Override
    public List<OmOrderMpReport> orderDetailMp(OmOrderMpReport omOrderMpReport) {

        List<OmOrderMpReport> omOrderMpReports = omOrderMasterMapper.selectOrderDetailMp(omOrderMpReport);
        List<SysDictData> po_invoice_type = iSysDictTypeService.selectDictDataByType(OmConstant.SUPPLY);
        List<SysDictData> om_sale_type = iSysDictTypeService.selectDictDataByType(OmConstant.SALE);

        HashMap<String, String> poInvoiceType = new HashMap<>();
        for (SysDictData sysDictData : po_invoice_type) {
            poInvoiceType.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> hashMap1 = new HashMap<>();
        for (SysDictData sysDictData : om_sale_type) {
            hashMap1.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        for (OmOrderMpReport exportOrder : omOrderMpReports) {
            exportOrder.setSaleType(hashMap1.get(exportOrder.getSaleType()));
            exportOrder.setSupplyType(poInvoiceType.get(exportOrder.getSupplyType()));
        }

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

        return omOrderMpReports;

    }


    /**
     * 查询销售订单列表
     * 
     * @param omOrderMaster 销售订单
     * @return 销售订单
     */
    @Override
    public List<OmOrderMaster> selectOmOrderMasterList(OmOrderMaster omOrderMaster)
    {

        SysDept sysDept = new SysDept();
        List<SysDept> sysDepts = sysDeptMapper.selectDeptList(sysDept);
        List<SysDictData> om_invoice_type = iSysDictTypeService.selectDictDataByType("om_invoice_type");
        List<SysDictData> sys_work_status = iSysDictTypeService.selectDictDataByType("sys_work_status");
        List<SysDictData> sys_invoice_status = iSysDictTypeService.selectDictDataByType("sys_invoice_status");
        HashMap<String, String> deptMap = new HashMap<>();
        for (SysDept dept : sysDepts) {
            deptMap.put(dept.getDeptId().toString(),dept.getDeptName());
        }
        HashMap<String, String> hashMap = new HashMap<>();
        HashMap<String, String> poInvoiceType = new HashMap<>();
        for (SysDictData sysDictData : om_invoice_type) {
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
        for (OmFapiaoMaster poPlanMaster : omFapiaoMasters) {
            Map<String, Object> params = poPlanMaster.getParams();
            params.put("om_invoice_type",poInvoiceType.get(poPlanMaster.getInvoiceType()));
            params.put("sys_work_status",hashMap1.get(poPlanMaster.getWorkStatus()));
            params.put("sys_invoice_status",hashMap12.get(poPlanMaster.getInvoiceStatus()));
            params.put("deptName",deptMap.get(poPlanMaster.getWorkDept()));
        }


        OmFapiaoMaster ex =   omFapiaoMasterMapper.selectOmFapiaoMasterForUpdate(omCode);
        if(!ex.getInvoiceStatus().equals("0")){
            throw  new CommonException(omCode + " 状态异常");
        }
        if(!ex.getVatStatus().equals("0")){
            throw  new CommonException(omCode + "发票状态异常");
        }
        OmFapiaoMaster omFapiaoMaster = omFapiaoMasterMapper.selectOmFapiaoMasterById(omCode);
        omFapiaoMaster.getOmFapiaoSalveList().forEach(e ->{
            Long woUniqueId = e.getWoUniqueId();
            if(woUniqueId!=0 && woUniqueId!=null){
                Map<String, Object>  master= wmOutApiService.selectWmOutMasterForUpdate(e.getWoCode());
                if(!master.get("invoice_status").equals(Constants.CHECK)){
                    throw  new CommonException(e.getWoCode() + "非审核状态，单据无法审核");
                }

                Map<String,Object> result =  wmOutApiService.selectWmOutSalveById(woUniqueId);
                BigDecimal quantity = (BigDecimal) result.get("quantity");
                BigDecimal vat_quantity = (BigDecimal) result.get("vat_quantity");
                BigDecimal subtract = quantity.subtract(vat_quantity);
                if(subtract.compareTo(e.getVatQuantity()) < 0){
                    throw  new CommonException("审核失败，" + e.getInvName() + "数量不足");
                }else{
                    wmOutApiService.changeQuantityVat(woUniqueId,e.getVatQuantity());
                }
            }
        });

        SysDept sysDept = new SysDept();
        List<SysDept> sysDepts = sysDeptMapper.selectDeptList(sysDept);
        List<SysDictData> om_invoice_type = iSysDictTypeService.selectDictDataByType("om_invoice_type");
        List<SysDictData> sys_work_status = iSysDictTypeService.selectDictDataByType("sys_work_status");
        List<SysDictData> sys_invoice_status = iSysDictTypeService.selectDictDataByType("sys_invoice_status");
        HashMap<String, String> deptMap = new HashMap<>();
        for (SysDept dept : sysDepts) {
            deptMap.put(dept.getDeptId().toString(),dept.getDeptName());
        }
        HashMap<String, String> hashMap = new HashMap<>();
        HashMap<String, String> poInvoiceType = new HashMap<>();
        for (SysDictData sysDictData : om_invoice_type) {
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
        for (OmFapiaoMaster poPlanMaster : omFapiaoMasters) {
            Map<String, Object> params = poPlanMaster.getParams();
            params.put("om_invoice_type",poInvoiceType.get(poPlanMaster.getInvoiceType()));
            params.put("sys_work_status",hashMap1.get(poPlanMaster.getWorkStatus()));
            params.put("sys_invoice_status",hashMap12.get(poPlanMaster.getInvoiceStatus()));
            params.put("deptName",deptMap.get(poPlanMaster.getWorkDept()));
        }

        List<OmOrderMaster> omOrderMasters = omOrderMasterMapper.selectOmOrderMasterList(omOrderMaster);
        List<SysDictData> po_invoice_type = iSysDictTypeService.selectDictDataByType(OmConstant.INVOICE);
        List<SysDictData> po_plan_type = iSysDictTypeService.selectDictDataByType(OmConstant.ORDER);
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
        HashMap<String, String> hashMap12 = new HashMap<>();
        for (SysDictData sysDictData : sys_invoice_status) {
            hashMap12.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        for (OmOrderMaster orderMaster : omOrderMasters) {
            Map<String, Object> params = orderMaster.getParams();
            params.put("po_invoice_type",poInvoiceType.get(orderMaster.getInvoiceType()));
            params.put("po_order_type",hashMap.get(orderMaster.getWorkType()));
            params.put("sys_work_status",hashMap1.get(orderMaster.getWorkStatus()));
            params.put("sys_invoice_status",hashMap12.get(orderMaster.getInvoiceStatus()));
            params.put("deptName",deptMap.get(orderMaster.getWorkDept()));
        }

        return omOrderMasters;
    }


    /**
     * 新增销售订单
     * 
     * @param omOrderMaster 销售订单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertOmOrderMaster(OmOrderMaster omOrderMaster)
    {
        Boolean censor = censor(omOrderMaster);
        if(!censor){
            throw  new CommonException("供货方式与物料信息不匹配");
        }
        if("".equals(omOrderMaster.getOmCode()) || StringUtils.isNull(omOrderMaster.getOmCode())){
            omOrderMaster.setOmCode(iSysCoderService.generate(OmConstant.ORDER,omOrderMaster.getWorkType()));
        }
        omOrderMaster.setCreateBy(SecurityUtils.getUsername());
        int rows = omOrderMasterMapper.insertOmOrderMaster(omOrderMaster);
        /* 新增明细*/
        insertOmOrderSalve(omOrderMaster);
        return rows;
    }


    /**
     *  检查单子供货方式是否合法
     * @return
     */
     public Boolean censor(OmOrderMaster omOrderMaster){
         String supplyType = omOrderMaster.getSupplyType();
         if(supplyType.equals(Constants.CHECK)){
             List<OmOrderSalve> omOrderSalveList = omOrderMaster.getOmOrderSalveList();
             List<String> collect = omOrderSalveList.stream().map(OmOrderSalve::getOmCode).distinct().collect(Collectors.toList());
             return collect.size() == 1;
         }
        return true;
     }


    /**
     * 修改销售订单
     * 
     * @param omOrderMaster 销售订单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateOmOrderMaster(OmOrderMaster omOrderMaster)
    {
        OmOrderMaster omOrderMaster1 = omOrderMasterMapper.selectOmOrderMasterByWoCodeForUpdate(omOrderMaster.getOmCode());
        if(omOrderMaster1.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("审核单据，无法删除");
        }
        Boolean censor = censor(omOrderMaster);
        if(!censor){
            throw  new CommonException("供货方式与物料信息不匹配");
        }

        /** 老Id */
        ArrayList<Long> oldId = omOrderMasterMapper.selectomOrderSalveByCode(omOrderMaster.getOmCode());
        /**  新数据  */
        List<OmOrderSalve> omOrderSalveList = omOrderMaster.getOmOrderSalveList();
        /** 新ID */
        ArrayList<Long> newId = new ArrayList<>();
        ArrayList<OmOrderSalve> newData = new ArrayList<>();
        ArrayList<OmOrderSalve> editData = new ArrayList<>();
        for (OmOrderSalve omOrderSalve : omOrderSalveList) {
            if(omOrderSalve.getUniqueId() == null){
                //新增数据
                omOrderSalve.setCreateBy(SecurityUtils.getUsername());
                omOrderSalve.setOmCode(omOrderMaster.getOmCode());
                newData.add(omOrderSalve);
            }
            else{
                newId.add(omOrderSalve.getUniqueId());
            }
        }
        omOrderMaster.setUpdateBy("admin");
        // 删除ID
        List<Long> delId = oldId.stream().filter(t -> !newId.contains(t)).collect(Collectors.toList());

        //修改ID
        oldId.retainAll(newId);

        for (Long aLong : oldId) {
            for (OmOrderSalve omOrderSalve : omOrderSalveList) {
                if(omOrderSalve.getUniqueId().equals(aLong)){
                    omOrderSalve.setUpdateBy(omOrderMaster.getUpdateBy());
                    editData.add(omOrderSalve);
                    break;
                }
            }
        }
        Long[] longs = delId.toArray(new Long[delId.size()]);
        if(longs.length != 0){
            omOrderMasterMapper.deleteOmOrderSalveIds(longs);
        }
        if(newData.size() != 0){
            omOrderMasterMapper.batchOmOrderSalve(newData);
        }
        int j=1;
        if(editData.size() != 0){
            j = omOrderMasterMapper.updateOmOrderSlave(editData);
        }
        int i = omOrderMasterMapper.updateOmOrderMaster(omOrderMaster);
        if(i == 0 || j == 0){
            throw  new CommonException("修改失败");
        }

        return i;
    }

    /**
     * 批量删除销售订单
     * 
     * @param list 需要删除的销售订单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteOmOrderMasterByCodes(List<OmOrderMaster> list)
    {
        List<String> collect = list.stream().map(OmOrderMaster::getOmCode).collect(Collectors.toList());
        ArrayList<String> strings = omOrderMasterMapper.selectInvoiceStatusByCode(collect);
        if(strings.contains(UserConstants.NOT_UNIQUE)){
            throw  new CommonException("审核单据无法删除");
        }
        omOrderMasterMapper.deleteOmOrderSalveByOmCodes(collect);
        return omOrderMasterMapper.deleteOmOrderMasterByCodes(list);
    }

    /**
     * 删除销售订单信息
     * 
     * @param uniqueId 销售订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOmOrderMasterById(Long uniqueId)
    {
        omOrderMasterMapper.deleteOmOrderSalveByOmCode(uniqueId);
        return omOrderMasterMapper.deleteOmOrderMasterById(uniqueId);
    }

    @Override
    public OmOrderMaster getNextOrLast(OmOrderMaster omOrderMaster) {
        Long uniqueId = omOrderMaster.getUniqueId();
        Boolean type = omOrderMaster.getType();
        OmOrderMaster omOrderMaster1 = new OmOrderMaster();
        omOrderMaster1.setParams(omOrderMaster.getParams());
        omOrderMaster1.setWorkType(omOrderMaster.getWorkType());
       ArrayList<Long> ids =   omOrderMasterMapper.selectOrderMasterId(omOrderMaster1);
        for (int i = 0; i < ids.size(); i++) {
            Long aLong = ids.get(i);
            if (aLong.equals(uniqueId) && ids.get(ids.size()-1).equals(uniqueId) && type) {
                throw  new CommonException("已经是最后的单据");
            }else if (aLong.equals(uniqueId) && ids.get(0).equals(uniqueId) && !type){
                throw  new CommonException("已经是最后的单据");
            }else if(aLong.equals(uniqueId)){
                return  type?  omOrderMasterMapper.selectOmOrderMasterById(ids.get(i+1)) :  omOrderMasterMapper.selectOmOrderMasterById(ids.get(i-1));
            }
        }

        return  null;
    }

    @Override
    public List<Map<String, String>> getDetail(OmOrderMaster omOrderMaster) {
        List<Map<String, String>> detail;
        detail = omOrderMasterMapper.getDetail(omOrderMaster);
        List<SysDictData> po_plan_type = iSysDictTypeService.selectDictDataByType(OmConstant.ORDER);
        List<SysDictData> om_sale_type = iSysDictTypeService.selectDictDataByType("om_sale_type");
        HashMap<String, String> hashMap = new HashMap<>();
        for (SysDictData sysDictData : po_plan_type) {
            hashMap.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> hashMap1 = new HashMap<>();
        for (SysDictData sysDictData : om_sale_type) {
            hashMap1.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        for (Map<String, String> stringStringMap : detail) {
            String invoice_type = stringStringMap.get("work_type");
            String sale_type = stringStringMap.get("sale_type");
            stringStringMap.put("work_type",hashMap.get(invoice_type));
            stringStringMap.put("saleType",hashMap1.get(sale_type));
            stringStringMap.put("invoice_type", OmConstant.INVOICE);
            stringStringMap.put("order_type",OmConstant.ORDER);
        }
        if(StringUtils.isNotNull(omOrderMaster.getFlag())){
            String value = OmSupplyType.MAKE_TO_ORDER.getValue();
            String value1 = OmSupplyType.W_W_J_G.getValue();
            String value2 = OmSupplyType.INVENTORY_ALLOCATION.getValue();
            String value3 = OmSupplyType.K_C_G_Y.getValue();
            String value4 = OmSupplyType.W_C_Z_X.getValue();
            if(omOrderMaster.getFlag()){
                for (Map<String, String> stringStringMap : detail) {
                    String supply_type = stringStringMap.get("supply_type");
                    if(supply_type.equals(value) || supply_type.equals(value1) || supply_type.equals(value4)){
                        stringStringMap.put("inv_out_type","0");
                    }else{
                        stringStringMap.put("inv_out_type","2");
                    }
                }
            }else{
                for (Map<String, String> stringStringMap : detail) {
                    String supply_type = stringStringMap.get("supply_type");
                    stringStringMap.put("invoice_type", OmConstant.INVOICE);
                    stringStringMap.put("order_type",OmConstant.ORDER);
                    if(supply_type.equals(value) || supply_type.equals(value1) || supply_type.equals(value4)){
                        stringStringMap.put("inv_in_type","1");
                    }else{
                        stringStringMap.put("inv_in_type","0");
                    }
                }
            }
        }
        return detail;
    }


    @Override
    public OmOrderMaster query(Long uniqueId) {
        List<SysDictData> po_plan_type = iSysDictTypeService.selectDictDataByType(OmConstant.ORDER);
        HashMap<String, String> hashMap = new HashMap<>();
        for (SysDictData sysDictData : po_plan_type) {
            hashMap.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        OmOrderMaster query = omOrderMasterMapper.query(uniqueId);
        query.setWorkType(hashMap.get(query.getWorkType()));
        Map<String,String>  num =  omOrderMasterMapper.selectNum(query.getOmCode());
        Map<String, Object> params = query.getParams();
        params.put("NUM",num);
        return query;

    }


    /**
     * 校验审核单据
     * @param assist
     * @return
     */
    @Override
    public Boolean check(List<OmOrderMaster> list) {
        List<String> collect = list.stream().map(OmOrderMaster::getOmCode).collect(Collectors.toList());
        ArrayList<String> status =  omOrderMasterMapper.selectInvoiceStatusByCode(collect);
             if(status.contains(UserConstants.NOT_UNIQUE) || status.contains(UserConstants.YELLOW)){
                 throw  new CommonException("单据需为开立状态");
             }else{
                 return true;
             }

    }
    @Override
    public Boolean antiCheck(List<OmOrderMaster> list) {
        List<String> collect = list.stream().map(OmOrderMaster::getOmCode).collect(Collectors.toList());
        ArrayList<String> status =  omOrderMasterMapper.selectInvoiceStatusByCode(collect);
            if(status.contains(UserConstants.UNIQUE) || status.contains(UserConstants.YELLOW)){
                throw  new CommonException("单据需为审核状态");
            }else{
                return true;
            }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeWorkStatus(OmOrderMaster omOrderMaster) {
        String omCode = omOrderMaster.getOmCode();
            String workStatus = omOrderMaster.getWorkStatus();
            Date deliverDate = omOrderMaster.getDeliverDate();
            String deliverStatus = omOrderMaster.getDeliverStatus();
        OmOrderMaster omOrderMaster1 = omOrderMasterMapper.selectOmOrderMasterByWoCodeForUpdate(omCode);
            String status = omOrderMaster1.getWorkStatus();
            if(status.equals(WorkStatus.SYSTEM_CLOSING.getValue())){
                throw  new CommonException("系统关闭状态,无法操作");
            }
            Integer i;
            String newStatus;
            if(status.equals(workStatus)){
                newStatus = WorkStatus.NORMAl.getValue();
                i = omOrderMasterMapper.updateWorkStatus_1(WorkStatus.NORMAl.getValue(), omOrderMaster);
            }else{
                newStatus = workStatus;
                i = omOrderMasterMapper.updateWorkStatus_1(workStatus, omOrderMaster);
            }
            omOrderMasterMapper.insertOpertion(omCode,newStatus,deliverDate,deliverStatus,SecurityUtils.getUsername());
            if(i == 0){
                throw new CommonException("操作失败");
            }
    }

    @Override
    public List<Map<String, Object>> selecDetailByWorkType(Map<String, Object> map) {
        List<SysDictData> sysDictData = iSysDictTypeService.selectDictDataByType(OmConstant.ORDER);
        List<SysDictData> sysDictData1 = iSysDictTypeService.selectDictDataByType(OmConstant.SUPPLY);
        HashMap<String, String> stringStringHashMap1 = new HashMap<>();
        for (SysDictData dictData : sysDictData1) {
            stringStringHashMap1.put(dictData.getDictValue(),dictData.getDictLabel());
        }
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        for (SysDictData sysDictDatum : sysDictData) {
            stringStringHashMap.put(sysDictDatum.getDictValue(),sysDictDatum.getDictLabel());
        }
        map.put("supplyType", OmSupplyType.MAKE_TO_ORDER.getValue()+","+OmSupplyType.INVENTORY_ALLOCATION.getValue()+","+OmSupplyType.W_W_J_G.getValue());
        List<Map<String, Object>> maps = omOrderMasterMapper.selectDetailByWorkType(map);
        Collections.reverse(maps);
        for (Map<String, Object> stringObjectMap : maps) {
            stringObjectMap.put("workTypeName",stringStringHashMap.get(stringObjectMap.get("work_type")));
            stringObjectMap.put("supplyTypeName",stringStringHashMap1.get(stringObjectMap.get("supply_type")));
        }
        return maps;
    }

    @Override
    public void changeDate(OmOrderMaster omOrderMaster) {
        String omCode = omOrderMaster.getOmCode();
        OmOrderMaster omOrderMaster1 = omOrderMasterMapper.selectOmOrderMasterByWoCodeForUpdate(omCode);
        if(omOrderMaster1.getWorkStatus().equals(WorkStatus.SYSTEM_CLOSING.getValue())){
            throw  new CommonException("系统关闭状态，无法操作");
        }
        String workStatus = omOrderMaster.getWorkStatus();
        Date deliverDate = omOrderMaster.getDeliverDate();
        String deliverStatus = omOrderMaster.getDeliverStatus();
        omOrderMaster.setUpdateBy(SecurityUtils.getUsername());
        int i = omOrderMasterMapper.changeDate(omOrderMaster);
        if(i == 0){
            throw  new CommonException("更新失败");
        }
        omOrderMasterMapper.insertOpertion(omCode,workStatus,deliverDate,deliverStatus,SecurityUtils.getUsername());
    }

    @Override
    public List<OmOrderDetail> orderDetail(String supplyType) {
        List<OmOrderDetail> omOrderDetails = omOrderMasterMapper.orderDetail(supplyType);
        List<SysDictData> sysDictData = iSysDictTypeService.selectDictDataByType(OmConstant.SALE);
        List<SysDictData> sysDictData1 = iSysDictTypeService.selectDictDataByType(OmConstant.ORDER);
        HashMap<String, String> sales = new HashMap<>();
        for (SysDictData sysDictDatum : sysDictData) {
            sales.put(sysDictDatum.getDictValue(),sysDictDatum.getDictLabel());
        }
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        for (SysDictData dictData : sysDictData1) {
            stringStringHashMap.put(dictData.getDictValue(),dictData.getDictLabel());
        }
        for (OmOrderDetail omOrderDetail : omOrderDetails) {

            omOrderDetail.setSaleType(sales.get(omOrderDetail.getSaleType()));
            omOrderDetail.setWorkType(stringStringHashMap.get(omOrderDetail.getWorkType()));

        }
        return omOrderDetails;
    }


    /**
     * 批量审核
     * @param list
     */
    @Override
    public OmOrderMaster batchCheck(List<OmOrderMaster> list) {
        omOrderMasterMapper.batchCheck(list,SecurityUtils.getUsername());
        int length = list.size();
        if(length == 1){
            return omOrderMasterMapper.selectOmOrderMasterByCode(list.get(0).getOmCode());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmOrderMaster batchAntiCheck(List<OmOrderMaster> list) {
        omOrderMasterMapper.batchAntiCheck(list);
        List<String> collect = list.stream().map(OmOrderMaster::getOmCode).collect(Collectors.toList());
        String[] strings = collect.toArray(new String[collect.size()]);
        ArrayList<OmOrderMaster> omOrderMasters = omOrderMasterMapper.selectOmOrderMasterByCodes(strings);
        for (OmOrderMaster omOrderMaster : omOrderMasters) {
            List<OmOrderSalve> omOrderSalveList = omOrderMaster.getOmOrderSalveList();
            for (OmOrderSalve omOrderSalve : omOrderSalveList) {
                BigDecimal wiQuantityB = omOrderSalve.getWiQuantity();
                BigDecimal wiQuantityR = omOrderSalve.getWiQuantityR();
                BigDecimal mpQuantity = omOrderSalve.getMpQuantity();
                if(mpQuantity.compareTo(BigDecimal.ZERO) > 0){
                    throw  new CommonException("被计划单使用，无法反审核");
                }

                BigDecimal prsQuantity = omOrderSalve.getPrsQuantity();
                if(prsQuantity!=null && prsQuantity.compareTo(BigDecimal.ZERO) > 0){
                    throw  new CommonException("被生产单使用，无法反审核");
                }

                BigDecimal wiAmount= omOrderSalve.getWiAmount();
                if(wiAmount!=null && wiAmount.compareTo(BigDecimal.ZERO) > 0){
                    throw  new CommonException("被收款单使用，无法反审核");
                }

                if(wiQuantityB.compareTo(BigDecimal.ZERO) > 0 || wiQuantityR.compareTo(BigDecimal.ZERO)>0){
                    throw  new CommonException(omOrderMaster.getOmCode() + " : 反审核失败");
                }
            }
        }
        int length = strings.length;
        if(length == 1){
            return omOrderMasterMapper.selectOmOrderMasterByCode(strings[0]);
        }
        return null;
    }


    @Override
    public List<ExportOrder> export(OmOrderMaster omOrderMaster) {
        List<ExportOrder> export = omOrderMasterMapper.export(omOrderMaster);
        List<SysDictData> om_sale_type = iSysDictTypeService.selectDictDataByType("om_sale_type");
        HashMap<String, String> hashMap1 = new HashMap<>();
        for (SysDictData sysDictData : om_sale_type) {
            hashMap1.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        for (ExportOrder exportOrder : export) {
            exportOrder.setSaleType(hashMap1.get(exportOrder.getSaleType()));
        }
        return export;
    }

    @Override
    public List<Map<String, String>> chainDetail(OmOrderSalve omOrderSalve) {
        List<Map<String, String>> maps1 = omDeliverMasterService.linkDetail(omOrderSalve.getUniqueId());
        return maps1;
    }

    @Override
    public List<OmOrderReport> orderReport(OmOrderReport omOrderReport) {
        List<OmOrderReport> list =   omOrderMasterMapper.selectOrderReport(omOrderReport);
        List<InvSortDTO> invSorts = invSortService.selectInvSortDTOList(new InvSortDTO());
        List<SysDictData> po_invoice_type = iSysDictTypeService.selectDictDataByType(OmConstant.INVOICE);
        List<SysDictData> po_plan_type = iSysDictTypeService.selectDictDataByType(OmConstant.ORDER);
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
        for (OmOrderReport orderReport : list) {
            for (InvSortDTO invSort : invSorts) {
                if(orderReport.getInvSortId().equals(invSort.getSortId().toString())){
                    orderReport.setInvSortId(invSort.getSortName());
                }
                if(orderReport.getInvSortRoot().equals(invSort.getSortId().toString())){
                    orderReport.setInvSortRoot(invSort.getSortName());
                }
            }
            orderReport.setWorkDept(deptMap.get(orderReport.getWorkDept()));
            orderReport.setInvoiceType(poInvoiceType.get(orderReport.getInvoiceType()));
            orderReport.setWorkType(hashMap1.get(orderReport.getWorkType()));
            orderReport.setInvoiceStatus(hashMap12.get(orderReport.getInvoiceStatus()));
            orderReport.setWorkStatus(hashMap.get(orderReport.getWorkStatus()));
            orderReport.setSaleType(hashMap3.get(orderReport.getSaleType()));
        }
        return list;
    }

    @Override
    public List<OmOrderRank> rank(OmOrderRank omOrderRank) {
         List<OmOrderRank> list  =  omOrderMasterMapper.rank(omOrderRank);
        List<InvSortDTO> invSorts = invSortService.selectInvSortDTOList(new InvSortDTO());
        for (OmOrderRank omRank : list) {
            for (InvSortDTO invSort : invSorts) {
                if(omRank.getInvSortId().equals(invSort.getSortId().toString())){
                    omRank.setInvSortId(invSort.getSortName());
                }
                if(omRank.getInvSortRoot().equals(invSort.getSortId().toString())){
                    omRank.setInvSortRoot(invSort.getSortName());
                }
            }
        }
        return list;


    }

    @Override
    public List<OmWorkStaffStatistics> statistics(OmWorkStaffStatistics omWorkStaffStatistics) {
       List<OmWorkStaffStatistics> list =   omOrderMasterMapper.statistics(omWorkStaffStatistics);
        List<SysDept> sysDepts = iSysDeptService.selectDeptList(new SysDept());
        HashMap<String, String> deptMap = new HashMap<>();
        for (SysDept dept : sysDepts) {
            deptMap.put(dept.getDeptId().toString(),dept.getDeptName());
        }
        for (OmWorkStaffStatistics omStatistics : list) {
            omStatistics.setWorkDept(deptMap.get(omStatistics.getWorkDept()));
        }
        return list;
    }


    /**
     * 新增销售订单从信息
     * 
     * @param omOrderMaster 销售订单对象
     */
    public void insertOmOrderSalve(OmOrderMaster omOrderMaster)
    {
        List<OmOrderSalve> omOrderSalveList = omOrderMaster.getOmOrderSalveList();
        String uniqueId = omOrderMaster.getOmCode();
        if (StringUtils.isNotNull(omOrderSalveList))
        {
            List<OmOrderSalve> list = new ArrayList<>();
            for (OmOrderSalve omOrderSalve : omOrderSalveList)
            {
                omOrderSalve.setCreateBy(omOrderMaster.getCreateBy());
                omOrderSalve.setOmCode(uniqueId);
                list.add(omOrderSalve);
            }
            if (list.size() > 0)
            {
                omOrderMasterMapper.batchOmOrderSalve(list);
            }
        }
    }

    public OmOrderMaster selectOmOrderMasterForUpdate(String woCode) {
        return  omOrderMasterMapper.selectOmOrderMasterByWoCodeForUpdate(woCode);
    }

    public OmOrderSalve selectOmOrderSalveById(Long woUniqueId) {
        return  omOrderMasterMapper.selectOmOrderSalveById(woUniqueId);
    }

    public void changeQuantityMp(Long woUniqueId, BigDecimal quantity) {
        omOrderMasterMapper.changeQuantityMp(woUniqueId,quantity);
    }

    public void changeQuantityPrs(Long woUniqueId, BigDecimal quantity){
        omOrderMasterMapper.changeQuantityPrs(woUniqueId, quantity);
    }

    public List<Map<String, String>> leadIntoPrs(OmOrderReport mpOrderSalve){
        List<Map<String,String>>  list  =  omOrderMasterMapper.leadIntoPrs(mpOrderSalve);
        for (Map<String, String> stringStringMap : list) {
            stringStringMap.put("invoice_type",OmConstant.INVOICE);
            stringStringMap.put("work_type",OmConstant.ORDER);
        }
        return list;
    }

    public List<Map<String, String>> leadPrs(OmOrderReport omOrderSalve) {
        List<Map<String,String>>  list  =  omOrderMasterMapper.leadMp(omOrderSalve);
        List<SysDictData> sysDictData = iSysDictTypeService.selectDictDataByType(OmConstant.ORDER);
        HashMap<String, String> map1 = new HashMap<>();
        for (SysDictData sysDictDatum : sysDictData) {
            map1.put(sysDictDatum.getDictValue(),sysDictDatum.getDictLabel());
        }
        for (Map<String, String> stringStringMap : list) {
            String supply_type = stringStringMap.get("supply_type");
            if(supply_type.equals("0") || supply_type.equals("3")||supply_type.equals("4")){
                stringStringMap.put("inv_in_type","1");
            }else{
                stringStringMap.put("inv_in_type","0");
            }
            stringStringMap.put("work_type",map1.get(stringStringMap.get("work_type")));
        }
        return list ;
    }

    public List<Map<String,String>> leadIntoMp(OmOrderReport omOrderSalve) {
       List<Map<String,String>>  list  =  omOrderMasterMapper.leadIntoMp(omOrderSalve);
        for (Map<String, String> stringStringMap : list) {
            stringStringMap.put("invoice_type",OmConstant.INVOICE);
            stringStringMap.put("work_type",OmConstant.ORDER);
        }
        return list;
    }

    public List<Map<String, String>> leadMp(OmOrderReport omOrderSalve) {
        List<Map<String,String>>  list  =  omOrderMasterMapper.leadMp(omOrderSalve);
        List<SysDictData> sysDictData = iSysDictTypeService.selectDictDataByType(OmConstant.ORDER);
        HashMap<String, String> map1 = new HashMap<>();
        for (SysDictData sysDictDatum : sysDictData) {
            map1.put(sysDictDatum.getDictValue(),sysDictDatum.getDictLabel());
        }
        for (Map<String, String> stringStringMap : list) {
            String supply_type = stringStringMap.get("supply_type");
            if(supply_type.equals("0") || supply_type.equals("3")||supply_type.equals("4")){
                stringStringMap.put("inv_in_type","1");
            }else{
                stringStringMap.put("inv_in_type","0");
            }
            stringStringMap.put("work_type",map1.get(stringStringMap.get("work_type")));
        }
        return list ;
    }

    @Override
    public OmOrderSalve selectOrderSalveBySalve(OmOrderSalve salve){
        return omOrderMasterMapper.selectOrderSalveBySalve(salve);
    }

    @Override
    public Map<String ,String> pumpCustomerByPpNumber(String ppNumber) {
        return omOrderMasterMapper.selectCustomerNameByPpNumber(ppNumber);
    }

    @Override
    public Map<String, String> queryOrderSalveByUniqueId(Long woUniqueId) {
        OmOrderSalve orderSalve= omOrderMasterMapper.queryOrderSalveByUniqueId(woUniqueId);
        List<SysDictData> data1 = iSysDictTypeService.selectDictDataByType("inv_pack_type");
        HashMap<String, String> map1 = new HashMap<>();
        for (SysDictData sysDictDatum : data1) {
            map1.put(sysDictDatum.getDictValue(),sysDictDatum.getDictLabel());
        }
        List<SysDictData> data2 = iSysDictTypeService.selectDictDataByType("inv_pack_unit");
        HashMap<String, String> map2 = new HashMap<>();
        for (SysDictData sysDictDatum : data2) {
            map2.put(sysDictDatum.getDictValue(),sysDictDatum.getDictLabel());
        }

        orderSalve.setMinType(map2.get(orderSalve.getMinType()));
        orderSalve.setPieceType(map1.get(orderSalve.getPieceType()));

        try {
            Map<String, String> map = BeanUtils.describe(orderSalve);
            if(orderSalve.getPiece()==null)map.put("pieceInt", "0");
            else map.put("pieceInt", String.valueOf(orderSalve.getPiece().intValue()));

            if(orderSalve.getMinNumber()==null)map.put("minInt", "0");
            else map.put("minInt", String.valueOf(orderSalve.getMinNumber().intValue()));
            return map;
        }catch (Exception e){ e.printStackTrace( );}

        return null;
    }
}
