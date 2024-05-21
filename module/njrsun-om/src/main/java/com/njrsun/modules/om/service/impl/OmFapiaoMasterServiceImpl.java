package com.njrsun.modules.om.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.njrsun.api.fd.service.IFdOmFapiaoApiService;
import com.njrsun.api.fd.service.IFdPeriodApiService;
import com.njrsun.common.constant.Constants;
import com.njrsun.common.core.domain.entity.SysDept;
import com.njrsun.common.core.domain.entity.SysDictData;
import com.njrsun.common.dto.InvSortDTO;
import com.njrsun.common.dto.WmWarehouseDTO;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.om.constant.OmConstant;
import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.enums.WorkStatus;
import com.njrsun.modules.om.mapper.OmFapiaoMasterMapper;
import com.njrsun.modules.om.service.IOmFapiaoMasterService;
import com.njrsun.api.wm.service.IWmOutApiService;
import com.njrsun.system.mapper.SysDeptMapper;
import com.njrsun.system.service.ISysDeptService;
import com.njrsun.system.service.ISysDictTypeService;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 销售开票主Service业务层处理
 * 
 * @author njrsun
 * @date 2022-04-04
 */
@Service
public class OmFapiaoMasterServiceImpl implements IOmFapiaoMasterService
{
    @Autowired
    private OmFapiaoMasterMapper omFapiaoMasterMapper;

    @Autowired
    private SysCoderServiceImpl sysCoderService;

    @Autowired
    private ISysDictTypeService iSysDictTypeService;

    @Autowired
    private ISysDeptService iSysDeptService;

    @Autowired
    private IWmOutApiService wmOutApiService;

    @Autowired
    private IFdOmFapiaoApiService fdOmFapiaoApiService;

    @Autowired
    private IFdPeriodApiService fdPeriodApiService;

    @Autowired
    private SysDeptMapper sysDeptMapper;
    /**
     * 查询销售开票主
     * 
     * @param omCode 销售开票主ID
     * @return 销售开票主
     */
    @Override
    public OmFapiaoMaster selectOmFapiaoMasterById(String omCode)
    {
        return omFapiaoMasterMapper.selectOmFapiaoMasterById(omCode);
    }

    /**
     * 查询销售开票主列表
     * 
     * @param omFapiaoMaster 销售开票主
     * @return 销售开票主
     */
    @Override
    public List<OmFapiaoMaster> selectOmFapiaoMasterList(OmFapiaoMaster omFapiaoMaster)
    {
        List<OmFapiaoMaster> omFapiaoMasters = omFapiaoMasterMapper.selectOmFapiaoMasterList(omFapiaoMaster);

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

        return omFapiaoMasters;
    }

    /**
     * 新增销售开票主
     * 
     * @param omFapiaoMaster 销售开票主
     * @return 结果
     */
    @Transactional
    @Override
    public int insertOmFapiaoMaster(OmFapiaoMaster omFapiaoMaster)
    {
        omFapiaoMaster.setCreateBy(SecurityUtils.getUsername());
        omFapiaoMaster.setOmCode(sysCoderService.generate("om_fapiao_type",omFapiaoMaster.getWorkType()));
        int rows = omFapiaoMasterMapper.insertOmFapiaoMaster(omFapiaoMaster);

        insertOmFapiaoSalve(omFapiaoMaster);
        return rows;
    }

    /**
     * 修改销售开票主
     * 
     * @param omFapiaoMaster 销售开票主
     * @return 结果
     */
    @Transactional
    @Override
    public int updateOmFapiaoMaster(OmFapiaoMaster omFapiaoMaster)
    {
        omFapiaoMasterMapper.deleteOmFapiaoSalveByOmCode(omFapiaoMaster.getOmCode());
        insertOmFapiaoSalve(omFapiaoMaster);
        int i = omFapiaoMasterMapper.updateOmFapiaoMaster(omFapiaoMaster);
        if(i == 0){
            throw  new CommonException("更新失败");
        }
    return i;
    }

    /**
     * 批量删除销售开票主
     * 
     * @param omCodes 需要删除的销售开票主ID
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteOmFapiaoMasterByIds(List<String> omCodes)
    {
        omFapiaoMasterMapper.deleteOmFapiaoSalveByOmCodes(omCodes);
        return omFapiaoMasterMapper.deleteOmFapiaoMasterByIds(omCodes);
    }

    @Override
    public int deleteOmFapiaoMasterById(Long uniqueId) {

        return 0;
    }

    /**
     * 删除销售开票主信息
     * 
     * @param uniqueId 销售开票主ID
     * @return 结果
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmFapiaoMaster check(List<OmFapiaoMaster> list) {
        list.forEach( r ->{
            String omCode = r.getOmCode();
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
            omFapiaoMasterMapper.updateCheckStatus(omCode,"1",SecurityUtils.getUsername(),new Date());
        });

        if(list.size() == 1){
            return omFapiaoMasterMapper.selectOmFapiaoMasterById(list.get(0).getOmCode());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmFapiaoMaster antiCheck(List<OmFapiaoMaster> list) {
        list.forEach( r ->{
            String omCode = r.getOmCode();
            OmFapiaoMaster ex =   omFapiaoMasterMapper.selectOmFapiaoMasterForUpdate(omCode);
            if(!ex.getInvoiceStatus().equals("1")){
                throw  new CommonException(omCode + "非审核状态，单据无法反审核");
            }

            if(ex.getVatStatus().equals("1")){
                throw  new CommonException(omCode + "发票已入账，单据无法反审核");
            }

            OmFapiaoMaster omFapiaoMaster = omFapiaoMasterMapper.selectOmFapiaoMasterById(omCode);
            omFapiaoMaster.getOmFapiaoSalveList().forEach(e ->{
                Long woUniqueId = e.getWoUniqueId();
                if(woUniqueId!=0 && woUniqueId!=null) {
                    Map<String, Object>  master= wmOutApiService.selectWmOutMasterForUpdate(e.getWoCode());
                    if(!master.get("invoice_status").equals(Constants.CHECK)){
                        throw  new CommonException(e.getWoCode() + "非审核状态，单据无法反审核");
                    }

                    Map<String, Object> result = wmOutApiService.selectWmOutSalveById(woUniqueId);
                    BigDecimal vat_quantity = (BigDecimal) result.get("vat_quantity");
                    if (vat_quantity.compareTo(e.getVatQuantity()) < 0) {
                        throw new CommonException("反审核失败，" + e.getInvName() + "数量不足");
                    } else {
                        wmOutApiService.changeQuantityVat(woUniqueId, e.getVatQuantity().negate());
                    }
                }
            });
            omFapiaoMasterMapper.updateCheckStatus(omCode,"2",null,null);
        });

        if(list.size() == 1){
            return omFapiaoMasterMapper.selectOmFapiaoMasterById(list.get(0).getOmCode());
        }
        return null;
    }

    /**
     * 新增销售开票从信息
     * 
     * @param omFapiaoMaster 销售开票主对象
     */
    public void insertOmFapiaoSalve(OmFapiaoMaster omFapiaoMaster)
    {
        List<OmFapiaoSalve> omFapiaoSalveList = omFapiaoMaster.getOmFapiaoSalveList();
        String omCode = omFapiaoMaster.getOmCode();
        if (StringUtils.isNotNull(omFapiaoSalveList))
        {
            List<OmFapiaoSalve> list = new ArrayList<OmFapiaoSalve>();
            for (OmFapiaoSalve omFapiaoSalve : omFapiaoSalveList)
            {
                omFapiaoSalve.setCreateBy(omFapiaoMaster.getCreateBy());
                omFapiaoSalve.setOmCode(omCode);
                list.add(omFapiaoSalve);
            }
            if (list.size() > 0)
            {
                omFapiaoMasterMapper.batchOmFapiaoSalve(list);
            }
        }
    }

    public void updateStatus(String omCode, String s) {

        omFapiaoMasterMapper.updateStatus(omCode,s);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeWorkStatus(OmFapiaoMaster omFapiaoMaster){
        String omCode = omFapiaoMaster.getOmCode();
        String workStatus = omFapiaoMaster.getWorkStatus();
        OmFapiaoMaster omFapiaoMaster1 = omFapiaoMasterMapper.selectOmFapiaoMasterByCodeForUpdate(omCode);
        if(omFapiaoMaster1.getWorkStatus().equals(WorkStatus.SYSTEM_CLOSING.getValue())){
            throw  new CommonException("系统关闭状态,无法操作");
        }
        Integer i;
        if(omFapiaoMaster1.getWorkStatus().equals(workStatus)){
            i = omFapiaoMasterMapper.updateWorkStatus(WorkStatus.NORMAl.getValue(), omFapiaoMaster);
        }else{
            i = omFapiaoMasterMapper.updateWorkStatus(workStatus, omFapiaoMaster);
        }
        if(i == 0 ){
            throw  new CommonException("操作失败");
        }
    }

    @Override
    public OmFapiaoMaster selectOmFapiaoMasterByCode(OmFapiaoMaster omFapiaoMaster){
        return omFapiaoMasterMapper.selectOmFapiaoMasterByMaster(omFapiaoMaster);
    }

    @Override
    public List<OmFapiaoReport> fapiaoReport(OmFapiaoReport omFapiaoReport){
        List<OmFapiaoReport> list =   omFapiaoMasterMapper.selectFapiaoReport(omFapiaoReport);
        //List<InvSortDTO> invSorts = invSortService.selectInvSortDTOList(new InvSortDTO());
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
        for (OmFapiaoReport fapiaoReport : list) {
            /*
            for (InvSortDTO invSort : invSorts) {
                if(fapiaoReport.getInvSortId().equals(invSort.getSortId().toString())){
                    fapiaoReport.setInvSortId(invSort.getSortName());
                }
                if(fapiaoReport.getInvSortRoot().equals(invSort.getSortId().toString())){
                    fapiaoReport.setInvSortRoot(invSort.getSortName());
                }
            }
            */
            // fapiaoReport.setWorkDept(deptMap.get(fapiaoReport.getWorkDept()));
            fapiaoReport.setInvoiceType(poInvoiceType.get(fapiaoReport.getInvoiceType()));
            fapiaoReport.setWorkType(hashMap1.get(fapiaoReport.getWorkType()));
            fapiaoReport.setInvoiceStatus(hashMap12.get(fapiaoReport.getInvoiceStatus()));
            fapiaoReport.setWorkStatus(hashMap.get(fapiaoReport.getWorkStatus()));
            //fapiaoReport.setSaleType(hashMap3.get(fapiaoReport.getSaleType()));
        }
        return list;
    }

    @Override
    public List<Map<String, String>> getDetail(OmFapiaoMaster omFapiaoMaster){
        List<Map<String, String>> detail;
        detail = omFapiaoMasterMapper.getDetail(omFapiaoMaster);
        List<SysDictData> po_plan_type = iSysDictTypeService.selectDictDataByType(OmConstant.FAPIAO);
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
            stringStringMap.put("order_type",OmConstant.FAPIAO);
        }
        return detail;
    }

    @Override
    public List<ExportFapiao> export(OmFapiaoMaster omFapiaoMaster) {
        List<ExportFapiao> list= omFapiaoMasterMapper.export(omFapiaoMaster);
        List<SysDictData> sys_invoice_status = iSysDictTypeService.selectDictDataByType("sys_invoice_status");
        List<SysDictData> om_fapiao_type = iSysDictTypeService.selectDictDataByType(OmConstant.FAPIAO);
        HashMap<String, String> hashMap = new HashMap<>();
        for (SysDictData sysDictData : om_fapiao_type) {
            hashMap.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> hashMap12 = new HashMap<>();
        for (SysDictData sysDictData : sys_invoice_status) {
            hashMap12.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }

        for (ExportFapiao exportFapiao : list) {
            exportFapiao.setInvoiceStatus(hashMap12.get(exportFapiao.getInvoiceStatus()));
            exportFapiao.setWorkType(hashMap.get(exportFapiao.getWorkType()));
        }

        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmFapiaoMaster entry(List<OmFapiaoMaster> list) {
        list.forEach(r ->{
            String omCode = r.getOmCode();
            OmFapiaoMaster omFapiaoMaster = omFapiaoMasterMapper.selectOmFapiaoMasterById(omCode);
            if(!omFapiaoMaster.getVatStatus().equals("0") || !omFapiaoMaster.getInvoiceStatus().equals("1")){
                throw  new CommonException(r.getOmCode() + " 单据状态不符合要求");
            }

            String user= SecurityUtils.getUsername();
            omFapiaoMaster.setUserCheck(user);
            omFapiaoMaster.setCreateBy(user);
            omFapiaoMaster.setUpdateBy(user);
            omFapiaoMaster.setInvoiceStatus("1");
            omFapiaoMaster.setVatStatus("1");

            String vatNo= r.getVatNo();
            omFapiaoMaster.setVatNo(vatNo);

            OmFapiaoMaster query= new OmFapiaoMaster();
            query.setVatNo(vatNo);
            List<OmFapiaoMaster> fp= omFapiaoMasterMapper.selectOmFapiaoMasterList( query );
            for(OmFapiaoMaster s: fp){
                if(s.getOmCode().equals(omCode)==false)throw new CommonException("发票号码重复了");
            }

            boolean rt= fdOmFapiaoApiService.checkAccountId(omFapiaoMaster.getCustomerId());
            if(rt==false){
                throw new CommonException("发票客户没有科目无法入账");
            }

            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonString = mapper.writeValueAsString(omFapiaoMaster);
                fdOmFapiaoApiService.invoiceEntry(jsonString);
            }catch (Exception e){
                e.printStackTrace();
                throw new CommonException(r.getOmCode() + " 对象转换过程失败");
            }

            Integer period= fdPeriodApiService.current();
            omFapiaoMasterMapper.updateEntryStatus(omCode, "1", period, vatNo);
        });

        if(list.size() == 1){
            return omFapiaoMasterMapper.selectOmFapiaoMasterById(list.get(0).getOmCode());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmFapiaoMaster antiEntry(List<OmFapiaoMaster> list) {
        list.forEach(r ->{
            String omCode = r.getOmCode();
            OmFapiaoMaster omFapiaoMaster = omFapiaoMasterMapper.selectOmFapiaoMasterById(omCode);
            if(omFapiaoMaster.getInvoiceStatus().equals("0")){
                throw  new CommonException(r.getOmCode() + " 发票状态不符合要求");
            }
            else if(omFapiaoMaster.getVatStatus().equals("0")){
                throw  new CommonException(r.getOmCode() + " 入账状态不符合要求");
            }

            Integer period= fdPeriodApiService.current();
            if(!period.equals(omFapiaoMaster.getFdPeriod())){
                throw  new CommonException(r.getOmCode() + " 入账期间不符合要求");
            }

            int rt= fdOmFapiaoApiService.invoiceUnEntry(omFapiaoMaster.getOmCode());
            if(rt==-1){
                throw  new CommonException(r.getOmCode() + " 单据期间已经结存");
            }
            else if(rt==-2){
                throw  new CommonException(r.getOmCode() + " 应收单已有到账");
            }

            omFapiaoMasterMapper.updateVatStatus(omCode,"0");
        });
        if(list.size() == 1){
            return omFapiaoMasterMapper.selectOmFapiaoMasterById(list.get(0).getOmCode());
        }
        return null;
    }

    @Override
    public void cancelAllEntries() {
        String user= SecurityUtils.getUsername();
        if(user.equals("admin"))omFapiaoMasterMapper.cancelAllEntries();
    }
}
