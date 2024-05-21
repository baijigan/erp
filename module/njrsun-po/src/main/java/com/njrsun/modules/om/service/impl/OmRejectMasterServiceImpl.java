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
import com.njrsun.modules.om.enums.WorkStatus;
import com.njrsun.modules.om.mapper.OmDeliverMasterMapper;
import com.njrsun.modules.om.mapper.OmOrderMasterMapper;
import com.njrsun.modules.om.mapper.OmRejectMasterMapper;
import com.njrsun.modules.om.service.IOmRejectMasterService;
import com.njrsun.modules.om.domain.*;
import com.njrsun.system.service.ISysCoderService;
import com.njrsun.system.service.ISysDeptService;
import com.njrsun.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 销售订单主Service业务层处理
 * 
 * @author njrsun
 * @date 2021-08-31
 */
@Service
public class OmRejectMasterServiceImpl implements IOmRejectMasterService 
{
    @Autowired
    private OmDeliverMasterMapper omDeliverMasterMapper;
    @Autowired
    private OmRejectMasterMapper omRejectMasterMapper;
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
    private OmDeliverMasterServiceImpl omDeliverMasterService;

    /**
     * 查询销售订单主
     * 
     * @param omRejectMaster 销售订单主ID
     * @return 销售订单主
     */
    @Override
    public OmRejectMaster selectOmRejectMasterByCode(OmRejectMaster omRejectMaster)
    {
        return omRejectMasterMapper.selectOmRejectMasterByMaster(omRejectMaster);
    }

    /**
     * 查询销售订单主列表
     * 
     * @param omRejectMaster 销售订单主
     * @return 销售订单主
     */
    @Override
    public List<OmRejectMaster> selectOmRejectMasterList(OmRejectMaster omRejectMaster)
    {


        List<OmRejectMaster> omRejectMasters = omRejectMasterMapper.selectOmRejectMasterList(omRejectMaster);
        List<SysDictData> po_invoice_type = iSysDictTypeService.selectDictDataByType(OmConstant.INVOICE);
        List<SysDictData> po_plan_type = iSysDictTypeService.selectDictDataByType(OmConstant.REJECT);
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
        for (OmRejectMaster omRejectMaster1 : omRejectMasters) {
            Map<String, Object> params = omRejectMaster1.getParams();
            params.put(OmConstant.INVOICE,poInvoiceType.get(omRejectMaster1.getInvoiceType()));
            params.put(OmConstant.REJECT,hashMap.get(omRejectMaster1.getWorkType()));
            params.put("sys_work_status",hashMap1.get(omRejectMaster1.getWorkStatus()));
            params.put("sys_invoice_status",hashMap12.get(omRejectMaster1.getInvoiceStatus()));
            params.put("deptName",deptMap.get(omRejectMaster1.getWorkDept()));
        }
        return omRejectMasters;
    }

    /**
     * 新增销售订单主
     * 
     * @param omRejectMaster 销售订单主
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertOmRejectMaster(OmRejectMaster omRejectMaster)
    {
        omRejectMaster.setOmCode(iSysCoderService.generate(OmConstant.REJECT,omRejectMaster.getWorkType()));
        omRejectMaster.setCreateBy(SecurityUtils.getUsername());
        int rows = omRejectMasterMapper.insertOmRejectMaster(omRejectMaster);
        insertOmRejectSalve(omRejectMaster);
        return rows;
    }

    /**
     * 修改销售订单主
     * 
     * @param omRejectMaster 销售订单主
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateOmRejectMaster(OmRejectMaster omRejectMaster)
    {
        OmRejectMaster omRejectMaster1 = omRejectMasterMapper.selectOmRejectMasterByCodeForUpdate(omRejectMaster.getOmCode());


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



        if(omRejectMaster1.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("审核单据，无法删除");
        }
        /** 老Id */
        ArrayList<Long> oldId = omRejectMasterMapper.selectomRejectSalveByCode(omRejectMaster.getOmCode());
        /**  新数据  */
        List<OmRejectSalve> omRejectSalveList = omRejectMaster.getOmRejectSalveList();
        /** 新ID */
        ArrayList<Long> newId = new ArrayList<>();
        ArrayList<OmRejectSalve> newData = new ArrayList<>();
        ArrayList<OmRejectSalve> editData = new ArrayList<>();
        for (OmRejectSalve omRejectSalve : omRejectSalveList) {
            if(omRejectSalve.getUniqueId() == null){
                //新增数据
                omRejectSalve.setCreateBy(SecurityUtils.getUsername());
                omRejectSalve.setOmCode(omRejectMaster.getOmCode());
                newData.add(omRejectSalve);
            }
            else{
                newId.add(omRejectSalve.getUniqueId());
            }
        }
        omRejectMaster.setUpdateBy("admin");
        // 删除ID
        List<Long> delId = oldId.stream().filter(t -> !newId.contains(t)).collect(Collectors.toList());


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

        //修改ID
        oldId.retainAll(newId);

        for (Long aLong : oldId) {
            for (OmRejectSalve omRejectSalve : omRejectSalveList) {
                if(omRejectSalve.getUniqueId().equals(aLong)){
                    omRejectSalve.setUpdateBy(omRejectSalve.getUpdateBy());
                    editData.add(omRejectSalve);
                    break;
                }
            }
        }
        Long[] longs = delId.toArray(new Long[delId.size()]);
        if(longs.length != 0){
            omRejectMasterMapper.deleteOmRejectSalveByIds(longs);
        }
        if(newData.size() != 0){
            omRejectMasterMapper.batchOmRejectSalve(newData);
        }
        int j=1;
        if(editData.size() != 0){
            j = omRejectMasterMapper.updateOmRejectSlave(editData);
        }
        int i = omRejectMasterMapper.updateOmRejectMaster(omRejectMaster);
        if(i == 0 || j == 0){
            throw  new CommonException("修改失败");
        }

        return i;
    }

    /**
     * 批量删除销售订单主
     * 
     * @param list 需要删除的销售订单主ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteOmRejectMasterByCodes(List<OmRejectMaster> list)
    {
        List<String> collect = list.stream().map(OmRejectMaster::getOmCode).collect(Collectors.toList());
        String[] strings1 = collect.toArray(new String[collect.size()]);
        List<String> strings = omRejectMasterMapper.selectOmRejectInvoiceStatusByCodes(strings1);
        if(strings.contains(UserConstants.NOT_UNIQUE)){
            throw  new CommonException("审核状态下，单据无法删除");
        }
        omRejectMasterMapper.deleteOmRejectSalveByOmCodes(strings1);
        return omRejectMasterMapper.deleteOmRejectMasterByIds(list);
    }

    /**
     * 删除销售订单主信息
     * 
     * @param uniqueId 销售订单主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOmRejectMasterById(Long uniqueId)
    {
        omRejectMasterMapper.deleteOmRejectSalveByOmCode(uniqueId);
        return omRejectMasterMapper.deleteOmRejectMasterById(uniqueId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmRejectMaster batchCheck(List<OmRejectMaster> list) {
        List<String> collect = list.stream().map(OmRejectMaster::getOmCode).collect(Collectors.toList());
        String[] strings = collect.toArray(new String[collect.size()]);
        List<String> result =   omRejectMasterMapper.selectOmRejectInvoiceStatusByCodes(strings);
         if(result.contains(UserConstants.NOT_UNIQUE)){
             throw  new CommonException("存在审核单据");
         }else if (result.contains(UserConstants.YELLOW)){
             throw  new CommonException("存在退回单据");
         }
        for (OmRejectMaster s : list) {
            OmRejectMaster omRejectMaster = omRejectMasterMapper.selectOmRejectMasterByCodeForUpdate(s.getOmCode());
           List<OmRejectSalve> omRejectSalveList =    omRejectMasterMapper.selectOmOrderSalveByOmCode(s.getOmCode());
            for (OmRejectSalve omRejectSalve : omRejectSalveList) {
                if(StringUtils.isNotNull(omRejectSalve.getWoInvoiceId()) && !omRejectSalve.getWoUniqueId().equals(0L)){
                    OmDeliverMaster omOrderMaster = omDeliverMasterService.selectOmDeliverMasterByCodeForUpdate(omRejectSalve.getWoCode());
                    if(!omOrderMaster.getInvoiceStatus().equals(UserConstants.NOT_UNIQUE)){
                        throw  new CommonException("审核失败，上游单据非审核状态！");
                    }
                    OmDeliverSalve omDeliverSalve = omDeliverMasterService.selectOmDeliverSalveById(omRejectSalve.getWoUniqueId());
                    Long woUniqueId = omRejectSalve.getWoUniqueId();
                    BigDecimal quantity = omRejectSalve.getQuantity();
                    BigDecimal add = quantity.add(omDeliverSalve.getWiQuantityR());
                    if(add.compareTo(omDeliverSalve.getQuantity())>0){
                        throw  new CommonException("审核失败,数量不足");
                    }
                    omDeliverMasterMapper.changeQuantity_r(woUniqueId,omRejectSalve.getWoCode(),quantity);
                }
            }

            int i = omRejectMasterMapper.batchCheck(s, SecurityUtils.getUsername(), UserConstants.NOT_UNIQUE);
            if(i == 0){
                throw  new CommonException("审核失败");
            }
        }
        int length = strings.length;
        if(length == 1){
            return omRejectMasterMapper.selectOmRejectMasterByCode(strings[0]);
        }else{
            return null;
        }

    }


    @Override
    public List<Map<String, String>> getDetail(OmRejectMaster omRejectMaster) {
         omRejectMaster.setCustomer(omRejectMaster.getCustomer().trim());
         omRejectMaster.setWorkStaff(omRejectMaster.getWorkStaff().trim());
        List<Map<String, String>> detail = omRejectMasterMapper.getDetail(omRejectMaster);
        List<SysDictData> po_plan_type = iSysDictTypeService.selectDictDataByType(OmConstant.REJECT);
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
        }
        return detail;
    }

    @Override
    public OmRejectMaster batchAntiCheck(List<OmRejectMaster> list) {
        List<String> collect = list.stream().map(OmRejectMaster::getOmCode).collect(Collectors.toList());
        String[] strings = collect.toArray(new String[collect.size()]);
        /* 锁 */
        List<OmRejectMaster> omRejectMasters = omRejectMasterMapper.selectOmRejectMasterByCodes(strings);
        for (OmRejectMaster omRejectMaster : omRejectMasters) {
            if (!omRejectMaster.getInvoiceStatus().equals(UserConstants.NOT_UNIQUE)) {
                throw new CommonException("非审核单据无法进行反审核");
            }
        }
            for (OmRejectMaster rejectMaster : list) {
                OmRejectMaster master = omRejectMasterMapper.selectOmRejectMasterByCodeForUpdate(rejectMaster.getOmCode());
                List<OmRejectSalve> omRejectSalveList =    omRejectMasterMapper.selectOmOrderSalveByOmCode(rejectMaster.getOmCode());
                for (OmRejectSalve omRejectSalve : omRejectSalveList) {
                    BigDecimal wiQuantityB = omRejectSalve.getWiQuantity();
                    BigDecimal wiQuantityR = omRejectSalve.getWiQuantityR();
                    if(wiQuantityB.compareTo(BigDecimal.ZERO) > 0 || wiQuantityR.compareTo(BigDecimal.ZERO) > 0){
                        throw  new CommonException("单据已被使用,无法反审核");
                    }
                    if(StringUtils.isNotNull(omRejectSalve.getWoInvoiceId()) && !omRejectSalve.getWoUniqueId().equals(0L)){
                        OmDeliverMaster omOrderMaster = omDeliverMasterService.selectOmDeliverMasterByCodeForUpdate(omRejectSalve.getWoCode());
                        if(!omOrderMaster.getInvoiceStatus().equals(UserConstants.NOT_UNIQUE)){
                            throw  new CommonException("审核失败，上游单据非审核状态");
                        }
                        OmDeliverSalve omDeliverSalve = omDeliverMasterService.selectOmDeliverSalveById(omRejectSalve.getWoUniqueId());
                        BigDecimal wiQuantityR1 = omDeliverSalve.getWiQuantityR();
                        BigDecimal quantity = omRejectSalve.getQuantity();
                        Long id = omRejectSalve.getWoUniqueId();
                        String woCode = omRejectSalve.getWoCode();
                        if(wiQuantityR1.compareTo(quantity) < 0 ){
                            throw  new CommonException("反审核失败，无法退货");
                        }
                        omDeliverMasterMapper.changeQuantity_r(id,woCode,quantity.negate());
                    }
                }
                int i = omRejectMasterMapper.batchCheck(rejectMaster, null, UserConstants.YELLOW);
                if(i ==0){
                    throw  new CommonException("反审核失败");
                }
            }
        int length = strings.length;
        if(length == 1 ){
            return omRejectMasterMapper.selectOmRejectMasterByCode(strings[0]);
        }
        return null;

    }

    @Override
    public OmRejectMaster gettNextOrLast(OmRejectMaster omRejectMaster) {
        Long uniqueId = omRejectMaster.getUniqueId();
        Boolean type = omRejectMaster.getType();
        ArrayList<Long> list = omRejectMasterMapper.selectOmRejectMasterId(omRejectMaster);
        for (int i = 0; i < list.size(); i++) {
            Long aLong = list.get(i);

            if(aLong.equals(uniqueId) && i == list.size()-1 &&  type){
                throw  new CommonException("已经是最后的单据");
            }else if (aLong.equals(uniqueId) && i == 0 && !type){
                throw  new CommonException("已经是最后的单据");
            }else if(aLong.equals(uniqueId)){
                return  type ? omRejectMasterMapper.selectOmRejectMasterById(list.get(i+1)) : omRejectMasterMapper.selectOmRejectMasterById(list.get(i-1));
            }
        }
        return null;
    }

    @Override
    public List<ExportReject> export(OmRejectMaster omRejectMaster) {
        return omRejectMasterMapper.export(omRejectMaster);
    }

    @Override
    public List<Map<String, String>> linkDetail(Long uniqueId) {
        List<Map<String, String>> maps = omRejectMasterMapper.linkDetail(uniqueId);
        for (Map<String, String> map : maps) {
            map.put("type",OmConstant.REJECT);
            map.put("invoice",OmConstant.INVOICE);
        }

        return iSysDictTypeService.transInvoiceWorkType(maps);
    }

    @Override
    public List<OmRejectReport> rejectReport(OmRejectReport omRejectReport) {
        List<OmRejectReport> list  =   omRejectMasterMapper.rejectReport(omRejectReport);
        List<InvSortDTO> invSorts = invSortService.selectInvSortDTOList(new InvSortDTO());
        List<SysDictData> po_invoice_type = iSysDictTypeService.selectDictDataByType(OmConstant.INVOICE);
        List<SysDictData> po_plan_type = iSysDictTypeService.selectDictDataByType(OmConstant.REJECT);
        List<SysDictData> sys_work_status = iSysDictTypeService.selectDictDataByType("sys_work_status");
        List<SysDictData> sys_invoice_status = iSysDictTypeService.selectDictDataByType("sys_invoice_status");
        List<SysDept> sysDepts = iSysDeptService.selectDeptList(new SysDept());
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
        for (OmRejectReport omRejectReport1 : list) {
            for (InvSortDTO invSort : invSorts) {
                if(omRejectReport1.getInvSortId().equals(invSort.getSortId().toString())){
                    omRejectReport1.setInvSortId(invSort.getSortName());
                }
                if(omRejectReport1.getInvSortRoot().equals(invSort.getSortId().toString())){
                    omRejectReport1.setInvSortRoot(invSort.getSortName());
                }
            }
            omRejectReport1.setWorkDept(deptMap.get(omRejectReport1.getWorkDept()));
            omRejectReport1.setInvoiceType(poInvoiceType.get(omRejectReport1.getInvoiceType()));
            omRejectReport1.setWorkType(hashMap1.get(omRejectReport1.getWorkType()));
            omRejectReport1.setInvoiceStatus(hashMap12.get(omRejectReport1.getInvoiceStatus()));
            omRejectReport1.setWorkStatus(hashMap.get(omRejectReport1.getWorkStatus()));
        }


        return list;
    }

    @Override
    public List<Map<String, String>> chainDetail(OmRejectSalve omRejectSalve) {
        List<Map<String, String>> maps = omRejectMasterMapper.selectOmRejectSalveWoById(omRejectSalve.getUniqueId());
        return iSysDictTypeService.transInvoiceWorkType(maps);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeWorkStatus(OmRejectMaster omRejectMaster) {
        String omCode = omRejectMaster.getOmCode();
        String workStatus = omRejectMaster.getWorkStatus();
        OmRejectMaster omRejectMaster1 = omRejectMasterMapper.selectOmRejectMasterByCodeForUpdate(omCode);
        if(omRejectMaster1.getWorkStatus().equals(WorkStatus.SYSTEM_CLOSING.getValue())){
            throw  new CommonException("系统关闭状态,无法操作");
        }
        Integer i;
        if(omRejectMaster1.getWorkStatus().equals(workStatus)){

            i = omRejectMasterMapper.updateWorkStatus(WorkStatus.NORMAl.getValue(), omRejectMaster);

        }else {

            i = omRejectMasterMapper.updateWorkStatus(workStatus, omRejectMaster);
        }
        if(i == 0){
            throw  new CommonException("操作失败");
        }

    }

    /**
     * 新增销售退货从信息
     * 
     * @param omRejectMaster 销售订单主对象
     */
    public void insertOmRejectSalve(OmRejectMaster omRejectMaster)
    {
        List<OmRejectSalve> omRejectSalveList = omRejectMaster.getOmRejectSalveList();
        String omCode = omRejectMaster.getOmCode();
        if (StringUtils.isNotNull(omRejectSalveList))
        {
            List<OmRejectSalve> list = new ArrayList<OmRejectSalve>();
            for (OmRejectSalve omRejectSalve : omRejectSalveList)
            {
                omRejectSalve.setCreateBy(omRejectMaster.getCreateBy());
                omRejectSalve.setOmCode(omCode);
                list.add(omRejectSalve);
            }
            if (list.size() > 0)
            {
                omRejectMasterMapper.batchOmRejectSalve(list);
            }
        }
    }

    public OmRejectSalve selectOmRejectSalveById(Long uniqueId){
        return omRejectMasterMapper.selectOmRejectSalveById(uniqueId);
    }

    public List<Map<String, String>> lead(Map<String, String> query) {
        return omRejectMasterMapper.lead(query);
    }

    public List<Map<String, String>> leadInto(Map<String, String> map) {
        List<Map<String,String>> list =  omRejectMasterMapper.leadInto(map);
        for (Map<String, String> stringStringMap : list) {
            stringStringMap.put("invoice_type",OmConstant.INVOICE);
            stringStringMap.put("work_type",OmConstant.REJECT);
        }
        return list;
    }

    @Override
    public void systemOpenClose(String woCode, String i) {
        omRejectMasterMapper.systemOpenClose(woCode, i);
    }

    public OmRejectSalve selectOmRejectSalveByIdForUpdate(Long batchUniqueId) {
        return omRejectMasterMapper.selectOmRejectSalveForUpdate(batchUniqueId);
    }

    public void changeQuantity(BigDecimal amount, String woUniqueId) {
        omRejectMasterMapper.changeQuantity(amount, woUniqueId);
    }

    @Override
    public OmRejectMaster selectOmRejectMasterForUpdate(String woCode) {
        return omRejectMasterMapper.selectOmRejectMasterForUpdate(woCode);
    }
}
