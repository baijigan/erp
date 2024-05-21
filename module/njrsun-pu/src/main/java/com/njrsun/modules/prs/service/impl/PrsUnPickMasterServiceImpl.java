package com.njrsun.modules.prs.service.impl;

import com.njrsun.common.constant.Constants;
import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.core.domain.entity.SysDept;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.om.enums.WorkStatus;
import com.njrsun.modules.prs.contacts.PrsContacts;
import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.mapper.PrsPickMasterMapper;
import com.njrsun.modules.prs.mapper.PrsUnPickMasterMapper;
import com.njrsun.modules.prs.service.IPrsPickMasterService;
import com.njrsun.modules.prs.service.IPrsUnPickMasterService;
import com.njrsun.api.mp.service.MpMbomPortalService;
import com.njrsun.system.mapper.SysDeptMapper;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import com.njrsun.system.service.impl.SysDictTypeServiceImpl;
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
 * 生产领料单Service业务层处理
 * 
 * @author njrsun
 * @date 2021-11-18
 */
@Service
public class PrsUnPickMasterServiceImpl implements IPrsUnPickMasterService
{
    @Autowired
    private PrsUnPickMasterMapper prsUnPickMasterMapper;

    @Autowired
    private PrsPickMasterMapper prsPickMasterMapper;

    @Autowired
    private IPrsPickMasterService prsPickMasterService;

    @Autowired
    private SysCoderServiceImpl sysCoderService;
    @Autowired
    private PrsOrderMasterServiceImpl prsOrderMasterService;
    @Autowired
    private SysDictTypeServiceImpl sysDictTypeService;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private MpMbomPortalService mpMbomPortalService;

    /**
     * 查询生产领料单
     * 
     * @param prsUnPickMaster 生产领料单ID
     * @return 生产领料单
     */
    @Override
    public PrsUnPickMaster selectPrsUnPickMasterById(PrsUnPickMaster prsUnPickMaster)
    {
        return prsUnPickMasterMapper.selectPrsUnPickMasterById(prsUnPickMaster);
    }

    /**
     * 查询生产领料单列表
     * 
     * @param prsUnPickMaster 生产领料单
     * @return 生产领料单
     */
    @Override
    public List<PrsUnPickMaster> selectPrsUnPickMasterList(PrsUnPickMaster prsUnPickMaster)
    {
        List<PrsUnPickMaster> prsUnPickMasters = prsUnPickMasterMapper.selectPrsUnPickMasterList(prsUnPickMaster);
        HashMap<String, String> deptMap = new HashMap<>();
       return prsUnPickMasters;
    }

    /**
     * 新增生产领料单
     * 
     * @param prsUnPickMaster 生产领料单
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPrsUnPickMaster(PrsUnPickMaster prsUnPickMaster)
    {
        prsUnPickMaster.setCreateBy(SecurityUtils.getUsername());
        prsUnPickMaster.setPrsCode(sysCoderService.generate(PrsContacts.UNPICK,prsUnPickMaster.getWorkType()));
        int rows = prsUnPickMasterMapper.insertPrsUnPickMaster(prsUnPickMaster);
        return rows;
    }

    /**
     * 修改生产领料单
     * 
     * @param prsUnPickMaster 生产领料单
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePrsUnPickMaster(PrsUnPickMaster prsUnPickMaster)
    {
        prsUnPickMaster.setCreateBy(SecurityUtils.getUsername());
        PrsUnPickMaster prsUnPickMaster1 = prsUnPickMasterMapper.selectPrsUnPickForUpdate(prsUnPickMaster.getPrsCode());
        if(prsUnPickMaster1.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("审核单据,无法修改");
        }
        /** 老Id */
        ArrayList<Long> oldId = prsUnPickMasterMapper.selectPrsUnPickSalveIdByCode(prsUnPickMaster.getPrsCode());
        /**  新数据  */
        List<PrsUnPickSalve> prsUnPickSalveList = prsUnPickMaster.getPrsUnPickSalveList();
        /** 新ID */
        ArrayList<Long> newId = new ArrayList<>();
        ArrayList<PrsUnPickSalve> newData = new ArrayList<>();
        ArrayList<PrsUnPickSalve> editData = new ArrayList<>();
        for (PrsUnPickSalve prsUnPickSalve : prsUnPickSalveList) {
            if(prsUnPickSalve.getUniqueId() == null){
                //新增数据
                prsUnPickSalve.setCreateBy(SecurityUtils.getUsername());
                prsUnPickSalve.setPrsCode(prsUnPickMaster.getPrsCode());
                newData.add(prsUnPickSalve);
            }
            else{
                newId.add(prsUnPickSalve.getUniqueId());
            }
        }
        prsUnPickMaster.setUpdateBy(SecurityUtils.getUsername());
        // 删除ID
        List<Long> delId = oldId.stream().filter(t -> !newId.contains(t)).collect(Collectors.toList());

        for (PrsUnPickSalve prsUnPickSalve : prsUnPickSalveList) {
            if(prsUnPickSalve.getUniqueId() == null){
                //新增数据
                prsUnPickSalve.setCreateBy(SecurityUtils.getUsername());
                prsUnPickSalve.setPrsCode(prsUnPickMaster.getPrsCode());
                newData.add(prsUnPickSalve);
            }
            else{
                newId.add(prsUnPickSalve.getUniqueId());
            }
        }
        //修改ID
        oldId.retainAll(newId);

        for (Long aLong : oldId) {
            for (PrsUnPickSalve prsUnPickSalve : prsUnPickSalveList) {
                if(prsUnPickSalve.getUniqueId().equals(aLong)){
                    prsUnPickSalve.setUpdateBy(prsUnPickMaster.getUpdateBy());
                    editData.add(prsUnPickSalve);
                    break;
                }
            }
        }
        Long[] longs = delId.toArray(new Long[delId.size()]);
        if(longs.length != 0){
            prsUnPickMasterMapper.deletePrsUnPickSalveByIds(longs);
        }
        if(newData.size() != 0){
            prsUnPickMasterMapper.batchPrsUnPickSalve(newData);
        }
        int j=1;
        if(editData.size() != 0){
            j = prsUnPickMasterMapper.updatePrsUnPickSalve(editData);
        }
        int i = prsUnPickMasterMapper.updatePrsUnPickMaster(prsUnPickMaster);
        if(i == 0 || j == 0){
            throw  new CommonException("修改失败");
        }
        return i;
    }

    /**
     * 批量删除生产领料单
     * 
     * @param list 需要删除的生产领料单ID
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePrsUnPickMasterByIds(List<PrsUnPickMaster> list)
    {
        List<PrsUnPickMaster> result = prsUnPickMasterMapper.selectPrsInvoiceListByCode(list);
        for (PrsUnPickMaster prsUnPickMaster : result) {
            if(prsUnPickMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException("审核单据，无法删除");
            }
        }
        prsUnPickMasterMapper.deletePrsUnPickSalveByPrsCodes(list);
        return prsUnPickMasterMapper.deletePrsUnPickMasterByIds(list);
    }

    /**
     * 删除生产领料单信息
     * 
     * @param uniqueId 生产领料单ID
     * @return 结果
     */
    @Override
    public int deletePrsUnPickMasterById(Long uniqueId)
    {
        prsUnPickMasterMapper.deletePrsUnPickSalveByPrsCode(uniqueId);
        return prsUnPickMasterMapper.deletePrsUnPickMasterById(uniqueId);
    }

    @Override
    public void changeWorkStatus(PrsUnPickMaster prsUnPickMaster) {
        String prsCode = prsUnPickMaster.getPrsCode();
        String workStatus = prsUnPickMaster.getWorkStatus();
        PrsUnPickMaster prsUnPickMaster1 = prsUnPickMasterMapper.selectPrsUnPickForUpdate(prsCode);
        if(prsUnPickMaster1.getWorkStatus().equals(WorkStatus.SYSTEM_CLOSING.getValue())){
            throw  new CommonException("系统关闭状态,无法操作");
        }
        Integer i;
        if(prsUnPickMaster1.getWorkStatus().equals(workStatus)){
            i = prsUnPickMasterMapper.updateWorkStatus(WorkStatus.NORMAl.getValue(), prsUnPickMaster);
        }else{
            i = prsUnPickMasterMapper.updateWorkStatus(workStatus, prsUnPickMaster);
        }
        if(i == 0 ){
            throw  new CommonException("操作失败");
        }

    }

    @Override
    public List<PrsUnPickExport> getDetail(PrsUnPickExport prsUnPickExport) {
        return  prsUnPickMasterMapper.getDetail(prsUnPickExport);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, String>> lead(Map<String,String> query) {
        return prsUnPickMasterMapper.lead(query);
    }

    @Override
    public List<Map<String, String>> leadInto(Map<String,String> query) {
        List<Map<String, String>> lists= prsUnPickMasterMapper.leadInto(query);
        for(Map<String, String> list : lists){
            list.put("work_type", PrsContacts.UNPICK);
            list.put("invoice_type", PrsContacts.INVOICE);
        }

        return lists;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrsUnPickMaster batchCheck(List<PrsUnPickMaster> list) {
        for (PrsUnPickMaster s : list) {
            PrsUnPickMaster prsUnPickMaster = prsUnPickMasterMapper.selectPrsUnPickMasterByCodeForUpdate(s.getPrsCode());
            List<PrsUnPickSalve> salves =   prsUnPickMasterMapper.selectUnPickSalveByCode(s);
            for (PrsUnPickSalve prsUnPickSalve : salves) {
                Long planUniqueId = prsUnPickSalve.getWoUniqueId();
                if(!planUniqueId.equals(0L)){  // 加锁
                    PrsPickMaster prsPickMaster = prsPickMasterMapper.selectPrsPickMasterForUpdate(prsUnPickSalve.getWoCode());
                    if(!prsPickMaster.getInvoiceStatus().equals(UserConstants.NOT_UNIQUE)){
                        throw  new CommonException("审核失败，上游单据非审核状态！");
                    }
                    PrsPickSalve prsPickSalve = prsPickMasterMapper.selectPrsPickSalveById(planUniqueId);
                    BigDecimal amount = prsUnPickSalve.getQuantity();
                    BigDecimal wiQuantity = prsPickSalve.getWiQuantity();
                    BigDecimal wiQuantityR = prsPickSalve.getWiQuantityR();
                    BigDecimal sum = amount.add(wiQuantityR);
                    if(wiQuantity.compareTo(sum) < 0){
                        throw  new CommonException(prsPickSalve.getPrsCode() +":审核失败");
                    }
                    Integer i = prsPickMasterMapper.changeQuantity_r(planUniqueId.toString(), amount);
                    if(i == 0){
                        throw  new CommonException("审核失败");
                    }
                }
                int i = prsUnPickMasterMapper.changeStatus(Constants.CHECK, SecurityUtils.getUsername(), prsUnPickMaster);
                if(i == 0){
                    throw  new CommonException("审核失败");
                }
            }
        }
        if(list.size() == 1){
            return prsUnPickMasterMapper.selectPrsUnPickMasterById(list.get(0));
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrsUnPickMaster batchAntiCheck(List<PrsUnPickMaster> list) {
        for (PrsUnPickMaster unPickMaster : list) {
            PrsUnPickMaster master = prsUnPickMasterMapper.selectPrsUnPickMasterByCodeForUpdate(unPickMaster.getPrsCode());
            List<PrsUnPickSalve> prsUnPickSalveList =  prsUnPickMasterMapper.selectUnPickSalveByCode(unPickMaster);
            for (PrsUnPickSalve prsUnPickSalve : prsUnPickSalveList) {
                BigDecimal wiQuantityB = prsUnPickSalve.getWiQuantity();
                BigDecimal wiQuantityR = prsUnPickSalve.getWiQuantityR();
                if(wiQuantityB.compareTo(BigDecimal.ZERO) > 0 || wiQuantityR.compareTo(BigDecimal.ZERO) > 0){
                    throw  new CommonException("单据已被使用,无法反审核");
                }
                if(StringUtils.isNotNull(prsUnPickSalve.getWoInvoiceId()) || !prsUnPickSalve.getWoUniqueId().equals(0L)){
                    PrsPickMaster prsPickMaster = prsPickMasterMapper.selectPrsPickForUpdate(prsUnPickSalve.getWoCode());
                    if(!prsPickMaster.getInvoiceStatus().equals(UserConstants.NOT_UNIQUE)){
                        throw  new CommonException("审核失败，上游单据非审核状态");
                    }
                    PrsPickSalve prsPickSalve = prsPickMasterMapper.selectPrsPickSalveById(prsUnPickSalve.getWoUniqueId());
                    BigDecimal wiQuantityR1 = prsPickSalve.getWiQuantityR();
                    BigDecimal quantity = prsUnPickSalve.getQuantity();
                    Long id = prsUnPickSalve.getWoUniqueId();
                    String woCode = prsUnPickSalve.getWoCode();
                    if(wiQuantityR1.compareTo(quantity) < 0 ){
                        throw  new CommonException("反审核失败，无法退货");
                    }
                    prsPickMasterMapper.changeQuantity_r(id.toString(), quantity.negate());
                }
            }
            Integer i = prsUnPickMasterMapper.changeStatus(Constants.RETURN, null, master);
            if(i ==0){
                throw  new CommonException("反审核失败");
            }
        }
        if(list.size() == 1 ){
            return prsUnPickMasterMapper.selectPrsUnPickMasterById(list.get(0));
        }
        return null;
    }

    @Override
    public List<String> selectPrsCode(PrsUnPickMaster prsUnPickMaster) {
        return prsUnPickMasterMapper.selectPrsCode(prsUnPickMaster);
    }

    @Override
    public List<Map<String, String>> chain(PrsUnPickMaster prsProductExport) {
     List<Map<String,String>>  up =     prsUnPickMasterMapper.upData(prsProductExport);
        for (Map<String, String> stringStringMap : up) {
            HashMap<String, String> transform = sysDictTypeService.transform(stringStringMap.get("invoice"));
            HashMap<String, String> transform1 = sysDictTypeService.transform(stringStringMap.get("type"));
            stringStringMap.put("invoice_name",transform.get(stringStringMap.get("invoice_id")));
            stringStringMap.put("type_name",transform1.get(stringStringMap.get("type_id")));
        }
        return  up;
    }

    @Override
    public PrsUnPickMaster selectPrsUnPickMasterForUpdate(String woCode) {
        return prsUnPickMasterMapper.selectPrsUnPickMasterByCodeForUpdate(woCode);
    }

    @Override
    public PrsUnPickSalve selectPrsUnPickSalveById(Long uniqueId) {
        return prsUnPickMasterMapper.selectPrsUnPickSalveById(uniqueId);
    }

    @Override
    public void changeQuantity(Long uniqueId, BigDecimal quantity) {
        prsUnPickMasterMapper.changeQuantity(uniqueId, quantity);
    }

    private void antiCheck(PrsUnPickMaster prsUnPickMaster) {
        PrsUnPickMaster prsUnPickMaster1 = prsUnPickMasterMapper.selectPrsUnPickForUpdate(prsUnPickMaster.getPrsCode());
        PrsUnPickMaster prsUnPickMaster2 = prsUnPickMasterMapper.selectPrsUnPickMasterById(prsUnPickMaster1);
        List<PrsUnPickSalve> prsUnPickSalveList = prsUnPickMaster2.getPrsUnPickSalveList();
        String lastCode = null;
        if(prsUnPickSalveList.size() != 0){
            lastCode = prsUnPickSalveList.get(0).getWoCode();
        }
        for (PrsUnPickSalve prsUnPickSalve : prsUnPickSalveList) {
            String woCode = prsUnPickSalve.getWoCode();
            Map<String,String> lock =  mpMbomPortalService.selectMpMbomPortalForUpdate(woCode);
            Map<String,Object> salve =   mpMbomPortalService.selectMpMbomSalveById(prsUnPickSalve.getWoUniqueId());
            BigDecimal bigDecimal = (BigDecimal) salve.get("wi_quantity");
            BigDecimal quantity1 = prsUnPickSalve.getQuantity();
            if(bigDecimal.compareTo(quantity1)<0){
                throw  new CommonException("反审核失败");
            }
            int i = mpMbomPortalService.updateMpMbomSalve(prsUnPickSalve.getWoUniqueId(), quantity1.negate());
            if(i == 0){
                throw  new CommonException("反审核失败");
            }
            if(!lastCode.equals(prsUnPickSalve.getWoCode())){
                mpMbomPortalService.changeWorkStatus(WorkStatus.NORMAl.getValue(),woCode);
            }
            lastCode = prsUnPickSalve.getWoCode();
        }
        Integer integer = prsUnPickMasterMapper.changeStatus(Constants.RETURN, null, prsUnPickMaster);
        if(integer ==0){
            throw  new CommonException("反审核失败");
        }
        if(prsUnPickSalveList.size() != 0){
            mpMbomPortalService.changeWorkStatus(WorkStatus.NORMAl.getValue(),lastCode);
        }
    }

    private void check(PrsUnPickMaster prsUnPickMaster) {
        PrsUnPickMaster prsUnPickMaster1 = prsUnPickMasterMapper.selectPrsUnPickForUpdate(prsUnPickMaster.getPrsCode());
        PrsUnPickMaster prsUnPickMaster2 = prsUnPickMasterMapper.selectPrsUnPickMasterById(prsUnPickMaster1);
        String lastCode = null;
        List<PrsUnPickSalve> prsUnPickSalveList = prsUnPickMaster2.getPrsUnPickSalveList();
        if(prsUnPickSalveList.size() > 0 ){
            lastCode = prsUnPickSalveList.get(0).getWoCode();
        }
        for (PrsUnPickSalve prsUnPickSalve : prsUnPickSalveList) {
            String woCode = prsUnPickSalve.getWoCode();
            Map<String,String> lock =  mpMbomPortalService.selectMpMbomPortalForUpdate(woCode);
           Map<String,Object> salve =   mpMbomPortalService.selectMpMbomSalveById(prsUnPickSalve.getWoUniqueId());
            BigDecimal bigDecimal = (BigDecimal) salve.get("quantity");
            BigDecimal bigDecimal1 = (BigDecimal) salve.get("wi_quantity");
            BigDecimal quantity1 = prsUnPickSalve.getQuantity();
            if(bigDecimal.compareTo(bigDecimal1.add(quantity1))<0){
                throw  new CommonException("审核失败 "+salve.get("inv_name")+" 数量不足");
            }
            int i = mpMbomPortalService.updateMpMbomSalve(prsUnPickSalve.getWoUniqueId(), quantity1);
            if(i == 0){
                throw  new CommonException("审核失败");
            }
            if(!woCode.equals(lastCode)){
              BigDecimal sum =    mpMbomPortalService.selectSumQuantity(lastCode);
                if(sum.compareTo(BigDecimal.ZERO) == 0 ){
                    mpMbomPortalService.changeWorkStatus(WorkStatus.SYSTEM_CLOSING.getValue(),lastCode);
                }
                lastCode =woCode;
            }
        }
        Integer integer = prsUnPickMasterMapper.changeStatus(Constants.CHECK, SecurityUtils.getUsername(), prsUnPickMaster);
        if(integer ==0){
            throw  new CommonException("审核失败");
        }
        if(prsUnPickSalveList.size() != 0){
            BigDecimal sum = mpMbomPortalService.selectSumQuantity(lastCode);
            if(sum.compareTo(BigDecimal.ZERO) == 0 ){
                mpMbomPortalService.changeWorkStatus(WorkStatus.SYSTEM_CLOSING.getValue(),lastCode);
            }
        }
    }

    /**
     * 新增生产领料单从信息
     * 
     * @param prsUnPickMaster 生产领料单对象
     */
    public void insertPrsUnPickSalve(PrsUnPickMaster prsUnPickMaster)
    {
        List<PrsUnPickSalve> prsUnPickSalveList = prsUnPickMaster.getPrsUnPickSalveList();
        String uniqueId = prsUnPickMaster.getPrsCode();
        if (StringUtils.isNotNull(prsUnPickSalveList))
        {
            List<PrsUnPickSalve> list = new ArrayList<PrsUnPickSalve>();
            for (PrsUnPickSalve prsUnPickSalve : prsUnPickSalveList)
            {
                prsUnPickSalve.setCreateBy(prsUnPickMaster.getCreateBy());
                prsUnPickSalve.setPrsCode(uniqueId);
                list.add(prsUnPickSalve);
            }
            if (list.size() > 0)
            {
                prsUnPickMasterMapper.batchPrsUnPickSalve(list);
            }
        }
    }


    public List<Map<String, String>> selectPrsUnPickByPrsOrderCode(PrsOrderExport prsOrderExport) {
        return prsUnPickMasterMapper.selectPrsUnPickByPrsOrderCode(prsOrderExport);
    }


    public PrsUnPickMaster selectPrsUnPickMasterByOrderCode(String prsCode) {
        return  prsUnPickMasterMapper.selectPrsByOrderCode(prsCode);
    }

}
