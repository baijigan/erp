package com.njrsun.modules.prs.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.njrsun.common.constant.Constants;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.modules.om.enums.WorkStatus;
import com.njrsun.modules.prs.contacts.PrsContacts;
import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.mapper.PrsProductMasterMapper;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import com.njrsun.system.service.impl.SysDictTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import com.njrsun.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.njrsun.modules.prs.mapper.PrsCheckinMasterMapper;
import com.njrsun.modules.prs.service.IPrsCheckinMasterService;

/**
 * 生产入库单主Service业务层处理
 * 
 * @author njrsun
 * @date 2022-01-18
 */
@Service
public class PrsCheckinMasterServiceImpl implements IPrsCheckinMasterService 
{
    @Autowired
    private PrsCheckinMasterMapper prsCheckinMasterMapper;
    @Autowired
    private PrsProductMasterMapper prsProductMasterMapper;
    @Autowired
    private SysCoderServiceImpl sysCoderService;
    @Autowired
    private SysDictTypeServiceImpl sysDictTypeService;

    /**
     * 查询生产入库单主
     * 
     * @param uniqueId 生产入库单主ID
     * @return 生产入库单主
     */
    @Override
    public PrsCheckinMaster selectPrsCheckinMasterById(PrsCheckinMaster prsCheckinMaster)
    {
        return prsCheckinMasterMapper.selectPrsCheckinMasterById(prsCheckinMaster);
    }

    /**
     * 查询生产入库单主列表
     * 
     * @param prsCheckinMaster 生产入库单主
     * @return 生产入库单主
     */
    @Override
    public List<PrsCheckinMaster> selectPrsCheckinMasterList(PrsCheckinMaster prsCheckinMaster)
    {
        List<PrsCheckinMaster> prsCheckinMasters = prsCheckinMasterMapper.selectPrsCheckinMasterList(prsCheckinMaster);
        return prsCheckinMasters;
    }

    /**
     * 新增生产入库单主
     * 
     * @param prsCheckinMaster 生产入库单主
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPrsCheckinMaster(PrsCheckinMaster prsCheckinMaster)
    {
        prsCheckinMaster.setPrsCode(sysCoderService.generate(PrsContacts.CHECK,prsCheckinMaster.getWorkType()));
        prsCheckinMaster.setCreateBy(SecurityUtils.getUsername());
        int rows = prsCheckinMasterMapper.insertPrsCheckinMaster(prsCheckinMaster);
        insertPrsCheckinSalve(prsCheckinMaster);
        prsCheckinMaster.setPrsCode(sysCoderService.generate(PrsContacts.CHECK,prsCheckinMaster.getWorkType()));
        prsCheckinMaster.setCreateBy(SecurityUtils.getUsername());
        int rows = prsCheckinMasterMapper.insertPrsCheckinMaster(prsCheckinMaster);
        insertPrsCheckinSalve(prsCheckinMaster);
        return rows;
    }

    /**
     * 修改生产入库单主
     * 
     * @param prsCheckinMaster 生产入库单主
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePrsCheckinMaster(PrsCheckinMaster prsCheckinMaster)
    {
        PrsCheckinMaster prsCheckinMaster1 = prsCheckinMasterMapper.selectPrsCheckinForUpdate(prsCheckinMaster.getPrsCode());
        if(prsCheckinMaster1.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("审核单据,无法修改");
        }
        /** 老Id */
        ArrayList<Long> oldId = prsCheckinMasterMapper.selectPrsSalveByCode(prsCheckinMaster.getPrsCode());
        /**  新数据  */
        List<PrsCheckinSalve> prsCheckinSalveList = prsCheckinMaster.getPrsCheckinSalveList();
        /** 新ID */
        ArrayList<Long> newId = new ArrayList<>();
        ArrayList<PrsCheckinSalve> newData = new ArrayList<>();
        ArrayList<PrsCheckinSalve> editData = new ArrayList<>();
        for (PrsCheckinSalve prsCheckinSalve : prsCheckinSalveList) {
            if(prsCheckinSalve.getUniqueId() == null){
                //新增数据
                prsCheckinSalve.setCreateBy(SecurityUtils.getUsername());
                prsCheckinSalve.setPrsCode(prsCheckinMaster.getPrsCode());
                newData.add(prsCheckinSalve);
            }
            else{
                newId.add(prsCheckinSalve.getUniqueId());
            }
        }
        for (PrsCheckinSalve prsCheckinSalve : prsCheckinSalveList) {
            if(prsCheckinSalve.getUniqueId() == null){
                //新增数据
                prsCheckinSalve.setCreateBy(SecurityUtils.getUsername());
                prsCheckinSalve.setPrsCode(prsCheckinMaster.getPrsCode());
                newData.add(prsCheckinSalve);
            }
            else{
                newId.add(prsCheckinSalve.getUniqueId());
            }
        }
        prsCheckinMaster.setUpdateBy(SecurityUtils.getUsername());
        // 删除ID
        List<Long> delId = oldId.stream().filter(t -> !newId.contains(t)).collect(Collectors.toList());

        //修改ID
        oldId.retainAll(newId);

        for (Long aLong : oldId) {
            for (PrsCheckinSalve prsCheckinSalve : prsCheckinSalveList) {
                if(prsCheckinSalve.getUniqueId().equals(aLong)){
                    prsCheckinSalve.setUpdateBy(prsCheckinMaster.getUpdateBy());
                    editData.add(prsCheckinSalve);
                    break;
                }
            }
        }
        Long[] longs = delId.toArray(new Long[delId.size()]);
        if(longs.length != 0){
            prsCheckinMasterMapper.deletePrsCheckinSlave(longs);
        }
        if(newData.size() != 0){
            prsCheckinMasterMapper.batchPrsCheckinSalve(newData);
        }
        int j=1;
        if(editData.size() != 0){
            j = prsCheckinMasterMapper.updateCheckinSalve(editData);
        }
        int i = prsCheckinMasterMapper.updatePrsCheckinMaster(prsCheckinMaster);
        if(i == 0 || j == 0){
            throw  new CommonException("修改失败");
        }
        return i;
    }

    /**
     * 批量删除生产入库单主
     * 
     * @param uniqueIds 需要删除的生产入库单主ID
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePrsCheckinMasterByIds(List<PrsCheckinMaster>  list)
    {
        ArrayList<PrsCheckinMaster> re  =   prsCheckinMasterMapper.selectPrsInvoice(list);
        for (PrsCheckinMaster prsCheckinMaster : re) {
            if(prsCheckinMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException("审核状态，无法删除");
            }
        }
        prsCheckinMasterMapper.deletePrsCheckinSalveByPrsCodes(list);
        return prsCheckinMasterMapper.deletePrsCheckinMasterByIds(list);
    }

    /**
     * 删除生产入库单主信息
     * 
     * @param uniqueId 生产入库单主ID
     * @return 结果
     */
    @Override
    public int deletePrsCheckinMasterById(Long uniqueId)
    {
        prsCheckinMasterMapper.deletePrsCheckinSalveByPrsCode(uniqueId);
        return prsCheckinMasterMapper.deletePrsCheckinMasterById(uniqueId);
    }

    @Override
    public PrsCheckinMaster batchCheck(List<PrsCheckinMaster> list) {
        List<PrsCheckinMaster> prsCheckinMasters = prsCheckinMasterMapper.selectPrsInvoice(list);
        for (PrsCheckinMaster prsCheckinMaster : prsCheckinMasters) {
            if(!prsCheckinMaster.getInvoiceStatus().equals(Constants.OPEN)){
                throw  new CommonException(prsCheckinMaster.getPrsCode() + ": 单据 非开立状态");
            }
        }
        for (PrsCheckinMaster prsCheckinMaster : list) {
            check(prsCheckinMaster);
        }
        if(list.size() == 1){
            return prsCheckinMasterMapper.selectPrsCheckinMasterById(list.get(0));
        }
        return null;
    }

    @Override
    public PrsCheckinMaster batchAntiCheck(List<PrsCheckinMaster> list) {
        List<PrsCheckinMaster> prsCheckinMasters = prsCheckinMasterMapper.selectPrsInvoice(list);
        for (PrsCheckinMaster prsCheckinMaster : prsCheckinMasters) {
            if(!prsCheckinMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException(prsCheckinMaster.getPrsCode() + ": 单据 非开立状态");
            }
        }
        for (PrsCheckinMaster prsCheckinMaster : list) {
            anticheck(prsCheckinMaster);
        }
        if(list.size() == 1){
            return prsCheckinMasterMapper.selectPrsCheckinMasterById(list.get(0));
        }
        return null;
    }

    @Override
    public List<String> selectPrsCode(PrsCheckinMaster prsCheckinMaster) {
        return  prsCheckinMasterMapper.selectPrsCode(prsCheckinMaster);
    }

    @Override
    public List<PrsCheckinExport> getDetail(PrsCheckinExport prsCheckinExport) {
        return prsCheckinMasterMapper.getDetail(prsCheckinExport);
    }

    @Override
    public List<Map<String, String>> chain(PrsCheckinExport prsCheckinExport) {
        List<Map<String,String>>  up =     prsCheckinMasterMapper.upData(prsCheckinExport);
        for (Map<String, String> stringStringMap : up) {
            HashMap<String, String> transform = sysDictTypeService.transform(stringStringMap.get("invoice"));
            HashMap<String, String> transform1 = sysDictTypeService.transform(stringStringMap.get("type"));
            stringStringMap.put("invoice_name",transform.get(stringStringMap.get("invoice_id")));
            stringStringMap.put("type_name",transform1.get(stringStringMap.get("type_id")));
        }
        return  up;
    }

    @Override
    public List<Map<String, String>> lead(Map<String, String> query){
        return prsCheckinMasterMapper.lead(query);
    }

    @Override
    public List<Map<String, String>> leadInto(Map<String, String> map){
        List<Map<String, String>> list= prsCheckinMasterMapper.leadInto(map);
        for (Map<String, String> stringStringMap : list) {
            stringStringMap.put("invoice_type",Constants.PRS_INVOICE_TYPE);
            stringStringMap.put("work_type",PrsContacts.CHECK);
        }

        return list;
    }

    @Override
    public void changeWorkStatus(PrsCheckinMaster prsCheckinMaster) {
        String prsCode = prsCheckinMaster.getPrsCode();
        String workStatus = prsCheckinMaster.getWorkStatus();
        PrsCheckinMaster prsCheckinMaster1 = prsCheckinMasterMapper.selectPrsCheckinForUpdate(prsCode);
        if(prsCheckinMaster1.getWorkStatus().equals(WorkStatus.SYSTEM_CLOSING.getValue())){
            throw  new CommonException("系统关闭状态,无法操作");
        }
        Integer i;
        if(prsCheckinMaster1.getWorkStatus().equals(workStatus)){
            i = prsCheckinMasterMapper.updateWorkStatus(WorkStatus.NORMAl.getValue(), prsCheckinMaster);
        }else{
            i = prsCheckinMasterMapper.updateWorkStatus(workStatus, prsCheckinMaster);
        }
        if(i == 0 ){
            throw  new CommonException("操作失败");
        }
    }

    @Override
    public List<PrsCheckinReport> report(PrsCheckinReport prsCheckinReport) {
        return  prsCheckinMasterMapper.report(prsCheckinReport);
    }

    @Override
    public PrsCheckinMaster selectPrsCheckinMasterForUpdate(String woCode) {

        return prsCheckinMasterMapper.selectPrsCheckinForUpdate(woCode);
    }

    @Override
    public PrsCheckinSalve selectPrsCheckinSalveById(Long uniqueId) {
        return prsCheckinMasterMapper.selectPrsCheckinSalveById(uniqueId);
    }

    @Override
    public void changeQuantity(Long uniqueId, BigDecimal quantity) {
        prsCheckinMasterMapper.changeQuantity(uniqueId, quantity);
    }

    private void anticheck(PrsCheckinMaster prsCheckinMaster) {
        PrsCheckinMaster prsCheckinMaster1 = prsCheckinMasterMapper.selectPrsCheckinForUpdate(prsCheckinMaster.getPrsCode());
        PrsCheckinMaster prsCheckinMaster2 = prsCheckinMasterMapper.selectPrsCheckinMasterById(prsCheckinMaster);
        String lastCode = null;
        List<PrsCheckinSalve> prsCheckinSalveList = prsCheckinMaster2.getPrsCheckinSalveList();
        if(prsCheckinSalveList.size() > 0 ){
            lastCode = prsCheckinSalveList.get(0).getWoCode();
        }
        for (PrsCheckinSalve prsCheckinSalve : prsCheckinSalveList) {
            String woCode = prsCheckinSalve.getWoCode();
            PrsProductMaster prsProductMaster = prsProductMasterMapper.selectPrsProductMasterForUpdate(woCode);
            BigDecimal wiQuantity = prsProductMaster.getWiQuantity();
            BigDecimal quantity = prsCheckinSalve.getQuantity();
            if(wiQuantity.compareTo(quantity) < 0 ){
                throw  new CommonException("反审核失败 "+prsCheckinSalve.getInvName()+" 数量不足");
            }
            int i = prsProductMasterMapper.updateQuantity(prsCheckinSalve.getWoCode(), quantity.negate());
            if(i == 0){
                throw  new CommonException("反审核失败");
            }
            if(!lastCode.equals(prsCheckinSalve.getWoCode())){
                prsProductMasterMapper.changeWorkStatus(WorkStatus.NORMAl.getValue(),woCode);
            }
            lastCode = prsCheckinSalve.getWoCode();
        }
        Integer integer = prsCheckinMasterMapper.changeStatus(Constants.RETURN, SecurityUtils.getUsername(), prsCheckinMaster);
        if(integer ==0){
            throw  new CommonException("反审核失败");
        }
        if(prsCheckinSalveList.size() != 0){
                prsProductMasterMapper.changeWorkStatus(WorkStatus.NORMAl.getValue(),lastCode);
        }
    }

    private void check(PrsCheckinMaster prsCheckinMaster) {
        PrsCheckinMaster prsCheckinMaster1 = prsCheckinMasterMapper.selectPrsCheckinForUpdate(prsCheckinMaster.getPrsCode());
        PrsCheckinMaster prsCheckinMaster2 = prsCheckinMasterMapper.selectPrsCheckinMasterById(prsCheckinMaster);
        String lastCode = null;
        List<PrsCheckinSalve> prsCheckinSalveList = prsCheckinMaster2.getPrsCheckinSalveList();
        if(prsCheckinSalveList.size() > 0 ){
            lastCode = prsCheckinSalveList.get(0).getWoCode();
        }
        for (PrsCheckinSalve prsCheckinSalve : prsCheckinSalveList) {
            String woCode = prsCheckinSalve.getWoCode();
            PrsProductMaster prsProductMaster = prsProductMasterMapper.selectPrsProductMasterForUpdate(woCode);
            BigDecimal invQuantity = prsProductMaster.getInvQuantity();
            BigDecimal wiQuantity = prsProductMaster.getWiQuantity();
            BigDecimal quantity = prsCheckinSalve.getQuantity();
            if(invQuantity.compareTo(wiQuantity.add(quantity))<0){
                throw  new CommonException("审核失败 "+prsCheckinSalve.getInvName()+" 数量不足");
            }
            int i = prsProductMasterMapper.updateQuantity(prsCheckinSalve.getWoCode(), quantity);
            if(i == 0){
                throw  new CommonException("审核失败");
            }
            if(!woCode.equals(lastCode)){
                PrsProductMaster prsProductMaster1 = new PrsProductMaster();
                prsProductMaster1.setPrsCode(lastCode);
                PrsProductMaster prsProductMaster2 = prsProductMasterMapper.selectPrsProductMasterById(prsProductMaster1);
                if(prsProductMaster2.getWiQuantity().compareTo(prsProductMaster2.getInvQuantity()) == 0 ){
                    prsProductMasterMapper.changeWorkStatus(WorkStatus.SYSTEM_CLOSING.getValue(),lastCode);
                }
                lastCode =woCode;
            }
        }
        Integer integer = prsCheckinMasterMapper.changeStatus(Constants.CHECK, SecurityUtils.getUsername(), prsCheckinMaster);
        if(integer ==0){
            throw  new CommonException("审核失败");
        }
        if(prsCheckinSalveList.size() != 0){
            PrsProductMaster prsProductMaster1 = new PrsProductMaster();
            prsProductMaster1.setPrsCode(lastCode);
            PrsProductMaster prsProductMaster2 = prsProductMasterMapper.selectPrsProductMasterById(prsProductMaster1);
            if(prsProductMaster2.getWiQuantity().compareTo(prsProductMaster2.getInvQuantity()) == 0 ){
                prsProductMasterMapper.changeWorkStatus(WorkStatus.SYSTEM_CLOSING.getValue(),lastCode);
            }
        }

    }

    /**
     * 新增生产入库单从信息
     * 
     * @param prsCheckinMaster 生产入库单主对象
     */
    public void insertPrsCheckinSalve(PrsCheckinMaster prsCheckinMaster)
    {
        List<PrsCheckinSalve> prsCheckinSalveList = prsCheckinMaster.getPrsCheckinSalveList();
        String uniqueId = prsCheckinMaster.getPrsCode();
        if (StringUtils.isNotNull(prsCheckinSalveList))
        {
            List<PrsCheckinSalve> list = new ArrayList<PrsCheckinSalve>();
            for (PrsCheckinSalve prsCheckinSalve : prsCheckinSalveList)
            {
                prsCheckinSalve.setPrsCode(uniqueId);
                list.add(prsCheckinSalve);
            }
            if (list.size() > 0)
            {
                prsCheckinMasterMapper.batchPrsCheckinSalve(list);
            }
        }
    }
}
