package com.njrsun.modules.rd.service.impl;

import com.njrsun.common.constant.Constants;
import com.njrsun.common.core.domain.entity.SysDept;
import com.njrsun.common.core.domain.entity.SysDictData;
import com.njrsun.common.dto.InvItemDTO;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.inv.mapper.InvItemsMapper;
import com.njrsun.modules.rd.constant.RdConstant;
import com.njrsun.modules.rd.domain.RdEbomExport;
import com.njrsun.modules.rd.domain.RdEbomMaster;
import com.njrsun.modules.rd.domain.RdEbomSalve;
import com.njrsun.modules.rd.domain.RdTree;
import com.njrsun.modules.rd.enums.WorkStatus;
import com.njrsun.modules.rd.mapper.RdEbomMasterMapper;
import com.njrsun.modules.rd.service.IRdEbomMasterService;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import com.njrsun.system.service.impl.SysDeptServiceImpl;
import com.njrsun.system.service.impl.SysDictTypeServiceImpl;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 配方Service业务层处理
 * 
 * @author njrsun
 * @date 2021-11-23
 */
@Service
public class RdEbomMasterServiceImpl implements IRdEbomMasterService
{
    @Autowired
    private RdEbomMasterMapper rdEbomMasterMapper;
    @Autowired
    private SysCoderServiceImpl sysCoderService;
    @Autowired
    private SysDeptServiceImpl sysDeptService;
    @Autowired
    private SysDictTypeServiceImpl sysDictTypeService;
    @Autowired
    private InvItemsMapper invItemsMapper;

    /**
     * 查询配方
     * @param uniqueId 配方ID
     * @return 配方
     */
    @Override
    public RdEbomMaster selectRdRecipeMasterById(RdEbomMaster rdEbomMaster)
    {
        RdEbomMaster rdEbomMaster1 = rdEbomMasterMapper.selectRdRecipeMasterById(rdEbomMaster);

        List<RdEbomSalve> rdRecipeSalveList = rdEbomMaster1.getRdRecipeSalveList();
         List<RdEbomSalve> list =  tree(rdRecipeSalveList);
        rdEbomMaster1.setRdRecipeSalveList(list);
        return rdEbomMaster1;
    }

    private List<RdEbomSalve> tree(List<RdEbomSalve> rdRecipeSalveList) {
        List<RdEbomSalve> returnList = new ArrayList<>();
        ArrayList<Long> tempList = new ArrayList<>();
        for (RdEbomSalve rdRecipeSalve : rdRecipeSalveList) {
            tempList.add(rdRecipeSalve.getUniqueId());
        }
        for (RdEbomSalve rdRecipeSalve : rdRecipeSalveList) {
            if(!tempList.contains(rdRecipeSalve.getParentId())){
                recursionFn(rdRecipeSalveList,rdRecipeSalve);
                returnList.add(rdRecipeSalve);
            }
        }
        return returnList;
    }

    private void recursionFn(List<RdEbomSalve> rdRecipeSalveList, RdEbomSalve rdRecipeSalve) {
        List<RdEbomSalve> childList=  getChildList(rdRecipeSalveList,rdRecipeSalve);
        rdRecipeSalve.setChildren((ArrayList<RdEbomSalve>) childList);
        for (RdEbomSalve sort : childList) {
            if(hasChild(rdRecipeSalveList,sort)){
                recursionFn(rdRecipeSalveList,sort);
            }
        }

    }

    private boolean hasChild(List<RdEbomSalve> rdRecipeSalveList, RdEbomSalve sort) {
        return  getChildList(rdRecipeSalveList,sort).size() > 0 ;

    }

    private List<RdEbomSalve> getChildList(List<RdEbomSalve> rdRecipeSalveList, RdEbomSalve rdRecipeSalve) {
        List<RdEbomSalve> list = new ArrayList<>();
        for (RdEbomSalve recipeSalve : rdRecipeSalveList) {
            Long parentId = recipeSalve.getParentId();
            Long uniqueId = rdRecipeSalve.getUniqueId();
            if(StringUtils.isNotNull(parentId) &&  (parentId.equals(uniqueId))){
                list.add(recipeSalve);
            }
        }
        return list;

    }

    /**
     * 查询配方列表
     * 
     * @param rdEbomMaster 配方
     * @return 配方
     */
    @Override
    public List<RdEbomMaster> selectRdRecipeMasterList(RdEbomMaster rdEbomMaster)
    {
        List<RdEbomMaster> rdEbomMasters = rdEbomMasterMapper.selectRdRecipeMasterList(rdEbomMaster);
        List<SysDept> sysDepts = sysDeptService.selectDeptList(new SysDept());
        List<SysDictData> sysDictData = sysDictTypeService.selectDictDataByType(RdConstant.INVOICE);
        List<SysDictData> sysDictData1 = sysDictTypeService.selectDictDataByType(RdConstant.EBOM);
        HashMap<String, String> transform = sysDictTypeService.transform(Constants.SYS_STATUS);
        HashMap<String, String> depts = new HashMap<>();
        for (SysDept sysDept : sysDepts) {
            depts.put(sysDept.getDeptId().toString(),sysDept.getDeptName());
        }
        HashMap<String, String> map1 = new HashMap<>();
        for (SysDictData sysDept : sysDictData) {
            map1.put(sysDept.getDictValue(),sysDept.getDictLabel());
        }
        HashMap<String, String> map2 = new HashMap<>();
        for (SysDictData sysDept : sysDictData1) {
            map2.put(sysDept.getDictValue(),sysDept.getDictLabel());
        }
        for (RdEbomMaster rdEbomMaster1 : rdEbomMasters) {
            Map<String, Object> params = rdEbomMaster1.getParams();
            params.put(RdConstant.INVOICE,map1.get(rdEbomMaster1.getInvoiceType()));
            params.put(RdConstant.EBOM,map2.get(rdEbomMaster1.getWorkType()));
            params.put("deptName",depts.get(rdEbomMaster1.getWorkDept()));
            params.put("sys_work_status",transform.get(rdEbomMaster1.getWorkStatus()));
        }
        return rdEbomMasters;
    }

    /**
     * 新增配方
     * 
     * @param rdEbomMaster 配方
     * @return 结果
     */
    @Transactional
    @Override
    public int insertRdRecipeMaster(RdEbomMaster rdEbomMaster)
    {
        if(rdEbomMaster.getWorkType().equals("0")){
            checkVersion(rdEbomMaster);
        }
        rdEbomMaster.setRdCode(sysCoderService.generate(RdConstant.EBOM, rdEbomMaster.getWorkType()));
        rdEbomMaster.setCreateBy(SecurityUtils.getUsername());
        int i = rdEbomMasterMapper.insertRdRecipeMaster(rdEbomMaster);
        if(i == 0){
            throw  new CommonException("新增失败");
        }
        List<RdEbomSalve> rdRecipeSalveList = rdEbomMaster.getRdRecipeSalveList();
        for (RdEbomSalve rdRecipeSalve : rdRecipeSalveList) {
            rdRecipeSalve.setAncestors("0");
            rdRecipeSalve.setParentId(0L);
            rdRecipeSalve.setCreateBy(SecurityUtils.getUsername());
            rdRecipeSalve.setRdCode(rdEbomMaster.getRdCode());
            rdEbomMasterMapper.insertRdRecipeSalve(rdRecipeSalve);
            List<RdEbomSalve> child = rdRecipeSalve.getChildren();

            if(child.size() > 0){
                updateChild(rdRecipeSalve,(ArrayList<RdEbomSalve>)child);
            }
        }
        return i ;
    }

    private void checkVersion(RdEbomMaster rdEbomMaster) {
       RdEbomMaster ebomMaster =  rdEbomMasterMapper.checkVersion(rdEbomMaster);
            if(StringUtils.isNotNull(ebomMaster)){
                throw  new CommonException("校验不通过，具有相同的版本号");
            }
    }

    /**
     * 修改配方
     * 
     * @param rdEbomMaster 配方
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateRdRecipeMaster(RdEbomMaster rdEbomMaster)
    {
        if(rdEbomMaster.getWorkType().equals("0")){
            checkVersion(rdEbomMaster);
        }
        RdEbomMaster rdEbomMaster1 = rdEbomMasterMapper.selectRdRecipeMasterForUpdate(rdEbomMaster.getRdCode());
        if(rdEbomMaster1.getInvoiceStatus().equals(Constants.CHECK)){
            throw  new CommonException("审核单据,无法删除");
        }
        /** 老Id */
        ArrayList<Long> oldId = rdEbomMasterMapper.selectRdRecipeSalveIdByCode(rdEbomMaster.getRdCode());
        /**  新数据  */
        List<RdEbomSalve> rdRecipeSalveList = rdEbomMaster.getRdRecipeSalveList();
        /** 新ID */
        ArrayList<Long> newId = new ArrayList<>();
        ArrayList<RdEbomSalve> editData = new ArrayList<>();
        for (RdEbomSalve rdRecipeSalve : rdRecipeSalveList) {
            if(rdRecipeSalve.getUniqueId() == null){
                //新增数据
                rdRecipeSalve.setCreateBy(SecurityUtils.getUsername());
                rdRecipeSalve.setRdCode(rdEbomMaster.getRdCode());
                rdRecipeSalve.setAncestors("0");
                rdRecipeSalve.setParentId(0L);
                rdEbomMasterMapper.insertRdRecipeSalve(rdRecipeSalve);
            }
            else{
                newId.add(rdRecipeSalve.getUniqueId());
                ArrayList<RdEbomSalve> rdRecipeSalves = new ArrayList<>();
                rdRecipeSalves.add(rdRecipeSalve);
                rdEbomMasterMapper.batchUpdateRdSalve(rdRecipeSalves);
            }
            ArrayList<RdEbomSalve> children = rdRecipeSalve.getChildren();
            if(children.size() > 0 ){
                updateChild(rdRecipeSalve,children);
            }else{
                rdEbomMasterMapper.deleteRdRecipeSalveByParentId(rdRecipeSalve.getUniqueId());
            }
        }
        rdEbomMaster.setUpdateBy(SecurityUtils.getUsername());
        // 删除ID
        List<Long> delId = oldId.stream().filter(t -> !newId.contains(t)).collect(Collectors.toList());
        if(delId.size() > 0){
            rdEbomMasterMapper.deleteRootRdceipe(delId);
        }

        int i = rdEbomMasterMapper.updateRdRecipeMaster(rdEbomMaster);
        if(i == 0){
            throw  new CommonException("修改失败");
        }
        return i;
    }

    private void updateChild(RdEbomSalve rdRecipeSalve, ArrayList<RdEbomSalve> child) {
        ArrayList<RdEbomSalve> edit  = new ArrayList<>();
        ArrayList<Long> longs = rdEbomMasterMapper.selectRdRecipeSalveByParentId(rdRecipeSalve.getUniqueId());
        List<Long> collect = child.stream().map(RdEbomSalve::getUniqueId).collect(Collectors.toList());
        List<Long> delId = longs.stream().filter(t -> !collect.contains(t)).collect(Collectors.toList());
        longs.retainAll(collect);

        if(delId.size() > 0){
            rdEbomMasterMapper.deleterdRecipeSalveIds(delId);  // 删除旧数据
        }
        for (RdEbomSalve rdRecipeSalve1 : child) {
            if(rdRecipeSalve1.getUniqueId()== null){
                rdRecipeSalve1.setRdCode(rdRecipeSalve.getRdCode());
                rdRecipeSalve1.setParentId(rdRecipeSalve.getUniqueId());
                rdRecipeSalve1.setAncestors(rdRecipeSalve.getAncestors()+","+rdRecipeSalve.getUniqueId());
                rdEbomMasterMapper.insertRdRecipeSalve(rdRecipeSalve1);
            }
            else if(longs.contains(rdRecipeSalve1.getUniqueId())){
                edit.add(rdRecipeSalve1);
            }
            if(rdRecipeSalve1.getChildren().size() > 0){
                updateChild(rdRecipeSalve1,rdRecipeSalve1.getChildren());
            } else{
                rdEbomMasterMapper.deleteRdRecipeSalveByParentId(rdRecipeSalve1.getUniqueId());
            }
        }
        if(edit.size() > 0){
            rdEbomMasterMapper.batchUpdateRdSalve(edit);
        }
    }

    /**
     * 批量删除配方
     * 
     * @param list 需要删除的配方ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteRdRecipeMasterByIds(List<RdEbomMaster> list)
    {
        List<RdEbomMaster> rdEbomMasters = rdEbomMasterMapper.selectRdStatusByCodes(list);
        for (RdEbomMaster rdEbomMaster : rdEbomMasters) {
            if(rdEbomMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException("审核状态下，无法删除单据");
            }
        }
        rdEbomMasterMapper.deleteRdRecipeSalveByRdCodes(list);
        return rdEbomMasterMapper.deleteRdRecipeMasterByIds(list);
    }

    /**
     * 删除配方信息
     * 
     * @param uniqueId 配方ID
     * @return 结果
     */
    @Override
    public int deleteRdRecipeMasterById(Long uniqueId)
    {
        rdEbomMasterMapper.deleteRdRecipeSalveByRdCode(uniqueId);
        return rdEbomMasterMapper.deleteRdRecipeMasterById(uniqueId);
    }

    @Override
    public RdEbomMaster getNextOrLast(RdEbomMaster rdEbomMaster) {
        String rdCode = rdEbomMaster.getRdCode();
        Boolean type = rdEbomMaster.getType();
        List<String> codes = rdEbomMasterMapper.selectRdCode(rdEbomMaster);
        int i  = 0 ;
        for (int i1 = 0; i1 < codes.size(); i1++) {
            if(codes.get(i1).equals(rdCode)){
                i = i1;
            }
        }
        if( (i == 0 && type) || ((i ==codes.size()-1) && !type)){
            throw  new CommonException("已经是最后一条单据了");
        }else if(type){
            RdEbomMaster rdEbomMaster1 = new RdEbomMaster();
            rdEbomMaster1.setRdCode(codes.get(i-1));
            return   selectRdRecipeMasterById(rdEbomMaster1);
        }
        else{
            RdEbomMaster rdEbomMaster1 = new RdEbomMaster();
            rdEbomMaster1.setRdCode(codes.get(i+1));
            return selectRdRecipeMasterById(rdEbomMaster1);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RdEbomMaster batchCheck(List<RdEbomMaster> list) {
        List<RdEbomMaster> rdEbomMasters = rdEbomMasterMapper.selectRdStatusByCodes(list);
        for (RdEbomMaster rdEbomMaster : rdEbomMasters) {
            if(!rdEbomMaster.getInvoiceStatus().equals(Constants.OPEN)){
                throw  new CommonException(rdEbomMaster.getRdCode() + ": 单据 非开立状态");
            }
        }
        for (RdEbomMaster rdEbomMaster : list) {
            check(rdEbomMaster);
        }
        if(list.size() == 1){
            return selectRdRecipeMasterById(list.get(0));
        }
        return null;
    }


    private void check(RdEbomMaster rdEbomMaster) {
        RdEbomMaster rdEbomMaster1 = rdEbomMasterMapper.selectRdRecipeMasterForUpdate(rdEbomMaster.getRdCode());
        Integer integer = rdEbomMasterMapper.changeStatus(Constants.CHECK, SecurityUtils.getUsername(), rdEbomMaster1);
        if(integer ==0){
            throw  new CommonException("审核失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RdEbomMaster batchAntiCheck(List<RdEbomMaster> list) {
        List<RdEbomMaster> rdEbomMasters = rdEbomMasterMapper.selectRdStatusByCodes(list);
        for (RdEbomMaster rdEbomMaster : rdEbomMasters) {
            if(!rdEbomMaster.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException(rdEbomMaster.getRdCode() + ": 单据 非审核状态");
            }
        }
        for (RdEbomMaster rdEbomMaster : list) {
            antiCheck(rdEbomMaster);
        }
        if(list.size() == 1){
            return selectRdRecipeMasterById(list.get(0));
        }
        return null;
    }

    @Override
    public void changeWorkStatus(RdEbomMaster rdEbomMaster) {
        String rdCode = rdEbomMaster.getRdCode();
        String workStatus = rdEbomMaster.getWorkStatus();
        RdEbomMaster rdEbomMaster1 = rdEbomMasterMapper.selectRdRecipeMasterForUpdate(rdCode);
        if(rdEbomMaster1.getWorkStatus().equals(WorkStatus.SYSTEM_CLOSING.getValue())){
            throw  new CommonException("系统关闭状态,无法操作");
        }
        Integer i;
        if(rdEbomMaster1.getWorkStatus().equals(workStatus)){
            i = rdEbomMasterMapper.updateWorkStatus(WorkStatus.NORMAl.getValue(), rdEbomMaster);
        }else{
            i = rdEbomMasterMapper.updateWorkStatus(workStatus, rdEbomMaster);
        }
        if(i == 0 ){
            throw  new CommonException("操作失败");
        }
    }

    @Override
    public List<RdEbomExport> detail(RdEbomExport rdRecipeExport) {
        return rdEbomMasterMapper.detail(rdRecipeExport);
    }

    @Override
    public void check(String rdCode,ArrayList<String> codeList) {
        if(!codeList.contains(rdCode)){
            codeList.add(rdCode);
        }else{
            throw  new CommonException(rdCode + ":  单据明细循环引用.....");
        }
        List<RdEbomSalve>  map =  rdEbomMasterMapper.selectRdSalveByCode(rdCode);
        for (RdEbomSalve salve : map) {
            String code = salve.getInvCode();
            Map<String,String>  result =  rdEbomMasterMapper.selectRdByInvCode(code);
                if(StringUtils.isNotEmpty(result)){
                    check(result.get("rd_code"),codeList);
                codeList.clear();
            }
        }
    }


    private void antiCheck(RdEbomMaster rdEbomMaster) {
        RdEbomMaster rdEbomMaster1 = rdEbomMasterMapper.selectRdRecipeMasterForUpdate(rdEbomMaster.getRdCode());
        Integer integer = rdEbomMasterMapper.changeStatus(Constants.RETURN, null, rdEbomMaster1);
        if(integer ==0){
            throw  new CommonException("审核失败");
        }
    }



    @Transactional(rollbackFor = Exception.class)
    public  void  importExcelEbom(String sheetName, InputStream is) throws Exception {
        //创建Excel工作薄
        Workbook work = getWorkbook(is, sheetName);
        if (null == work) {
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;
            sheet = work.getSheetAt(0);
            if (sheet == null) {
                throw  new CommonException("文件内容有误");
            }
            DataFormatter dataFormatter = new DataFormatter();
            int maxRow = sheet.getLastRowNum();
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = sheet.getFirstRowNum(); j <= maxRow;j++){
            if(dataFormatter.formatCellValue(sheet.getRow(j).getCell(5)).equals("子件名称")){
                j++;
                while (sheet.getRow(j) != null &&  !dataFormatter.formatCellValue(sheet.getRow(j).getCell(5)).equals("版本代号") && !dataFormatter.formatCellValue(sheet.getRow(j).getCell(5)).equals("")){
                    Cell cell10 = sheet.getRow(j).getCell(5);
                    String name = dataFormatter.formatCellValue(cell10);
                    InvItemDTO invItemDTO1 = invItemsMapper.selectInvFromRd(name);
                    if(StringUtils.isNull(invItemDTO1)){
                        stringBuilder.append("<br/>" + name + "： 物料不存在");
                    }
                    j++;
                }
            }
        }
        if(stringBuilder.length() > 0){
            throw  new CommonException(stringBuilder.toString());
        }
            for (int j = sheet.getFirstRowNum(); j <= maxRow;) {
                Row row1 = sheet.getRow(j);
                Cell cell2 = row1.getCell(1);
                String stringCellValue = dataFormatter.formatCellValue(cell2);
                if (stringCellValue.equals("母件名称")) {
                    RdEbomMaster rdEbomMaster = new RdEbomMaster();
                    j++;
                    Row row6 = sheet.getRow(j);
                    Cell cell4 = row6.getCell(1);
                    String invName = dataFormatter.formatCellValue(cell4);
                    InvItemDTO invItemDTO = invItemsMapper.selectInvDto(invName);
                    if(StringUtils.isNull(invItemDTO)){
                        throw  new CommonException("请先导入物料：" + invName);
                    }
                    BeanUtils.copyProperties(invItemDTO,rdEbomMaster);
                    rdEbomMaster.setInvSortId(invItemDTO.getSortId());
                    rdEbomMaster.setInvSortRoot(Long.valueOf(invItemDTO.getSortRoot()));
                    rdEbomMaster.setInvCode(invItemDTO.getCode());
                    rdEbomMaster.setInvName(invItemDTO.getName());
                    rdEbomMaster.setInvAttribute(invItemDTO.getAttribute());
                    rdEbomMaster.setInvoiceDate(new Date());
                    rdEbomMaster.setInvoiceStatus("1");
                    rdEbomMaster.setInvoiceType("0");
                    rdEbomMaster.setWorkType("2");
                    rdEbomMaster.setWorkStatus("0");
                    rdEbomMaster.setVerNumber("1.0");
                    rdEbomMaster.setFormConfig("010401,02010401");
                    rdEbomMaster.setBaseNumber(new BigDecimal("1"));
                    Cell cell1 = row6.getCell(2);
                    String s = dataFormatter.formatCellValue(cell1);
                    if(StringUtils.isNull(s) || s.equals("")){
                        throw  new CommonException("母件规格不能为空");
                    }
                    rdEbomMaster.setDensity(Float.parseFloat(s));
                    Cell cell11 = row6.getCell(11);
                    String color = dataFormatter.formatCellValue(cell11);
                    Cell cell = row6.getCell(12);
                    String cost = dataFormatter.formatCellValue(cell);
                    rdEbomMaster.setCost(cost);
                    rdEbomMaster.setColour(color);
                    Cell cell6 = row6.getCell(6);
                    String remark = dataFormatter.formatCellValue(cell6);
                    rdEbomMaster.setVerRemark(remark);
                    ArrayList<RdEbomSalve> rdEbomSalves = new ArrayList<>();
                    j=j+2;
                    BigDecimal sum = new BigDecimal("0");
                    String invCode = null;
                    while (sheet.getRow(j) != null &&  !dataFormatter.formatCellValue(sheet.getRow(j).getCell(5)).equals("版本代号") && !dataFormatter.formatCellValue(sheet.getRow(j).getCell(5)).equals("")){
                        RdEbomSalve rdEbomSalve = new RdEbomSalve();
                        Cell cell10 = sheet.getRow(j).getCell(5);
                        String name = dataFormatter.formatCellValue(cell10);
                        InvItemDTO invItemDTO1 = null;
                        try{
                             invItemDTO1 = invItemsMapper.selectInvFromRd(name);
                        }catch (TooManyResultsException e){
                            throw  new CommonException(name +":存在重复物料名");
                        }
                        rdEbomSalve.setInvCode(invItemDTO1.getCode());
                        rdEbomSalve.setInvSortId(invItemDTO1.getSortId());
                        rdEbomSalve.setInvSortRoot(Long.valueOf(invItemDTO1.getSortRoot()));
                        rdEbomSalve.setInvName(invItemDTO1.getName());
                        rdEbomSalve.setInvAttribute(invItemDTO1.getAttribute());
                        rdEbomSalve.setInvSupplyType("0");
                        rdEbomSalve.setInvOutType("0");
                        BeanUtils.copyProperties(invItemDTO1,rdEbomSalve);
                        Cell cell14 = sheet.getRow(j).getCell(8);
                        String s2 = dataFormatter.formatCellValue(cell14);
                        BigDecimal ra = new BigDecimal(s2);
                        rdEbomSalve.setRatio(ra);
                        sum = sum.add(ra);
                        Cell cell15 = sheet.getRow(j).getCell(30);
                        String stringCellValue1 = cell15.getStringCellValue();
                        if(stringCellValue1.equals("补足")){
                            rdEbomSalve.setFill("1");
                            invCode = invItemDTO1.getCode();
                        }else{
                            rdEbomSalve.setFill("0");
                            rdEbomSalve.setRemarks(stringCellValue1);
                        }
                        j++;
                        rdEbomSalves.add(rdEbomSalve);
                    }
                    BigDecimal subtract = sum.subtract(new BigDecimal("100"));
                    for (RdEbomSalve rdEbomSalf : rdEbomSalves) {
                        if(rdEbomSalf.getInvCode().equals(invCode)){
                            rdEbomSalf.setRatio(rdEbomSalf.getRatio().subtract(subtract));
                        }
                    }
                    rdEbomMaster.setRdRecipeSalveList(rdEbomSalves);
                    insertRdRecipeMaster(rdEbomMaster);
                }
            }
        work.close();
    }

    @Override
    public List<RdEbomMaster> lead(RdEbomMaster rdEbomMaster) {
        ArrayList<RdEbomMaster> rdEbomMasters;
        String invName = rdEbomMaster.getInvName();
        String invCode = rdEbomMaster.getInvCode();
        String rdCode= rdEbomMaster.getRdCode();
        if(invCode!=null && !invCode.equals("")){
            return selectRdRecipeMasterList(rdEbomMaster);
        }
        else if(rdCode!=null && !rdCode.equals("")){
            return selectRdRecipeMasterList(rdEbomMaster);
        }else {
            rdEbomMasters = new ArrayList<>();
            return rdEbomMasters;
        }
    }

    @Override
    public List<RdEbomExport> leadInto(RdEbomExport rdEbomSalve) {
        return detail(rdEbomSalve);
    }

    @Override
    public void exportLink(String rdCode,ArrayList<RdEbomSalve> list) {
        List<RdEbomSalve> rdEbomSalves = rdEbomMasterMapper.selectRdSalveByCode(rdCode);
        for (RdEbomSalve salve : rdEbomSalves) {
            list.add(salve);
            String code = salve.getInvCode();
            Map<String,String>  result =  rdEbomMasterMapper.selectRdByInvCode(code);
            if(StringUtils.isNotEmpty(result)){
                exportLink(result.get("rd_code"),list);
            }
        }
    }

    @Override
    public List<RdEbomMaster> associate(RdEbomMaster rdEbomMaster) {
        return rdEbomMasterMapper.associate(rdEbomMaster);
    }

    @Override
    public RdTree tree(String invCode,RdTree rdTree) throws UnsupportedEncodingException {
        HashMap<String, String> inv_supply_type = sysDictTypeService.transform("inv_supply_type");
        RdEbomMaster root =  rdEbomMasterMapper.selectRdEbomForInvCode(invCode);
        if(StringUtils.isNotNull(root)){
            InvItemDTO invItemDTO = invItemsMapper.selectInvDtoByCode(invCode);
            rdTree.setName(root.getInvCode()+"\n"+root.getInvName() + "\n"+root.getInvAttribute()+"\n"+"v"+ root.getVerNumber()+"\n"+inv_supply_type.get(invItemDTO.getSupplyType()));
            String gbk = URLEncoder.encode(root.getFormConfig(), "GBK");
            rdTree.setValue(root.getRdCode()+","+root.getWorkType()+","+gbk);
            List<RdEbomSalve> rdEbomSalves = rdEbomMasterMapper.selectRdSalveByCode(root.getRdCode());
            ArrayList<RdTree> rdTrees = new ArrayList<>();
            for (RdEbomSalve rdEbomSalf : rdEbomSalves) {
                RdTree tree = getTree(rdEbomSalf,root.getVerNumber(),root.getWorkType(),0);
                rdTrees.add(tree);
            }
            rdTree.setChildren(rdTrees);
            return rdTree;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clear(String workType) {
        if(workType.equals("")){
            rdEbomMasterMapper.clearAll();
        }else{
            List<String> code =  rdEbomMasterMapper.selectCodeByWorkType(workType);
            rdEbomMasterMapper.clearSalve(code);
            rdEbomMasterMapper.clear(workType);
        }
    }

/*    @Override
    public List<RdEbomMaster> selectRdRecipeMasterListNew(RdEbomMaster rdEbomMaster) {
        ArrayList<RdEbomMaster> rdEbomMasters;
        String invName = rdEbomMaster.getInvName();
        String invCode = rdEbomMaster.getInvCode();
        if(invName.equals("") && invCode.equals("")){
            rdEbomMasters = new ArrayList<>();
            return rdEbomMasters;
        }else{
          return   selectRdRecipeMasterList(rdEbomMaster);
        }
    }*/


    private RdTree getTree(RdEbomSalve salve,String version,String workType,Integer p) throws UnsupportedEncodingException {
        if( p > 10){
            throw  new CommonException("结构树构建失败");
        }
        ArrayList<RdTree> rdTrees = new ArrayList<>();
        RdEbomMaster root =  rdEbomMasterMapper.selectRdEbomForInvCode(salve.getInvCode());
        RdTree rdTree = new RdTree();
        HashMap<String, String> inv_supply_type = sysDictTypeService.transform("inv_supply_type");
        if(StringUtils.isNotNull(root)){
            List<RdEbomSalve> rdEbomSalves = rdEbomMasterMapper.selectRdSalveByCode(root.getRdCode());
            for (RdEbomSalve rdEbomSalf : rdEbomSalves) {
                if(salve.getFill().equals("1") && workType.equals("0")){
                    rdTree.setName(salve.getInvCode()+"\n"+salve.getInvName() + "\n"+salve.getInvAttribute()+"\nfix"+salve.getRatio().stripTrailingZeros()+"\nv"+version+"\n"+inv_supply_type.get(salve.getInvSupplyType()));
                }else if(salve.getFill().equals("1")){
                    rdTree.setName(salve.getInvCode()+"\n"+salve.getInvName() + "\n"+salve.getInvAttribute()+"\nfix"+salve.getRatio().stripTrailingZeros()+"\nv"+version+"\n"+inv_supply_type.get(salve.getInvSupplyType()));
                }else if(salve.getFill().equals("0") && workType.equals("0")){
                    rdTree.setName(salve.getInvCode()+"\n"+salve.getInvName() + "\n"+salve.getInvAttribute()+"\nx"+salve.getRatio().stripTrailingZeros()+"\nv"+root.getVerNumber()+"\n"+inv_supply_type.get(salve.getInvSupplyType()));
                }else{
                    rdTree.setName(salve.getInvCode()+"\n"+salve.getInvName() + "\n"+salve.getInvAttribute()+"\nx"+salve.getRatio().stripTrailingZeros()+"\nv"+version+"\n"+inv_supply_type.get(salve.getInvSupplyType()));
                }
                    RdTree tree = getTree(rdEbomSalf,root.getVerNumber(),root.getWorkType(),++p);
                    rdTrees.add(tree);
                    rdTree.setChildren(rdTrees);
                String formConfig = root.getFormConfig();
                String encode = URLEncoder.encode(formConfig, "GBK");
                rdTree.setValue(root.getRdCode()+","+root.getWorkType()+","+encode);
            }
        }else{
            if(salve.getFill().equals("1") && workType.equals("0")){
                rdTree.setName(salve.getInvCode()+" "+salve.getInvName() + " "+salve.getInvAttribute()+" fix"+salve.getRatio().stripTrailingZeros()+" "+inv_supply_type.get(salve.getInvSupplyType()));
            }else if(salve.getFill().equals("1")){
                rdTree.setName(salve.getInvCode()+" "+salve.getInvName() + " "+salve.getInvAttribute()+" fix"+salve.getRatio().stripTrailingZeros()+" "+inv_supply_type.get(salve.getInvSupplyType()));
            }else if(salve.getFill().equals("0") && workType.equals("0")){
                rdTree.setName(salve.getInvCode()+" "+salve.getInvName() + " "+salve.getInvAttribute()+" x"+salve.getRatio().stripTrailingZeros()+" "+inv_supply_type.get(salve.getInvSupplyType()));
            }else{
                rdTree.setName(salve.getInvCode()+" "+salve.getInvName() + " "+salve.getInvAttribute()+" x"+salve.getRatio().stripTrailingZeros()+" "+inv_supply_type.get(salve.getInvSupplyType()));
            }
            rdTree.setValue(salve.getRdCode());
            rdTrees.add(rdTree);
        }
        return rdTree;
    }


    public static Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
        Workbook workbook = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (".xls".equals(fileType)) {
            workbook = new HSSFWorkbook(inStr);
        } else if (".xlsx".equals(fileType)) {
            workbook = new XSSFWorkbook(inStr);
        } else {
            throw new Exception("请上传excel文件！");
        }
        return workbook;
    }

}
