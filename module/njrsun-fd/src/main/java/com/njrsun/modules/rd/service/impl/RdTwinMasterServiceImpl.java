package com.njrsun.modules.rd.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import cn.hutool.core.map.MapUtil;
import com.njrsun.common.core.domain.entity.SysDept;
import com.njrsun.common.core.domain.entity.SysDictData;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.constant.Constants;
import com.njrsun.common.enums.WorkStatus;

import com.njrsun.system.service.ISysCoderService;
import com.njrsun.system.service.ISysDeptService;
import com.njrsun.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.common.utils.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import com.njrsun.modules.rd.domain.RdTwinSalve;
import com.njrsun.modules.rd.constant.RdConstant;
import com.njrsun.modules.rd.mapper.RdTwinMasterMapper;
import com.njrsun.modules.rd.domain.RdTwinMaster;
import com.njrsun.modules.rd.service.IRdTwinMasterService;

/**
 * 孪生物料Service业务层处理
 * 
 * @author njrsun
 * @date 2022-12-05
 */
@Service
public class RdTwinMasterServiceImpl implements IRdTwinMasterService 
{
    @Autowired
    private RdTwinMasterMapper rdTwinMasterMapper;

    @Autowired
    private ISysDictTypeService sysDictTypeService;

    @Autowired
    private ISysCoderService sysCoderService;

    @Autowired
    private ISysDeptService sysDeptService;

    /**
     * 查询孪生物料单据
     * 
     * @param RdTwinMaster
     * @return RdTwinMaster
     */
    @Override
    public RdTwinMaster selectRdTwinMasterByCode(RdTwinMaster rdTwinMaster)
    {
        return rdTwinMasterMapper.selectRdTwinMasterBySelf(rdTwinMaster);
    }

    /**
     * 查询孪生物料列表
     * 
     * @param RdTwinMaster
     * @return List<RdTwinMaster>
     */
    @Override
    public List<RdTwinMaster> selectRdTwinMasterList(RdTwinMaster rdTwinMaster)
    {
        List<RdTwinMaster> list= rdTwinMasterMapper.selectRdTwinMasterList(rdTwinMaster);
        return transInvoiceWorkType(list);
    }

    // 数据字典翻译
    private List<RdTwinMaster> transInvoiceWorkType(List<RdTwinMaster> rdTwinMasters){
        List<SysDictData> invoice_type = sysDictTypeService.selectDictDataByType(RdConstant.INVOICE);
        List<SysDictData> work_type = sysDictTypeService.selectDictDataByType(RdConstant.TWIN);
        List<SysDictData> sys_invoice_status = sysDictTypeService.selectDictDataByType("sys_invoice_status");
        List<SysDictData> sys_work_status = sysDictTypeService.selectDictDataByType("sys_work_status");

        SysDept sysDept = new SysDept();
        List<SysDept> sysDepts = sysDeptService.selectDeptList(sysDept);
        HashMap<String, String> deptMap = new HashMap<>();
        for (SysDept dept : sysDepts) {
            deptMap.put(dept.getDeptId().toString(),dept.getDeptName());
        }
        HashMap<String, String> workMap = new HashMap<>();
        for (SysDictData sysDictData : work_type) {
            workMap.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> invoiceMap = new HashMap<>();
        for (SysDictData sysDictData : invoice_type) {
            invoiceMap.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> workStatusMap = new HashMap<>();
        for (SysDictData sysDictData : sys_work_status) {
            workStatusMap.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
        HashMap<String, String> invoiceStatusMap = new HashMap<>();
        for (SysDictData sysDictData : sys_invoice_status) {
            invoiceStatusMap.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }

        for (RdTwinMaster rdTwinMaster : rdTwinMasters) {
            Map<String, Object> params = rdTwinMaster.getParams();
            params.put("invoiceTypeName", invoiceMap.get(rdTwinMaster.getInvoiceType()));
            params.put("workTypeName", workMap.get(rdTwinMaster.getWorkType()));
            params.put("invoiceStatusName", invoiceStatusMap.get(rdTwinMaster.getInvoiceStatus()));
            params.put("workStatusName", workStatusMap.get(rdTwinMaster.getWorkStatus()));
            params.put("deptName", deptMap.get(rdTwinMaster.getWorkDept()));
        }

        return rdTwinMasters;
    }

    /**
     * 查询孪生物料清单
     *
     * @param RdTwinMaster
     * @return Detail<RdTwinMaster>
     */
    @Override
    public List<Map<String,String>> selectRdTwinMasterDetail(RdTwinMaster rdTwinMaster)
    {
        List<Map<String,String>> list= rdTwinMasterMapper.selectRdTwinMasterDetail(rdTwinMaster);
        List<Map<String, String>> data= sysDictTypeService.transInvoiceWorkType(list);
        return data.stream().map(MapUtil::toCamelCaseMap).collect(Collectors.toList());
    }

    /**
     * 新增孪生物料
     * 
     * @param RdTwinMaster
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertRdTwinMaster(RdTwinMaster rdTwinMaster)
    {
        rdTwinMaster.setCreateBy(SecurityUtils.getUsername());
        String code= sysCoderService.generate(RdConstant.TWIN, rdTwinMaster.getWorkType());
        rdTwinMaster.setRdCode(code);
        int rows = rdTwinMasterMapper.insertRdTwinMaster(rdTwinMaster);
        insertRdTwinSalve(rdTwinMaster);
        return rows;
    }

    // 增加子表
    private void insertRdTwinSalve(RdTwinMaster rdTwinMaster)
    {
        List<RdTwinSalve> rdTwinSalveList = rdTwinMaster.getRdTwinSalveList();
        String uniqueId = rdTwinMaster.getRdCode();
        if (StringUtils.isNotNull(rdTwinSalveList))
        {
            List<RdTwinSalve> list = new ArrayList<>();
            for (RdTwinSalve rdTwinSalve : rdTwinSalveList)
            {
                rdTwinSalve.setCreateBy(rdTwinMaster.getCreateBy());
                rdTwinSalve.setRdCode(uniqueId);
                list.add(rdTwinSalve);
            }
            if (list.size() > 0)
            {
                rdTwinMasterMapper.insertRdTwinSalve(list);
            }
        }
    }

    /**
     * 修改孪生物料
     * 
     * @param RdTwinMaster
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateRdTwinMaster(RdTwinMaster rdTwinMaster)
    {
        RdTwinMaster rdTwinMasterCheck = rdTwinMasterMapper.selectRdTwinMasterByCodeForUpdate(rdTwinMaster.getRdCode());
        if(rdTwinMasterCheck.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("审核单据，无法更新");
        }

        //  当前提交子表数据
        List<RdTwinSalve> rdTwinSalveList = rdTwinMaster.getRdTwinSalveList();

        // 当前数据库子表id
        ArrayList<Long> oldId = rdTwinMasterMapper.selectRdTwinSalveUniqueIdsByCode(rdTwinMaster.getRdCode());

        // 当前提交子表id
        ArrayList<Long> newId = new ArrayList<>();

        // 新增子表记录数据
        ArrayList<RdTwinSalve> newData = new ArrayList<>();
        for (RdTwinSalve rdTwinSalve : rdTwinSalveList) {
            if(rdTwinSalve.getUniqueId() == null){
                rdTwinSalve.setCreateBy(SecurityUtils.getUsername());
                rdTwinSalve.setRdCode(rdTwinMaster.getRdCode());
                newData.add(rdTwinSalve);
            }
            else{
                newId.add(rdTwinSalve.getUniqueId());
            }
        }

        rdTwinMaster.setUpdateBy(SecurityUtils.getUsername());

        // 获得删除id集合
        List<Long> delId = oldId.stream().filter(t -> !newId.contains(t)).collect(Collectors.toList());
        Long[] longs = delId.toArray(new Long[delId.size()]);
        if(longs.length != 0){
            rdTwinMasterMapper.deleteRdTwinSalveByUniqueIds(longs);
        }

        //获得修改id集合
        oldId.retainAll(newId);
        ArrayList<RdTwinSalve> editData = new ArrayList<>();
        for (Long aLong : oldId) {
            for (RdTwinSalve rdTwinSalve : rdTwinSalveList) {
                if(rdTwinSalve.getUniqueId().equals(aLong)){
                    rdTwinSalve.setUpdateBy(rdTwinMaster.getUpdateBy());
                    editData.add(rdTwinSalve);
                    break;
                }
            }
        }

        // 批量新增子表记录
        if(newData.size() != 0){
            rdTwinMasterMapper.insertRdTwinSalve(newData);
        }

        // 批量修改子表记录
        int j= 1;
        if(editData.size() != 0){
            j = rdTwinMasterMapper.updateRdTwinSalve(editData);
        }

        // 更新主表记录
        int i = rdTwinMasterMapper.updateRdTwinMaster(rdTwinMaster);
        if(i == 0 || j == 0){
            throw  new CommonException("修改失败");
        }

        return i;
    }

    /**
     * 删除孪生物料
     * 
     * @param List<RdTwinMaster>
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteRdTwinMasterByCodes(List<RdTwinMaster> list)
    {
        List<String> collect = list.stream().map(RdTwinMaster::getRdCode).collect(Collectors.toList());
        ArrayList<String> strings = rdTwinMasterMapper.selectInvoiceStatusByCode(collect);
        if(strings.contains(UserConstants.NOT_UNIQUE)){
            throw  new CommonException("审核单据无法删除");
        }
        
        rdTwinMasterMapper.deleteRdTwinSalveByRdCodes(collect);
        return rdTwinMasterMapper.deleteRdTwinMasterByRdCodes(list);
    }

    /**
     * 下一条孪生物料
     *
     * @param RdTwinMaster
     * @return RdTwinMaster
     */
    @Override
    public RdTwinMaster getNext(RdTwinMaster rdTwinMaster) {
        Long uniqueId = rdTwinMaster.getUniqueId();
        Boolean type = rdTwinMaster.getType();
        RdTwinMaster rdTwinMaster1 = new RdTwinMaster();
        rdTwinMaster1.setParams(rdTwinMaster.getParams());
        rdTwinMaster1.setWorkType(rdTwinMaster.getWorkType());
        ArrayList<Long> ids =   rdTwinMasterMapper.selectRdTwinMasterUniqueIds(rdTwinMaster1);
        for (int i = 0; i < ids.size(); i++) {
            Long aLong = ids.get(i);
            if (aLong.equals(uniqueId) && ids.get(ids.size()-1).equals(uniqueId) && type) {
                throw  new CommonException("已经是最后的单据");
            }else if (aLong.equals(uniqueId) && ids.get(0).equals(uniqueId) && !type){
                throw  new CommonException("已经是最后的单据");
            }else if(aLong.equals(uniqueId)){
                return  type?  rdTwinMasterMapper.selectRdTwinMasterById(ids.get(i+1)) :  rdTwinMasterMapper.selectRdTwinMasterById(ids.get(i-1));
            }
        }

        return  null;
    }

    /**
     * 审核孪生物料
     *
     * @param List<RdTwinMaster>
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RdTwinMaster batchCheck(List<RdTwinMaster> list) {
        String lastCode = null;
        checkInvoiceStatus(list);
        for (RdTwinMaster s : list) {
            RdTwinMaster rdTwinMaster = rdTwinMasterMapper.selectRdTwinMasterByCodeForUpdate(s.getRdCode());
            //List<RdTwinSalve> rdTwinSalveList = rdTwinMasterMapper.selectRdTwinSalveByCode(s.getRdCode());
            //if(rdTwinSalveList.size() !=0){
                // lastCode = rdTwinSalveList.get(0).getWoCode();
            //}

            /*
            for (RdTwinSalve rdTwinSalve : rdTwinSalveList) {
                if(StringUtils.isNotNull(rdTwinSalve.getWoUniqueId()) && !rdTwinSalve.getWoUniqueId().equals(0L)){
                    ${prvClassName} ${prvclassName} =  ${prvclassName}Mapper.select${prvClassName}ByCodeForUpdate(rdTwinSalve.getWoCode());
                    if(!${prvclassName}.getInvoiceStatus().equals(UserConstants.NOT_UNIQUE)) {
                        throw  new CommonException("审核失败，上游单据非审核状态");
                    }

                    ${prvsClassName} ${prvsclassName} =  ${prvclassName}Mapper.select${prvsClassName}ById(rdTwinSalve.getWoUniqueId());
                    BigDecimal quantity = ${prvsclassName}.getQuantity();
                    BigDecimal wiQuantity = ${prvsclassName}.getWiQuantity();
                    BigDecimal amount = rdTwinSalve.getQuantity();
                    if (quantity.compareTo(wiQuantity.add(amount)) < 0) {
                        throw  new CommonException(rdTwinSalve.getInvName() + "余量不足");
                    }

                    // 上游单据数量修改
                    ${prvclassName}Mapper.changeQuantity(rdTwinSalve.getWoUniqueId(), amount);

                    // 上游单据状态修改
                    if(!lastCode.equals(rdTwinSalve.getWoCode())){
                        BigDecimal bigDecimal = ${prvclassName}Mapper.selectSumQuantityByCode(lastCode);
                        if(bigDecimal.compareTo(BigDecimal.ZERO) == 0){
                            ${prvclassName}Mapper.updateWorkStatus(lastCode, WorkStatus.SYSTEM_CLOSING.getValue());
                        }
                    }

                    lastCode = rdTwinSalve.getWoCode();
                }
            }
            */

            Integer integer = rdTwinMasterMapper.updateCheck(s, UserConstants.NOT_UNIQUE, SecurityUtils.getUsername());
            if(integer == 0){
                throw  new CommonException("审核失败");
            }

            // 最后一条上游单据状态修改
            /*
            if(rdTwinSalveList.size() !=0){
                if(!rdTwinSalveList.get(rdTwinSalveList.size()-1).getWoUniqueId().equals(0L)){
                    lastCode= rdTwinSalveList.get(rdTwinSalveList.size()-1).getWoCode();
                    BigDecimal bigDecimal = ${prvclassName}Mapper.selectSumQuantityByCode(lastCode);
                    if(bigDecimal.compareTo(BigDecimal.ZERO) == 0){
                        ${prvclassName}Mapper.updateWorkStatus(lastCode, WorkStatus.SYSTEM_CLOSING.getValue());
                    }
                }
            }
            */
        }

        int length = list.size();
        if(length == 1 ){
            return rdTwinMasterMapper.selectRdTwinMasterByCode(list.get(0).getRdCode());
        }
        return null;
    }

    private void checkInvoiceStatus(List<RdTwinMaster> list) {
        List<String> collect = list.stream().map(RdTwinMaster::getRdCode).collect(Collectors.toList());
        String[] strings = collect.toArray(new String[collect.size()]);

        ArrayList<String> status =  rdTwinMasterMapper.queryInvoiceStatusForUpdate(strings);
        if(status.contains(UserConstants.NOT_UNIQUE) || status.contains(UserConstants.YELLOW)){
            throw  new CommonException("单据需为开立状态");
        }
    }

    /**
     * 反审核孪生物料
     *
     * @param List<RdTwinMaster>
     * @return 结果
     */
    @Override
    public RdTwinMaster batchAntiCheck(List<RdTwinMaster> list) {
        antiCheckInvoiceStatus(list);
        String lastCode = null;
        for (RdTwinMaster s : list) {
            RdTwinMaster rdTwinMaster = rdTwinMasterMapper.selectRdTwinMasterByCodeForUpdate(s.getRdCode());
            //List<RdTwinSalve> rdTwinSalveList = rdTwinMasterMapper.selectRdTwinSalveByCode(s.getRdCode());
            //if(rdTwinSalveList.size() != 0){
                // lastCode = rdTwinSalveList.get(0).getWoCode();
            //}
            /*
            for (RdTwinSalve rdTwinSalve : rdTwinSalveList) {
                if(rdTwinSalve.getWoUniqueId() != 0){
                    ${prvClassName} ${prvclassName} = ${prvclassName}Mapper.select${prvClassName}ByCodeForUpdate(rdTwinSalve.getWoCode());
                    if(!${prvclassName}.getInvoiceStatus().equals(UserConstants.NOT_UNIQUE)){
                        throw  new CommonException("反审核失败，上游单据非审核状态");
                    }

                    ${prvsClassName} ${prvsclassName} =  ${prvclassName}Mapper.select${prvsClassName}ById(rdTwinSalve.getWoUniqueId());
                    BigDecimal wiQuantity = rdTwinSalve.getWiQuantity();
                    BigDecimal wiQuantityR = rdTwinSalve.getWiQuantityR();
                    if(wiQuantity.compareTo(BigDecimal.ZERO)>0  || wiQuantityR.compareTo(BigDecimal.ZERO) > 0){
                        throw  new CommonException(rdTwinMaster.getRdCode()+"反审核失败，下游已引用");
                    }

                    BigDecimal woQuantity = ${prvsclassName}.getWiQuantity();
                    BigDecimal amount = rdTwinSalve.getQuantity();
                    if (woQuantity.compareTo(amount) < 0) {
                        throw new CommonException(${prvsclassName}.getInvName() + "反审核失败，上游余量不足");
                    }

                    // 上游单据数量修改
                    ${prvclassName}Mapper.changeQuantity(rdTwinSalve.getWoUniqueId(), amount.negate());

                    // 上游单据状态修改
                    if(!lastCode.equals(rdTwinSalve.getWoCode())){
                        ${prvclassName}Mapper.updateWorkStatus(lastCode, WorkStatus.NORMAl.getValue());
                    }

                    lastCode = rdTwinSalve.getWoCode();
                }
            }
            */

            Integer integer = rdTwinMasterMapper.updateCheck(s, UserConstants.YELLOW, null);
            if(integer == 0){
                throw  new CommonException("反审核失败");
            }

            // 上游单据状态修改
            /*
            ${prvclassName}Mapper.updateWorkStatus(lastCode, WorkStatus.NORMAl.getValue());
            */
        }

        int length = list.size();
        if(length == 1 ){
            return rdTwinMasterMapper.selectRdTwinMasterByCode(list.get(0).getRdCode());
        }
        return null;
    }

    private void antiCheckInvoiceStatus(List<RdTwinMaster> list) {
        List<String> collect = list.stream().map(RdTwinMaster::getRdCode).collect(Collectors.toList());
        String[] strings = collect.toArray(new String[collect.size()]);

        ArrayList<String> status =  rdTwinMasterMapper.queryInvoiceStatusForUpdate(strings);
        if(status.contains(UserConstants.UNIQUE) || status.contains(UserConstants.YELLOW)){
            throw  new CommonException("单据需为审核状态");
        }
    }

    /**
     * 挂起孪生物料
     *
     * @param List<RdTwinMaster>
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeWorkStatus(RdTwinMaster rdTwinMaster) {
        String RdCode = rdTwinMaster.getRdCode();
        String workStatus = rdTwinMaster.getWorkStatus();
        RdTwinMaster rdTwinMaster1 = rdTwinMasterMapper.selectRdTwinMasterByCodeForUpdate(RdCode);
        if(rdTwinMaster1.getWorkStatus().equals(WorkStatus.SYSTEM_CLOSING.getValue())){
            throw  new CommonException("系统关闭状态,无法操作");
        }

        Integer i;
        if(rdTwinMaster1.getWorkStatus().equals(workStatus)){
            i = rdTwinMasterMapper.updateWorkStatusOnVersion(WorkStatus.NORMAl.getValue(), rdTwinMaster);
        }else{
            i = rdTwinMasterMapper.updateWorkStatusOnVersion(workStatus, rdTwinMaster);
        }

        if(i == 0 )throw  new CommonException("操作失败");
    }

    /**
     * 链路孪生物料
     *
     * @param List<RdTwinMaster>
     * @return 结果
     */
    @Override
    public  List<Map<String,String>> chainDetail(RdTwinSalve rdTwinSalve) {
        List<Map<String,String>> list =  rdTwinMasterMapper.selectRdTwinSalveWoById(rdTwinSalve.getUniqueId());
        List<Map<String,String>> detail =  null; // ${prvclassName}.linkDetail(rdTwinSalve.getUniqueId());

        //list.addAll(detail);  后期接入
        return sysDictTypeService.transInvoiceWorkType(list);
    }

    /**
     * 链路孪生物料
     *
     * @param Long
     * @return 结果
     */
    @Override
    public  List<Map<String,String>> linkDetail(Long uniqueId){
        List<Map<String, String>> maps = rdTwinMasterMapper.linkDetail(uniqueId);
        for (Map<String, String> map : maps) {
            map.put("type",RdConstant.TWIN);
            map.put("invoice",RdConstant.INVOICE);
        }

        return sysDictTypeService.transInvoiceWorkType(maps);
    }

    /**
     * 供下游调用引入孪生物料
     *
     * @param Long
     * @return 结果
     */
    @Override
    public List<Map<String, String>> lead(Map<String, String> list) {
        List<Map<String, String>> maps = rdTwinMasterMapper.lead(list);
        HashMap<String, String> transform = sysDictTypeService.transform(RdConstant.TWIN);
        for (Map<String, String> stringStringMap : maps) {
            stringStringMap.put("type", RdConstant.TWIN);
            stringStringMap.put("invoice",RdConstant.INVOICE);;
        }

        List<Map<String, String>> data= sysDictTypeService.transInvoiceWorkType(maps);
        return data.stream().map(MapUtil::toCamelCaseMap).collect(Collectors.toList());
    }

    /**
     * 供下游调用引入孪生物料
     *
     * @param Long
     * @return 结果
     */
    @Override
    public List<Map<String, String>> leadInto(Map<String, String> list) {
        List<Map<String, String>> maps=  rdTwinMasterMapper.leadInto(list);
        for (Map<String, String> stringStringMap : maps) {
            stringStringMap.put("work_type", RdConstant.TWIN);
            stringStringMap.put("invoice_type",RdConstant.INVOICE);
        }

        List<Map<String, String>> data= sysDictTypeService.transInvoiceWorkType(maps);
        return data.stream().map(MapUtil::toCamelCaseMap).collect(Collectors.toList());
    }
}
