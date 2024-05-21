package com.njrsun.modules.prs.service.impl;

import cn.hutool.core.date.DateUtil;
import com.njrsun.common.constant.Constants;
import com.njrsun.common.core.domain.entity.SysDept;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.mp.contact.MpContacts;
import com.njrsun.modules.mp.domain.MpMbomMaster;
import com.njrsun.modules.mp.service.IMpMbomMasterService;
import com.njrsun.modules.mp.service.impl.MpOrderMasterServiceImpl;
import com.njrsun.modules.om.domain.OmOrderMaster;
import com.njrsun.modules.om.domain.OmOrderSalve;
import com.njrsun.modules.om.enums.WorkStatus;
import com.njrsun.modules.om.service.IOmOrderMasterService;
import com.njrsun.modules.prs.contacts.PrsContacts;
import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.mapper.PrsOrderMasterMapper;
import com.njrsun.modules.prs.mapper.PrsProductMasterMapper;
import com.njrsun.modules.prs.service.IPrsOrderMasterService;
import com.njrsun.system.mapper.SysDeptMapper;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import com.njrsun.system.service.impl.SysDictTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 生产订单主Service业务层处理
 * 
 * @author njrsun
 * @date 2021-11-12
 */
@Service
public class PrsOrderMasterServiceImpl implements IPrsOrderMasterService 
{
    @Autowired
    private PrsOrderMasterMapper prsOrderMasterMapper;

    @Autowired
    private SysCoderServiceImpl sysCoderService;

    @Autowired
    private MpOrderMasterServiceImpl mpOrderMasterService;

    @Autowired
    private SysDictTypeServiceImpl  sysDictTypeService;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private PrsProductMasterMapper prsProductMasterMapper;

    @Autowired
    private IMpMbomMasterService mpMbomMasterService;

    @Autowired
    private IOmOrderMasterService omOrderMasterService;

    @Autowired
    private PrsPickMasterServiceImpl prsPickMasterService;

    /**
     * 查询生产订单主
     * 
     * @param prsOrderMaster 生产订单主ID
     * @return 生产订单主
     */
    @Override
    public PrsOrderMaster selectPrsOrderMasterById(PrsOrderMaster prsOrderMaster)
    {
        return prsOrderMasterMapper.selectPrsOrderMasterById(prsOrderMaster);
    }

    /**
     * 查询生产订单主列表
     * 
     * @param prsOrderMaster 生产订单主
     * @return 生产订单主
     */
    @Override
    public List<PrsOrderMaster> selectPrsOrderMasterList(PrsOrderMaster prsOrderMaster)
    {

        List<PrsOrderMaster> prsOrderMasters = prsOrderMasterMapper.selectPrsOrderMasterList(prsOrderMaster);
        HashMap<String, String> transform = sysDictTypeService.transform(PrsContacts.ORDER);
        HashMap<String, String> sys_work_status = sysDictTypeService.transform("sys_work_status");

        return prsOrderMasters;
    }

    /**
     * 新增生产订单主
     * 
     * @param prsOrderMaster 生产订单主
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPrsOrderMaster(PrsOrderMaster prsOrderMaster)
    {
        String prsProcessCode = prsOrderMaster.getPrsProcessCode();
        int rows = prsOrderMasterMapper.insertPrsOrderMaster(prsOrderMaster);
        return rows;
    }

    /**
     * 修改生产订单主
     * 
     * @param prsOrderMaster 生产订单主
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePrsOrderMaster(PrsOrderMaster prsOrderMaster)
    {
        PrsOrderMaster prsOrderMaster1 = prsOrderMasterMapper.selectPrsOrderMasterByCodeForUpdate(prsOrderMaster.getPrsCode());
        if(prsOrderMaster1.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("审核单据,无法修改");
        }
        int i = prsOrderMasterMapper.updatePrsOrderMaster(prsOrderMaster);
        if(i == 0){
            throw  new CommonException("修改失败");
        }
        return i;
    }

    /**
     * 批量删除生产订单主
     * 
     * @param uniqueIds 需要删除的生产订单主ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deletePrsOrderMasterByIds(List<PrsOrderMaster> list)
    {
       List<PrsOrderMaster> result =   prsOrderMasterMapper.selectPrsInvoiceForCode(list);
        for (PrsOrderMaster prsOrderMaster : result) {
            if(prsOrderMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException("审核单据无法删除");
            }
        }
        return prsOrderMasterMapper.deletePrsOrderMasterByIds(list);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrsOrderMaster batchCheck(List<PrsOrderMaster> list) {
        List<PrsOrderMaster> prsOrderMasters = prsOrderMasterMapper.selectPrsInvoiceForCode(list);
        for (PrsOrderMaster prsOrderMaster : prsOrderMasters) {
            if(!prsOrderMaster.getInvoiceStatus().equals(Constants.OPEN)){
                throw  new CommonException(prsOrderMaster.getPrsCode() + ": 单据 非开立状态");
            }
        }
        for (PrsOrderMaster prsOrderMaster : prsOrderMasters) {
            if(prsOrderMaster.getMpOrderCode().equals(prsOrderMaster.getPpNumber())){
                checkOm(prsOrderMaster);
            }else{
                checkMp(prsOrderMaster);
            }
        }
        if(list.size() == 1){
            return prsOrderMasterMapper.selectPrsOrderMasterById(list.get(0));
        }
        return null;
    }

    public void checkMp(PrsOrderMaster prsOrderMaster) {
        PrsOrderMaster prsOrderMaster1 = prsOrderMasterMapper.selectPrsOrderMasterByCodeForUpdate(prsOrderMaster.getPrsCode());
        MpMbomMaster mpMbomMaster = mpMbomMasterService.selectMpMbomMasterForUpdate(prsOrderMaster1.getWoCode());
        if(!mpMbomMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException("上游单据为非审核状态..");
            }
        String woUniqueId = prsOrderMaster1.getWoUniqueId();
        BigDecimal invQuantity = prsOrderMaster1.getInvQuantity();
            BigDecimal quantity = mpMbomMaster.getInvQuantity();
            BigDecimal wiQuantity = mpMbomMaster.getWiQuantity();
            if(quantity.compareTo(wiQuantity.add(invQuantity))<0 ){
                throw  new CommonException( "上游单据中 " +  mpMbomMaster.getInvName() + " 数量不足!" );
            }
        mpMbomMasterService.changeQuantity(Long.valueOf(woUniqueId),invQuantity);
        prsOrderMasterMapper.changeStatus(Constants.CHECK, SecurityUtils.getUsername(), prsOrderMaster);
    }

    public void checkOm(PrsOrderMaster prsOrderMaster) {
        PrsOrderMaster prsOrderMaster1 = prsOrderMasterMapper.selectPrsOrderMasterByCodeForUpdate(prsOrderMaster.getPrsCode());
        OmOrderMaster omOrderMaster = omOrderMasterService.selectOmOrderMasterForUpdate(prsOrderMaster1.getWoCode());
        if(!omOrderMaster.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("上游单据为非审核状态..");
        }
        List<PrsOrderMaster> prsOrderMasters = prsOrderMasterMapper.selectPrsInvoiceForCode(list);
        for (PrsOrderMaster prsOrderMaster : prsOrderMasters) {
            if(!prsOrderMaster.getInvoiceStatus().equals(Constants.OPEN)){
                throw  new CommonException(prsOrderMaster.getPrsCode() + ": 单据 非开立状态");
            }
        }
        for (PrsOrderMaster prsOrderMaster : prsOrderMasters) {
            if(prsOrderMaster.getMpOrderCode().equals(prsOrderMaster.getPpNumber())){
                checkOm(prsOrderMaster);
            }else{
                checkMp(prsOrderMaster);
            }
        }
        String woUniqueId = prsOrderMaster1.getWoUniqueId();
        BigDecimal invQuantity = prsOrderMaster1.getInvQuantity();
        OmOrderSalve omOrderSalve =  omOrderMasterService.selectOmOrderSalveById(new Long(woUniqueId));

        BigDecimal quantity = omOrderSalve.getQuantity();
        BigDecimal prsQuantity = omOrderSalve.getPrsQuantity();
        if(quantity.compareTo(prsQuantity.add(invQuantity))<0 ){
            throw  new CommonException( "上游单据中 " +  omOrderSalve.getInvName() + " 数量不足!" );
        }
        omOrderMasterService.changeQuantityPrs(Long.valueOf(woUniqueId), invQuantity);
        prsOrderMasterMapper.changeStatus(Constants.CHECK, SecurityUtils.getUsername(), prsOrderMaster);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrsOrderMaster batchAntiCheck(List<PrsOrderMaster> list) {
        List<PrsOrderMaster> prsOrderMasters = prsOrderMasterMapper.selectPrsInvoiceForCode(list);
        for (PrsOrderMaster prsOrderMaster : prsOrderMasters) {
            if(!prsOrderMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException(prsOrderMaster.getPrsCode() + ": 单据 非审核状态");
            }
            if(!prsOrderMaster.getWorkStatus().equals("0")){
                throw  new CommonException("业务状态异常");
            }
        }
        for (PrsOrderMaster prsOrderMaster : prsOrderMasters) {
            if(prsOrderMaster.getMpOrderCode().equals(prsOrderMaster.getPpNumber())){
                antiCheckOm(prsOrderMaster);
            }else{
                antiCheckMp(prsOrderMaster);
            }
        }
        if(list.size() == 1){
            return prsOrderMasterMapper.selectPrsOrderMasterById(list.get(0));
        }
        return null;
    }


    @Override
    public List<String> selectPrsCodeList(PrsOrderMaster p) {
        return prsOrderMasterMapper.selectPrsCodeList(p);
    }


    @Override
    public List<Map<String, String>> linkDetail(PrsOrderExport prsOrderExport) {
       List<Map<String,String>> list =   prsOrderMasterMapper.upData(prsOrderExport);
        List<Map<String,String>> down =    prsProductMasterMapper.downData(prsOrderExport);
        for (Map<String, String> stringStringMap : down) {
            stringStringMap.put("invoice",PrsContacts.INVOICE);
            stringStringMap.put("type",PrsContacts.PRODUCT);
        }
        list.addAll(down);
       List<Map<String,String>> pick =  prsPickMasterService.selectPrsPickByPrsOrderCode(prsOrderExport);
        for (Map<String, String> stringStringMap : pick) {
            stringStringMap.put("invoice",PrsContacts.INVOICE);
            stringStringMap.put("type",PrsContacts.PICK);
        }
        list.addAll(pick);
        for (Map<String, String> stringStringMap : list) {
            HashMap<String, String> transform = sysDictTypeService.transform(stringStringMap.get("invoice"));
            HashMap<String, String> transform1 = sysDictTypeService.transform(stringStringMap.get("type"));
            stringStringMap.put("invoice_name",transform.get(stringStringMap.get("invoice_id")));
            stringStringMap.put("type_name",transform1.get(stringStringMap.get("type_id")));
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeWorkStatus(PrsOrderMaster prsOrderMaster) {
        String prsCode = prsOrderMaster.getPrsCode();
        String workStatus = prsOrderMaster.getWorkStatus();
        PrsOrderMaster prsOrderMaster1 = prsOrderMasterMapper.selectPrsOrderMasterByCodeForUpdate(prsCode);
        if(prsOrderMaster1.getWorkStatus().equals(WorkStatus.SYSTEM_CLOSING.getValue())){
            throw  new CommonException("系统关闭状态,无法操作");
        }
        Integer i;
        if(prsOrderMaster1.getWorkStatus().equals(workStatus)){
            i = prsOrderMasterMapper.updateWorkStatus(WorkStatus.NORMAl.getValue(), prsOrderMaster);
        }else{
            i = prsOrderMasterMapper.updateWorkStatus(workStatus, prsOrderMaster);
        }
        if(i == 0 ){
            throw  new CommonException("操作失败");
        }
    }

    @Override
    public List<PrsOverdueExport> export(PrsOverdueExport prsOverdueExport) {

        return prsOrderMasterMapper.export(prsOverdueExport);



    }

    @Override
    public ProductionProgress production(String workType, String flag) {

        List<Map<String, String>> production = prsOrderMasterMapper.production(workType);
        ProductionProgress productionProgress = new ProductionProgress();
        ArrayList<String> list = new ArrayList<>();
        for (Map<String, String> stringStringMap : production) {
            list.add(stringStringMap.get("name"));
        }
        productionProgress.setProductionLine(list);
        ArrayList<PrsWorkData> prsWorkData1 = new ArrayList<>();
        Calendar instance = Calendar.getInstance();
        for (int i = 0; i < production.size(); i++) {
            String beltline_id = production.get(i).get("code");
            List<PrsOrderMaster>  ex  =  prsOrderMasterMapper.selectPrsOrderMasterByBeltlineId(workType,flag,beltline_id);
            for (PrsOrderMaster prsOrderMaster : ex) {
                PrsWorkData prsWorkData = new PrsWorkData();
                 prsWorkData.setName(prsOrderMaster.getInvName());
                Date arrangeDate = prsOrderMaster.getArrangeDate();
                instance.setTime(arrangeDate);
                instance.add(Calendar.MINUTE,prsOrderMaster.getDuration().intValue());
                BigDecimal divide = prsOrderMaster.getWiQuantity().divide(prsOrderMaster.getInvQuantity(), 2, RoundingMode.HALF_UP);
                prsWorkData.setSchedule(divide);
                prsWorkData.setInvQuantity(prsOrderMaster.getInvQuantity());
                prsWorkData.setAttribute(prsOrderMaster.getInvAttribute());
                prsWorkData.setUnitName(prsOrderMaster.getUnitName());
                prsWorkData.setValue(Arrays.asList(i+"", DateUtil.formatDateTime(prsOrderMaster.getArrangeDate()), DateUtil.formatDateTime(instance.getTime()), prsOrderMaster.getDuration().toString()));
                prsWorkData1.add(prsWorkData);
            }
        }
        List<PrsOrderMaster>  ex =     prsOrderMasterMapper.selectPrsOrderMaster(workType,flag);
        productionProgress.setPrsWorkData(prsWorkData1);
        productionProgress.setDetail(ex);
        return  productionProgress;
    }

    @Override
    public void updatePrsOrderMasterCheck(PrsOrderMaster prsOrderMaster) {
        int i = prsOrderMasterMapper.updatePrsOrderMaster(prsOrderMaster);
        if(i == 0){
            throw  new CommonException("修改失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void antiCheckMp(PrsOrderMaster prsOrderMaster) {
        PrsOrderMaster prsOrderMaster1 = prsOrderMasterMapper.selectPrsOrderMasterByCodeForUpdate(prsOrderMaster.getPrsCode());
            String woCode = prsOrderMaster1.getWoCode();
        String woUniqueId = prsOrderMaster1.getWoUniqueId();
        MpMbomMaster mpMbomMaster = mpMbomMasterService.selectMpMbomMasterForUpdate(woCode);
        if(!mpMbomMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException("上游单据为非审核状态..");
            }
            BigDecimal wiQuantity = prsOrderMaster1.getWiQuantity();
            if(StringUtils.isNotNull(wiQuantity) && wiQuantity.compareTo(BigDecimal.ZERO)>0){
                throw  new CommonException("单据已被引用，无法反审核");
            }
        String prsCode = prsOrderMaster1.getPrsCode();
           PrsPickMaster prsPickMaster =    prsPickMasterService.selectPrsPickMasterByOrderCode(prsCode);
           if(StringUtils.isNotNull(prsPickMaster)){
               throw  new CommonException("单据已被引用");
           }
        BigDecimal invQuantity = prsOrderMaster1.getInvQuantity();
        BigDecimal wiQuantity1 = mpMbomMaster.getWiQuantity();
        if(wiQuantity1.compareTo(invQuantity)<0 ){
                throw  new CommonException( prsOrderMaster1.getInvName() +  "计划数量不足");
            }
        mpMbomMasterService.changeQuantity(Long.valueOf(woUniqueId),invQuantity.negate());
         prsOrderMasterMapper.changeStatus(Constants.RETURN,null, prsOrderMaster);
    }

    @Transactional(rollbackFor = Exception.class)
    public void antiCheckOm(PrsOrderMaster prsOrderMaster) {
        PrsOrderMaster prsOrderMaster1 = prsOrderMasterMapper.selectPrsOrderMasterByCodeForUpdate(prsOrderMaster.getPrsCode());
        String woCode = prsOrderMaster1.getWoCode();
        String woUniqueId = prsOrderMaster1.getWoUniqueId();

        OmOrderMaster omOrderMaster = omOrderMasterService.selectOmOrderMasterForUpdate(woCode);
        if(!omOrderMaster.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("上游单据为非审核状态..");
        }

        BigDecimal wiQuantity = prsOrderMaster1.getWiQuantity();
        if(  StringUtils.isNotNull(wiQuantity) && wiQuantity.compareTo(BigDecimal.ZERO)>0){
             throw  new CommonException("单据已被引用，无法反审核");
        }

        OmOrderSalve omOrderSalve =  omOrderMasterService.selectOmOrderSalveById(new Long(woUniqueId));
        BigDecimal quantity = prsOrderMaster1.getInvQuantity();
        BigDecimal prsQuantity = omOrderSalve.getPrsQuantity();
        if(prsQuantity.compareTo(quantity)<0 ){
            throw  new CommonException( omOrderSalve.getInvName() +  "生产数量不足");
        }

        omOrderMasterService.changeQuantityPrs(new Long(woUniqueId), quantity.negate());
        prsOrderMasterMapper.changeStatus(Constants.RETURN,null, prsOrderMaster);
    }

    public List<Map<String, String>> lead(PrsOrderMaster prsOrderMaster) {

        List<Map<String, String>> maps = prsOrderMasterMapper.lead(prsOrderMaster);
        for (Map<String, String> map : maps) {
            map.put("invoice_type",MpContacts.INVOICE);
            map.put("work_type",MpContacts.ORDER);
        }
        return maps;
    }
}
