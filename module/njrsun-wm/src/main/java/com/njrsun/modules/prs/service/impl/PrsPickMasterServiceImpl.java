package com.njrsun.modules.prs.service.impl;

import com.njrsun.common.constant.Constants;
import com.njrsun.common.core.domain.entity.SysDept;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.om.enums.WorkStatus;
import com.njrsun.modules.prs.contacts.PrsContacts;
import com.njrsun.modules.prs.domain.PrsOrderExport;
import com.njrsun.modules.prs.domain.PrsPickExport;
import com.njrsun.modules.prs.domain.PrsPickMaster;
import com.njrsun.modules.prs.domain.PrsPickSalve;
import com.njrsun.modules.prs.mapper.PrsPickMasterMapper;
import com.njrsun.modules.prs.service.IPrsPickMasterService;
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
public class PrsPickMasterServiceImpl implements IPrsPickMasterService
{
    @Autowired
    private PrsPickMasterMapper prsPickMasterMapper;

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
     * @param prsPickMaster 生产领料单ID
     * @return 生产领料单
     */
    @Override
    public PrsPickMaster selectPrsPickMasterById(PrsPickMaster prsPickMaster)
    {
        return prsPickMasterMapper.selectPrsPickMasterById(prsPickMaster);
    }

    /**
     * 查询生产领料单列表
     * 
     * @param prsPickMaster 生产领料单
     * @return 生产领料单
     */
    @Override
    public List<PrsPickMaster> selectPrsPickMasterList(PrsPickMaster prsPickMaster)
    {
        List<PrsPickMaster> prsPickMasters = prsPickMasterMapper.selectPrsPickMasterList(prsPickMaster);
        HashMap<String, String> transform = sysDictTypeService.transform(PrsContacts.PICK);
        HashMap<String, String> sys_work_status = sysDictTypeService.transform("sys_work_status");
        List<SysDept> sysDepts = sysDeptMapper.selectDeptList(new SysDept());
        HashMap<String, String> deptMap = new HashMap<>();
        for (SysDept sysDept : sysDepts) {
            deptMap.put(sysDept.getDeptId().toString(),sysDept.getDeptName());
        }
        for (PrsPickMaster pickMaster : prsPickMasters) {
            Map<String, Object> params = pickMaster.getParams();
            params.put(PrsContacts.ORDER,transform.get(pickMaster.getWorkType()));
            params.put("sys_work_status",sys_work_status.get(pickMaster.getWorkStatus()));
            params.put("deptName",deptMap.get(pickMaster.getWorkDept()));

        }
       return prsPickMasters;
    }

    /**
     * 新增生产领料单
     * 
     * @param prsPickMaster 生产领料单
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPrsPickMaster(PrsPickMaster prsPickMaster)
    {
        prsPickMaster.setCreateBy(SecurityUtils.getUsername());
        prsPickMaster.setPrsCode(sysCoderService.generate(PrsContacts.PICK,prsPickMaster.getWorkType()));
        int rows = prsPickMasterMapper.insertPrsPickMaster(prsPickMaster);
        insertPrsPickSalve(prsPickMaster);
        return rows;
    }

    /**
     * 修改生产领料单
     * 
     * @param prsPickMaster 生产领料单
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePrsPickMaster(PrsPickMaster prsPickMaster)
    {
        PrsPickMaster prsPickMaster1 = prsPickMasterMapper.selectPrsPickForUpdate(prsPickMaster.getPrsCode());
        if(prsPickMaster1.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("审核单据,无法修改");
        }
        /** 老Id */
        ArrayList<Long> oldId = prsPickMasterMapper.selectPrsPickSalveIdByCode(prsPickMaster.getPrsCode());
        /**  新数据  */
        List<PrsPickSalve> prsPickSalveList = prsPickMaster.getPrsPickSalveList();
        /** 新ID */
        ArrayList<Long> newId = new ArrayList<>();
        ArrayList<PrsPickSalve> newData = new ArrayList<>();
        ArrayList<PrsPickSalve> editData = new ArrayList<>();
        for (PrsPickSalve prsPickSalve : prsPickSalveList) {
            if(prsPickSalve.getUniqueId() == null){
                //新增数据
                prsPickSalve.setCreateBy(SecurityUtils.getUsername());
                prsPickSalve.setPrsCode(prsPickMaster.getPrsCode());
                newData.add(prsPickSalve);
            }
            else{
                newId.add(prsPickSalve.getUniqueId());
            }
        }
        prsPickMaster.setUpdateBy(SecurityUtils.getUsername());
        // 删除ID
        List<Long> delId = oldId.stream().filter(t -> !newId.contains(t)).collect(Collectors.toList());

        //修改ID
        oldId.retainAll(newId);

        for (Long aLong : oldId) {
            for (PrsPickSalve prsPickSalve : prsPickSalveList) {
                if(prsPickSalve.getUniqueId().equals(aLong)){
                    prsPickSalve.setUpdateBy(prsPickMaster.getUpdateBy());
                    editData.add(prsPickSalve);
                    break;
                }
            }
        }
        Long[] longs = delId.toArray(new Long[delId.size()]);
        if(longs.length != 0){
            prsPickMasterMapper.deletePrsPickSalveByIds(longs);
        }
        if(newData.size() != 0){
            prsPickMasterMapper.batchPrsPickSalve(newData);
        }
        int j=1;
        if(editData.size() != 0){
            j = prsPickMasterMapper.updatePrsPickSalve(editData);
        }
        int i = prsPickMasterMapper.updatePrsPickMaster(prsPickMaster);
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
    public int deletePrsPickMasterByIds(List<PrsPickMaster> list)
    {
        List<PrsPickMaster> result = prsPickMasterMapper.selectPrsInvoiceListByCode(list);
        for (PrsPickMaster prsPickMaster : result) {
            if(prsPickMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException("审核单据，无法删除");
            }
        }
        prsPickMasterMapper.deletePrsPickSalveByPrsCodes(list);
        return prsPickMasterMapper.deletePrsPickMasterByIds(list);
    }

    /**
     * 删除生产领料单信息
     * 
     * @param uniqueId 生产领料单ID
     * @return 结果
     */
    @Override
    public int deletePrsPickMasterById(Long uniqueId)
    {
        prsPickMasterMapper.deletePrsPickSalveByPrsCode(uniqueId);
        return prsPickMasterMapper.deletePrsPickMasterById(uniqueId);
    }

    @Override
    public void changeWorkStatus(PrsPickMaster prsPickMaster) {
        String prsCode = prsPickMaster.getPrsCode();
        String workStatus = prsPickMaster.getWorkStatus();
        PrsPickMaster prsPickMaster1 = prsPickMasterMapper.selectPrsPickForUpdate(prsCode);
        if(prsPickMaster1.getWorkStatus().equals(WorkStatus.SYSTEM_CLOSING.getValue())){
            throw  new CommonException("系统关闭状态,无法操作");
        }
        Integer i;
        if(prsPickMaster1.getWorkStatus().equals(workStatus)){
            i = prsPickMasterMapper.updateWorkStatus(WorkStatus.NORMAl.getValue(), prsPickMaster);
        }else{
            i = prsPickMasterMapper.updateWorkStatus(workStatus, prsPickMaster);
        }
        if(i == 0 ){
            throw  new CommonException("操作失败");
        }

    }

    @Override
    public List<PrsPickExport> getDetail(PrsPickExport prsPickExport) {
        return  prsPickMasterMapper.getDetail(prsPickExport);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, String>> lead(Map<String,String> query) {
        return prsPickMasterMapper.lead(query);
    }

    @Override
    public List<Map<String, String>> leadInto(Map<String,String>  query) {
        List<Map<String, String>> lists= prsPickMasterMapper.leadInto(query);
        for(Map<String, String> list : lists){
            list.put("work_type", PrsContacts.PICK);
            list.put("invoice_type", PrsContacts.INVOICE);
        }

        return lists;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeQuantity(String uniqueId, BigDecimal quantity){
        prsPickMasterMapper.changeQuantity(uniqueId, quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeQuantity_r(String uniqueId, BigDecimal quantity){
        prsPickMasterMapper.changeQuantity_r(uniqueId, quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrsPickMaster batchCheck(List<PrsPickMaster> list) {
        List<PrsPickMaster> prsProductMasters = prsPickMasterMapper.selectPrsInvoiceListByCode(list);
        for (PrsPickMaster prsPickMaster : prsProductMasters) {
            if(!prsPickMaster.getInvoiceStatus().equals(Constants.OPEN)){
                throw  new CommonException(prsPickMaster.getPrsCode() + ": 单据 非开立状态");
            }
        }
        for (PrsPickMaster prsPickMaster : list) {
            check(prsPickMaster);
        }
        if(list.size() == 1){
            return prsPickMasterMapper.selectPrsPickMasterById(list.get(0));
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrsPickMaster batchAntiCheck(List<PrsPickMaster> list) {
        List<PrsPickMaster> prsProductMasters = prsPickMasterMapper.selectPrsInvoiceListByCode(list);
        for (PrsPickMaster prsPickMaster : prsProductMasters) {
            if(!prsPickMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException(prsPickMaster.getPrsCode() + ": 单据 非审核状态");
            }
        }
        for (PrsPickMaster prsPickMaster : list) {
            antiCheck(prsPickMaster);
        }
        if(list.size() == 1){
            return prsPickMasterMapper.selectPrsPickMasterById(list.get(0));
        }
        return null;
    }


    @Override
    public List<String> selectPrsCode(PrsPickMaster prsPickMaster) {
        return prsPickMasterMapper.selectPrsCode(prsPickMaster);
    }


    @Override
    public List<Map<String, String>> chain(PrsPickMaster prsProductExport) {
     List<Map<String,String>>  up =     prsPickMasterMapper.upData(prsProductExport);
        for (Map<String, String> stringStringMap : up) {
            HashMap<String, String> transform = sysDictTypeService.transform(stringStringMap.get("invoice"));
            HashMap<String, String> transform1 = sysDictTypeService.transform(stringStringMap.get("type"));
            stringStringMap.put("invoice_name",transform.get(stringStringMap.get("invoice_id")));
            stringStringMap.put("type_name",transform1.get(stringStringMap.get("type_id")));
        }
        return  up;
    }

    @Override
    public PrsPickMaster selectPrsPickMasterForUpdate(String woCode){
        return prsPickMasterMapper.selectPrsPickMasterForUpdate(woCode);
    }

    @Override
    public PrsPickSalve selectPrsPickSalveById(Long uniqueId) {
        return prsPickMasterMapper.selectPrsPickSalveById(uniqueId);
    }

    @Override
    public List<PrsPickSalve> selectQuantity(String code) {
        return prsPickMasterMapper.selectQuantity(code);
    }

    private void antiCheck(PrsPickMaster prsPickMaster) {
        PrsPickMaster prsPickMaster1 = prsPickMasterMapper.selectPrsPickForUpdate(prsPickMaster.getPrsCode());
        PrsPickMaster prsPickMaster2 = prsPickMasterMapper.selectPrsPickMasterById(prsPickMaster1);
        List<PrsPickSalve> prsPickSalveList = prsPickMaster2.getPrsPickSalveList();
        String lastCode = null;
        if(prsPickSalveList.size() != 0){
            lastCode = prsPickSalveList.get(0).getWoCode();
        }
        for (PrsPickSalve prsPickSalve : prsPickSalveList) {
            Long woUniqueId= prsPickSalve.getWoUniqueId();
            if(woUniqueId!=null && !woUniqueId.equals(0L)){
                String woCode = prsPickSalve.getWoCode();
                Map<String,String> lock =  mpMbomPortalService.selectMpMbomPortalForUpdate(woCode);
                Map<String,Object> salve =   mpMbomPortalService.selectMpMbomSalveById(prsPickSalve.getWoUniqueId());
                BigDecimal bigDecimal = (BigDecimal) salve.get("wi_quantity");
                BigDecimal bigDecimal2 = (BigDecimal) salve.get("wi_quantity_r");
                BigDecimal quantity1 = prsPickSalve.getQuantity();
                if(bigDecimal.compareTo(quantity1)<0 || bigDecimal2.compareTo(BigDecimal.ZERO)>0){
                    throw  new CommonException("已出库，反审核失败");
                }

                int i = mpMbomPortalService.updateMpMbomSalve(prsPickSalve.getWoUniqueId(), quantity1.negate());
                if(i == 0){
                    throw  new CommonException("反审核失败");
                }
                if(!lastCode.equals(prsPickSalve.getWoCode())){
                    mpMbomPortalService.changeWorkStatus(WorkStatus.NORMAl.getValue(),woCode);
                }
                lastCode = prsPickSalve.getWoCode();
            }
        }

        if(prsPickSalveList.size() != 0 && lastCode!=null){
            mpMbomPortalService.changeWorkStatus(WorkStatus.NORMAl.getValue(),lastCode);
        }

        Integer integer = prsPickMasterMapper.changeStatus(Constants.RETURN, null, prsPickMaster);
        if(integer ==0){
            throw  new CommonException("反审核失败");
        }
    }

    private void check(PrsPickMaster prsPickMaster) {
        PrsPickMaster prsPickMaster1 = prsPickMasterMapper.selectPrsPickForUpdate(prsPickMaster.getPrsCode());
        PrsPickMaster prsPickMaster2 = prsPickMasterMapper.selectPrsPickMasterById(prsPickMaster1);
        String lastCode = null;
        List<PrsPickSalve> prsPickSalveList = prsPickMaster2.getPrsPickSalveList();
        if(prsPickSalveList.size() > 0 ){
            lastCode = prsPickSalveList.get(0).getWoCode();
        }
        for (PrsPickSalve prsPickSalve : prsPickSalveList) {
            Long woUniqueId= prsPickSalve.getWoUniqueId();
            if(woUniqueId!=null && !woUniqueId.equals(0L)){
                String woCode = prsPickSalve.getWoCode();
                Map<String,String> lock =  mpMbomPortalService.selectMpMbomPortalForUpdate(woCode);
                Map<String,Object> salve =   mpMbomPortalService.selectMpMbomSalveById(prsPickSalve.getWoUniqueId());
                BigDecimal bigDecimal = (BigDecimal) salve.get("quantity");
                BigDecimal bigDecimal1 = (BigDecimal) salve.get("wi_quantity");
                BigDecimal quantity1 = prsPickSalve.getQuantity();
                if(bigDecimal.compareTo(bigDecimal1.add(quantity1))<0){
                    throw  new CommonException("审核失败 "+salve.get("inv_name")+" 数量不足");
                }
                int i = mpMbomPortalService.updateMpMbomSalve(prsPickSalve.getWoUniqueId(), quantity1);
                if(i == 0){
                    throw  new CommonException("审核失败");
                }
                if(!woCode.equals(lastCode)){
                  BigDecimal sum =    mpMbomPortalService.selectSumQuantity(lastCode);
                    if(sum!=null && sum.compareTo(BigDecimal.ZERO) == 0 ){
                        mpMbomPortalService.changeWorkStatus(WorkStatus.SYSTEM_CLOSING.getValue(),lastCode);
                    }
                    lastCode =woCode;
                }
            }
        }

        if(prsPickSalveList.size() != 0 && lastCode!=null){
            BigDecimal sum = mpMbomPortalService.selectSumQuantity(lastCode);
            if(sum!=null && sum.compareTo(BigDecimal.ZERO) == 0 ){
                mpMbomPortalService.changeWorkStatus(WorkStatus.SYSTEM_CLOSING.getValue(),lastCode);
            }
        }

        Integer integer = prsPickMasterMapper.changeStatus(Constants.CHECK, SecurityUtils.getUsername(), prsPickMaster);
        if(integer ==0){
            throw  new CommonException("审核失败");
        }
    }

    /**
     * 新增生产领料单从信息
     * 
     * @param prsPickMaster 生产领料单对象
     */
    public void insertPrsPickSalve(PrsPickMaster prsPickMaster)
    {
        List<PrsPickSalve> prsPickSalveList = prsPickMaster.getPrsPickSalveList();
        String uniqueId = prsPickMaster.getPrsCode();
        if (StringUtils.isNotNull(prsPickSalveList))
        {
            List<PrsPickSalve> list = new ArrayList<PrsPickSalve>();
            for (PrsPickSalve prsPickSalve : prsPickSalveList)
            {
                prsPickSalve.setCreateBy(prsPickMaster.getCreateBy());
                prsPickSalve.setPrsCode(uniqueId);
                list.add(prsPickSalve);
            }
            if (list.size() > 0)
            {
                prsPickMasterMapper.batchPrsPickSalve(list);
            }
        }
    }


    public List<Map<String, String>> selectPrsPickByPrsOrderCode(PrsOrderExport prsOrderExport) {
        return prsPickMasterMapper.selectPrsPickByPrsOrderCode(prsOrderExport);
    }


    public PrsPickMaster selectPrsPickMasterByOrderCode(String prsCode) {
        return  prsPickMasterMapper.selectPrsByOrderCode(prsCode);
    }

}
