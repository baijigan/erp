package com.njrsun.modules.prs.service.impl;

import java.math.BigDecimal;
import java.util.List;

import com.njrsun.common.constant.Constants;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.modules.prs.domain.PrsProductMaster;
import com.njrsun.modules.prs.mapper.PrsProductMasterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.njrsun.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.njrsun.modules.prs.domain.PrsYieldSalve;
import com.njrsun.modules.prs.mapper.PrsYieldMasterMapper;
import com.njrsun.modules.prs.domain.PrsYieldMaster;
import com.njrsun.modules.prs.service.IPrsYieldMasterService;

/**
 * 生产报工单主Service业务层处理
 * 
 * @author njrsun
 * @date 2022-01-13
 */
@Service
public class PrsYieldMasterServiceImpl implements IPrsYieldMasterService 
{
    @Autowired
    private PrsYieldMasterMapper prsYieldMasterMapper;
    @Autowired
    private PrsProductMasterMapper prsProductMasterMapper;

    /**
     * 查询生产报工单主
     * 
     * @param uniqueId 生产报工单主ID
     * @return 生产报工单主
     */
    @Override
    public PrsYieldMaster selectPrsYieldMasterById(PrsYieldMaster prsYieldMaster)
    {
        return prsYieldMasterMapper.selectPrsYieldMasterById(prsYieldMaster);
    }

    /**
     * 查询生产报工单主列表
     * 
     * @param prsYieldMaster 生产报工单主
     * @return 生产报工单主
     */
    @Override
    public List<PrsYieldMaster> selectPrsYieldMasterList(PrsYieldMaster prsYieldMaster)
    {
        return prsYieldMasterMapper.selectPrsYieldMasterList(prsYieldMaster);
    }

    /**
     * 新增生产报工单主
     * 
     * @param prsYieldMaster 生产报工单主
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPrsYieldMaster(PrsYieldMaster prsYieldMaster)
    {
        int rows = prsYieldMasterMapper.insertPrsYieldMaster(prsYieldMaster);
        insertPrsYieldSalve(prsYieldMaster);
        return rows;
    }

    /**
     * 修改生产报工单主
     * 
     * @param prsYieldMaster 生产报工单主
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePrsYieldMaster(PrsYieldMaster prsYieldMaster)
    {
        PrsYieldMaster prsYieldMaster1 = prsYieldMasterMapper.selectPrsYieldMasterForUpdate(prsYieldMaster.getPrsCode());
        if(prsYieldMaster1.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("审核状态无法修改");
        }
        /** 老Id */
        ArrayList<Long> oldId = prsYieldMasterMapper.selectPrsYieLdSalveIdByCode(prsYieldMaster.getPrsCode());
        /**  新数据  */
        List<PrsYieldSalve> prsYieldSalves = prsYieldMaster.getPrsYieldSalveList();
        /** 新ID */
        ArrayList<Long> newId = new ArrayList<>();
        ArrayList<PrsYieldSalve> newData = new ArrayList<>();
        ArrayList<PrsYieldSalve> editData = new ArrayList<>();
        for (PrsYieldSalve prsYieldSalve : prsYieldSalves) {
            if(prsYieldSalve.getUniqueId() == null){
                //新增数据
                prsYieldSalve.setCreateBy(SecurityUtils.getUsername());
                prsYieldSalve.setPrsCode(prsYieldMaster.getPrsCode());
                newData.add(prsYieldSalve);
            }
            else{
                newId.add(prsYieldSalve.getUniqueId());
            }
        }
        prsYieldMaster.setUpdateBy(SecurityUtils.getUsername());
        // 删除ID
        List<Long> delId = oldId.stream().filter(t -> !newId.contains(t)).collect(Collectors.toList());

        /** 老Id */
        ArrayList<Long> oldId = prsYieldMasterMapper.selectPrsYieLdSalveIdByCode(prsYieldMaster.getPrsCode());
        /**  新数据  */
        List<PrsYieldSalve> prsYieldSalves = prsYieldMaster.getPrsYieldSalveList();
        /** 新ID */
        ArrayList<Long> newId = new ArrayList<>();
        ArrayList<PrsYieldSalve> newData = new ArrayList<>();
        ArrayList<PrsYieldSalve> editData = new ArrayList<>();
        for (PrsYieldSalve prsYieldSalve : prsYieldSalves) {
            if(prsYieldSalve.getUniqueId() == null){
                //新增数据
                prsYieldSalve.setCreateBy(SecurityUtils.getUsername());
                prsYieldSalve.setPrsCode(prsYieldMaster.getPrsCode());
                newData.add(prsYieldSalve);
            }
            else{
                newId.add(prsYieldSalve.getUniqueId());
            }
        }
        prsYieldMaster.setUpdateBy(SecurityUtils.getUsername());
        // 删除ID
        List<Long> delId = oldId.stream().filter(t -> !newId.contains(t)).collect(Collectors.toList());


        //修改ID
        oldId.retainAll(newId);

        for (Long aLong : oldId) {
            for (PrsYieldSalve prsYieldSalve : prsYieldSalves) {
                if(prsYieldSalve.getUniqueId().equals(aLong)){
                    prsYieldSalve.setUpdateBy(SecurityUtils.getUsername());
                    editData.add(prsYieldSalve);
                    break;
                }
            }
        }
        Long[] longs = delId.toArray(new Long[delId.size()]);
        if(longs.length != 0){
            prsYieldMasterMapper.deletePrsYieIdSalveByIds(longs);
        }
        if(newData.size() != 0){
            prsYieldMasterMapper.batchPrsYieldSalve(newData);
        }
        int j=1;
        if(editData.size() != 0){
            j = prsYieldMasterMapper.updatePrsYieIdSalveList(editData);
        }
        prsYieldMaster.setUpdateBy(SecurityUtils.getUsername());
        int i = prsYieldMasterMapper.updatePrsYieldMaster(prsYieldMaster);
        if(i == 0 || j == 0){
            throw  new CommonException("修改失败");
        }
        return i;
    }

    /**
     * 批量删除生产报工单主
     * 
     * @param list 需要删除的生产报工单主ID
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePrsYieldMasterByIds(List<PrsYieldMaster> list)
    {
        List<PrsYieldMaster> ex =  prsYieldMasterMapper.selectPrsYieIdStatus(list);
        for (PrsYieldMaster prsYieldMaster : ex) {
            if(prsYieldMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException("单据审核");
            }
        }
        prsYieldMasterMapper.deletePrsYieldSalveByPrsCodes(list);
        return prsYieldMasterMapper.deletePrsYieldMasterByIds(list);
    }

    /**
     * 删除生产报工单主信息
     * 
     * @param uniqueId 生产报工单主ID
     * @return 结果
     */
    @Override
    public int deletePrsYieldMasterById(Long uniqueId)
    {
        prsYieldMasterMapper.deletePrsYieldSalveByPrsCode(uniqueId);
        return prsYieldMasterMapper.deletePrsYieldMasterById(uniqueId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrsYieldMaster batchCheck(List<PrsYieldMaster> list) {
        List<PrsYieldMaster> prsYieldMasters = prsYieldMasterMapper.selectPrsYieIdStatus(list);
        for (PrsYieldMaster prsYieldMaster : prsYieldMasters) {
            if(!prsYieldMaster.getInvoiceStatus().equals(Constants.OPEN)){
                throw  new CommonException("单据审核");
            }
        }
        for (PrsYieldMaster prsYieldMaster : list) {
            check(prsYieldMaster);
        }
        if(list.size() > 0 ){
            return prsYieldMasterMapper.selectPrsYieldMasterById(list.get(0));
        }
        return null;
    }

    private void check(PrsYieldMaster prsYieldMaster) {
        List<PrsYieldSalve> prsYieldSalves = prsYieldMasterMapper.selectPrsSlaveByCode(prsYieldMaster);
        for (PrsYieldSalve prsYieldSalf : prsYieldSalves) {
            if(prsYieldSalf.getWorkersIds().isEmpty()){
                throw  new CommonException("工人不能为空");
            }
        }
        List<PrsYieldSalve> list =   prsYieldMasterMapper.selectPrsSlave(prsYieldMaster);
        for (PrsYieldSalve prsYieldSalve : list) {
            PrsProductMaster prsProductMaster = new PrsProductMaster();
            prsProductMaster.setPrsCode(prsYieldMaster.getPrsCode());
            PrsProductMaster prsProductMaster1 = prsProductMasterMapper.selectPrsProductMasterById(prsProductMaster);
            BigDecimal invQuantity = prsProductMaster1.getInvQuantity();
            BigDecimal quantity = prsYieldSalve.getQuantity();
            if(invQuantity.compareTo(quantity) != 0){
                throw  new CommonException("报工数量不正确");
            }
        }
        prsProductMasterMapper.isYield(prsYieldMaster.getPrsCode(),"1");
        prsYieldMasterMapper.check(prsYieldMaster.getPrsCode(),Constants.CHECK);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrsYieldMaster batchAntiCheck(List<PrsYieldMaster> list) {
        List<PrsYieldMaster> prsYieldMasters = prsYieldMasterMapper.selectPrsYieIdStatus(list);
        for (PrsYieldMaster prsYieldMaster : prsYieldMasters) {
            if(!prsYieldMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException("单据审核");
            }
        }
        for (PrsYieldMaster prsYieldMaster : list) {
            prsProductMasterMapper.isYield(prsYieldMaster.getPrsCode(),"0");
            prsYieldMasterMapper.check(prsYieldMaster.getPrsCode(),Constants.RETURN);
        }
        if(list.size() > 0 ){
            return prsYieldMasterMapper.selectPrsYieldMasterById(list.get(0));
        }
        return null;
    }

    /**
     * 新增生产报工单子信息
     * 
     * @param prsYieldMaster 生产报工单主对象
     */
    public void insertPrsYieldSalve(PrsYieldMaster prsYieldMaster)
    {
        List<PrsYieldSalve> prsYieldSalveList = prsYieldMaster.getPrsYieldSalveList();
        String prsCode = prsYieldMaster.getPrsCode();
        if (StringUtils.isNotNull(prsYieldSalveList))
        {
            List<PrsYieldSalve> list = new ArrayList<PrsYieldSalve>();
            for (PrsYieldSalve prsYieldSalve : prsYieldSalveList)
            {
                prsYieldSalve.setPrsCode(prsCode);
                list.add(prsYieldSalve);
            }
            if (list.size() > 0)
            {
                prsYieldMasterMapper.batchPrsYieldSalve(list);
            }
        }
    }


    public void deletePrsYieldMasterByCodes(List<PrsYieldMaster> yields) {
        prsYieldMasterMapper.deletePrsYieldSalveByPrsCodes(yields);
         prsYieldMasterMapper.deletePrsYieldMasterByIds(yields);

    }
}
