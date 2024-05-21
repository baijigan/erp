package com.njrsun.modules.prs.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.njrsun.common.constant.Constants;
import com.njrsun.common.core.domain.entity.SysDept;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.modules.om.enums.WorkStatus;
import com.njrsun.modules.prs.contacts.PrsContacts;
import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.mapper.*;
import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.mapper.*;
import com.njrsun.system.mapper.SysDeptMapper;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import com.njrsun.system.service.impl.SysDictTypeServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Map;

import com.njrsun.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.njrsun.modules.prs.service.IPrsProductMasterService;

/**
 * 生产完工单主Service业务层处理
 * 
 * @author njrsun
 * @date 2021-11-15
 */
@Service
public class PrsProductMasterServiceImpl implements IPrsProductMasterService 
{
    @Autowired
    private PrsProductMasterMapper prsProductMasterMapper;
    @Autowired
    private SysCoderServiceImpl sysCoderService;
    @Autowired
    private PrsOrderMasterMapper prsOrderMasterMapper;
    @Autowired
    private SysDictTypeServiceImpl sysDictTypeService;
    @Autowired
    private SysDeptMapper sysDeptMapper;
    @Autowired
    private PrsJobsMapper prsJobsMapper;
    @Autowired
    private PrsWorkerMapper prsWorkerMapper;
    @Autowired
    private PrsYieldMasterServiceImpl prsYieldMasterService;
    @Autowired
    private PrsPickMasterServiceImpl prsPickMasterService;
    @Autowired
    private PrsCheckinMasterMapper prsCheckinMasterMapper;

    /**
     * 查询生产完工单主
     * 
     * @param uniqueId 生产完工单主ID
     * @return 生产完工单主
     */
    @Override
    public PrsProductMaster selectPrsProductMasterById(PrsProductMaster uniqueId)
    {
        return prsProductMasterMapper.selectPrsProductMasterById(uniqueId);
    }

    /**
     * 查询生产完工单主列表
     * @param prsProductMaster 生产完工单主
     * @return 生产完工单主
     */
    @Override
    public List<PrsProductMaster> selectPrsProductMasterList(PrsProductMaster prsProductMaster)
    {
        List<PrsProductMaster> prsProductMasters = prsProductMasterMapper.selectPrsProductMasterList(prsProductMaster);
        HashMap<String, String> transform = sysDictTypeService.transform(PrsContacts.ORDER);
        HashMap<String, String> sys_work_status = sysDictTypeService.transform("sys_work_status");
        List<SysDept> sysDepts = sysDeptMapper.selectDeptList(new SysDept());
        HashMap<String, String> deptMap = new HashMap<>();
        return prsProductMasters;
    }

    /**
     * 新增生产完工单主
     * 
     * @param prsProductMaster 生产完工单主
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPrsProductMaster(PrsProductMaster prsProductMaster)
    {
        prsProductMaster.setCreateBy(SecurityUtils.getUsername());
        for (PrsProductMaster prsOrderMaster1 : prsProductMasters) {
            Map<String, Object> params = prsOrderMaster1.getParams();
            params.put(PrsContacts.ORDER,transform.get(prsOrderMaster1.getWorkType()));
            params.put("sys_work_status",sys_work_status.get(prsOrderMaster1.getWorkStatus()));
            params.put("deptName",deptMap.get(prsOrderMaster1.getWorkDept()));
        }

        for (PrsProductMaster prsOrderMaster1 : prsProductMasters) {
            Map<String, Object> params = prsOrderMaster1.getParams();
            params.put(PrsContacts.ORDER,transform.get(prsOrderMaster1.getWorkType()));
            params.put("sys_work_status",sys_work_status.get(prsOrderMaster1.getWorkStatus()));
            params.put("deptName",deptMap.get(prsOrderMaster1.getWorkDept()));
        }
        prsProductMaster.setPrsCode(sysCoderService.generate("prs_product_type",prsProductMaster.getWorkType()));
        int rows = prsProductMasterMapper.insertPrsProductMaster(prsProductMaster);
        return rows;
    }



    /**
     * 修改生产完工单主
     * 
     * @param prsProductMaster 生产完工单主
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePrsProductMaster(PrsProductMaster prsProductMaster)
    {
        PrsProductMaster prsProductMaster1 = prsProductMasterMapper.selectPrsProductMasterForUpdate(prsProductMaster.getPrsCode());
        if(prsProductMaster1.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("审核单据,无法修改");
        }
        int i = prsProductMasterMapper.updatePrsProductMaster(prsProductMaster);
        if(i == 0 ){
            throw  new CommonException("修改失败");
        }
        return i;
    }


    /**
     * 批量删除生产完工单主
     * 
     * @param list 需要删除的生产完工单主ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deletePrsProductMasterByIds(List<PrsProductMaster> list)
    {
         List<PrsProductMaster> result = prsProductMasterMapper.selectPrsPrductInvoiceByList(list);
        for (PrsProductMaster prsProductMaster : result) {
            if(prsProductMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException("审核单据，无法删除");
            }
        }
        return prsProductMasterMapper.deletePrsProductMasterByIds(list);
    }


    /**
     * 删除生产完工单主信息
     * 
     * @param uniqueId 生产完工单主ID
     * @return 结果
     */
    @Override
    public int deletePrsProductMasterById(Long uniqueId)
    {
        return prsProductMasterMapper.deletePrsProductMasterById(uniqueId);
    }


    @Override
    public void changeWorkStatus(PrsProductMaster prsProductMaster) {
        String prsCode = prsProductMaster.getPrsCode();
        String workStatus = prsProductMaster.getWorkStatus();
        PrsProductMaster prsProductMaster1 = prsProductMasterMapper.selectPrsProductMasterForUpdate(prsCode);
        if(prsProductMaster1.getWorkStatus().equals(WorkStatus.SYSTEM_CLOSING.getValue())){
            throw  new CommonException("系统关闭状态,无法操作");
        }
        Integer i;
        if(prsProductMaster1.getWorkStatus().equals(workStatus)){
            i = prsProductMasterMapper.updateWorkStatus(WorkStatus.NORMAl.getValue(), prsProductMaster);
        }else{
            i = prsProductMasterMapper.updateWorkStatus(workStatus, prsProductMaster);
        }
        if(i == 0 ){
            throw  new CommonException("操作失败");
        }
    }

    @Override
    public List<PrsProductExport> getDetail(PrsProductExport prsProductSalve) {
        ArrayList<PrsProductExport> prsProductExports = new ArrayList<>();
        return   prsProductExports;
    }

    @Override
    public List<Map<String, Object>> lead(Map<String, String> map){
        List<Map<String, Object>> lead = prsProductMasterMapper.lead(map);
        for (Map<String, Object> stringStringMap : lead) {
            stringStringMap.put("invoice_type", PrsContacts.INVOICE);
            stringStringMap.put("work_type",PrsContacts.PRODUCT);
        }
        return lead;
    }

    @Override
    public List<Map<String, String>> leadInto(PrsOrderMaster prsOrderMaster) {
        List<Map<String, String>> maps = prsOrderMasterMapper.leadInto(prsOrderMaster);
        for (Map<String, String> map : maps) {
            map.put("invoice_type",PrsContacts.INVOICE);
            map.put("work_type",PrsContacts.ORDER);
        }
        return maps;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrsProductMaster batchCheck(List<PrsProductMaster> list) {
        List<PrsProductMaster> prsProductMasters = prsProductMasterMapper.selectPrsPrductInvoiceByList(list);
        for (PrsProductMaster prsProductMaster : prsProductMasters) {
            if(!prsProductMaster.getInvoiceStatus().equals(Constants.OPEN)){
                throw  new CommonException(prsProductMaster.getPrsCode() + ": 单据 非开立状态");
            }
        }
        for (PrsProductMaster prsOrderMaster : list) {
            check(prsOrderMaster);
        }
        List<PrsYieldMaster> yields =  findYied(list);
        prsYieldMasterService.deletePrsYieldMasterByCodes(yields);
        if(list.size() == 1){
            return prsProductMasterMapper.selectPrsProductMasterById(list.get(0));
        }
        return null;

    }

    private List<PrsYieldMaster> findYied(List<PrsProductMaster> list) {
        ArrayList<PrsYieldMaster> prsYieldMasters = new ArrayList<>();
        for (PrsProductMaster prsProductMaster : list) {
            PrsYieldMaster prsYieldMaster = new PrsYieldMaster();
            prsYieldMaster.setPrsCode(prsProductMaster.getPrsCode());
            prsYieldMasters.add(prsYieldMaster);
        }

        return prsYieldMasters;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrsProductMaster batchAntiCheck(List<PrsProductMaster> list) {
        List<PrsProductMaster> prsProductMasters = prsProductMasterMapper.selectPrsPrductInvoiceByList(list);
        for (PrsProductMaster prsProductMaster : prsProductMasters) {
            if(!prsProductMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException(prsProductMaster.getPrsCode() + ": 单据 非审核状态");
            }
        }
        for (PrsProductMaster prsProductMaster : list) {
            antiCheck(prsProductMaster);
        }
        if(list.size() == 1){
            return prsProductMasterMapper.selectPrsProductMasterById(list.get(0));
        }
        return null;
    }

    @Override
    public List<String> selectPrsCodeList(PrsProductMaster prsProductMaster) {


        return prsProductMasterMapper.selectPrsCodeList(prsProductMaster);

    }

    @Override
    public List<Map<String, String>> chain(PrsProductExport prsProductExport) {
       List<Map<String,String>>  up =  prsProductMasterMapper.upData(prsProductExport);
       List<Map<String,String>> down =    prsCheckinMasterMapper.downData(prsProductExport);
        for (Map<String, String> stringStringMap : down) {
            stringStringMap.put("inovice",PrsContacts.INVOICE);
            stringStringMap.put("type",PrsContacts.CHECK);
        }
        for (Map<String, String> stringStringMap : up) {
            String invoice = stringStringMap.get("invoice");
            String type = stringStringMap.get("type");
            HashMap<String, String> transform = sysDictTypeService.transform(invoice);
            HashMap<String, String> transform1 = sysDictTypeService.transform(type);
            stringStringMap.put("invoice_name",transform.get(stringStringMap.get("invoice_id")));
            stringStringMap.put("type_name",transform1.get(stringStringMap.get("type_id")));
        }
        return up;
    }

    @Override
    public List<PrsOrderProductExport> report(PrsOrderProductExport prsProductExport) {

        return prsProductMasterMapper.report(prsProductExport);


    }

    @Override
    public List<PrsProduceReport> produceReport(PrsOrderProductExport prsProductExport) {


        return  prsProductMasterMapper.produceReport(prsProductExport);




    }

    @Override
    public List<PrsOutputReport> output(PrsProductMaster prsProductMaster) {
        HashMap<String, String> prs_product_type = sysDictTypeService.transform("prs_product_type");
        List<PrsOutputReport> list =    prsProductMasterMapper.output(prsProductMaster);
        ArrayList<PrsOutputReport> reports = new ArrayList<>();
        for (PrsOutputReport prsOutputReport : list) {
            String workersIds = prsOutputReport.getWorkersIds();
            prsOutputReport.setWorkType(prs_product_type.get(prsOutputReport.getWorkType()));
            if(!workersIds.equals("")){
                String[] split = workersIds.split(",");
                for (String s : split) {
                    PrsJobs prsJobs =    prsJobsMapper.selectPrsJobsByCode(s);
                    PrsOutputReport report = new PrsOutputReport();
                    BeanUtils.copyProperties(prsOutputReport,report);
                    report.setWorkersNames(prsJobs.getName());
                    report.setWorkersIds(prsJobs.getCode());
                    report.setRatio(prsJobs.getRatio());
                    report.setJods(prsJobs.getJobs());
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    private void antiCheck(PrsProductMaster prsProductMaster) {
        PrsProductMaster prsProductMaster2 = prsProductMasterMapper.selectPrsProductMasterForUpdate(prsProductMaster.getPrsCode());
        String woCode = prsProductMaster2.getWoCode();
        String woUniqueId = prsProductMaster2.getWoUniqueId();
        PrsOrderMaster prsOrderMaster = prsOrderMasterMapper.selectPrsOrderMasterByCodeForUpdate(woCode);
        if(!prsOrderMaster.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("上游单据为非审核状态..");
        }
        BigDecimal wiQuantity = prsOrderMaster.getWiQuantity();
        BigDecimal invQuantity1 = prsProductMaster2.getInvQuantity();
        if(wiQuantity.compareTo(invQuantity1)<0 ){
            throw  new CommonException( "上游单据中 " +  prsProductMaster2.getInvName() + " 数量不足!" );
        }
        int i = prsOrderMasterMapper.changeQuantity(woUniqueId, invQuantity1.negate());
        if(i == 0){
            throw  new CommonException("反审核失败，单据不存在");
        }
        Integer integer = prsProductMasterMapper.changeStatus(Constants.RETURN, null, prsProductMaster);
        if(integer ==0){
            throw  new CommonException("反审核失败");
        }
    }

    private void check(PrsProductMaster prsProductMaster) {
        PrsProductMaster prsProductMaster2 = prsProductMasterMapper.selectPrsProductMasterForUpdate(prsProductMaster.getPrsCode());
        String woCode = prsProductMaster2.getWoCode();
        String woUniqueId = prsProductMaster2.getWoUniqueId();
        PrsPickMaster prsPickMaster = prsPickMasterService.selectPrsPickMasterByOrderCode(woCode);
        if(StringUtils.isNull(prsPickMaster)){
            throw  new CommonException("生产订单未领料");
        }
        PrsOrderMaster prsOrderMaster = prsOrderMasterMapper.selectPrsOrderMasterByCodeForUpdate(woCode);
                if(!prsOrderMaster.getInvoiceStatus().equals(Constants.CHECK)){
                    throw  new CommonException("上游单据为非审核状态..");
                }
        BigDecimal wiQuantity = prsOrderMaster.getWiQuantity();
        BigDecimal invQuantity = prsOrderMaster.getInvQuantity();
        BigDecimal invQuantity1 = prsProductMaster2.getInvQuantity();
        if(invQuantity.compareTo(wiQuantity.add(invQuantity1))<0 ){
                    throw  new CommonException( "上游单据中 " +  prsProductMaster2.getInvName() + " 数量不足!" );
                }
        int i = prsOrderMasterMapper.changeQuantity(woUniqueId, invQuantity1);
        if(i == 0){
            throw  new CommonException("审核失败，单据不存在");
        }
        Integer integer = prsProductMasterMapper.changeStatus(Constants.CHECK, SecurityUtils.getUsername(), prsProductMaster);
        if(integer ==0){
            throw  new CommonException("审核失败");
        }

    }
}
