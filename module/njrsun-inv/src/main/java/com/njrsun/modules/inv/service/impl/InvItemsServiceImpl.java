package com.njrsun.modules.inv.service.impl;


import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.*;

import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.exception.CustomException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.inv.domain.InvItem;
import com.njrsun.modules.inv.domain.InvItems;
import com.njrsun.modules.inv.domain.InvRelated;
import com.njrsun.modules.inv.domain.InvSort;
import com.njrsun.modules.inv.mapper.InvItemsMapper;
import com.njrsun.modules.inv.mapper.InvRelatedMapper;
import com.njrsun.modules.inv.mapper.InvSortMapper;
import com.njrsun.modules.inv.mapper.InvUnitMapper;
import com.njrsun.modules.inv.service.IInvItemsService;
import com.njrsun.modules.inv.service.IInvSortService;
import com.njrsun.system.service.impl.SysConfigServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 物料名称Service业务层处理
 * 
 * @author njrsun
 * @date 2021-04-06
 */
@Service
public class InvItemsServiceImpl implements IInvItemsService
{
    @Autowired
    private InvItemsMapper invItemsMapper;

    @Autowired
    private InvSortMapper invSortMapper;



    @Autowired
    private SysConfigServiceImpl sysConfigServicel;

    @Autowired
    private InvUnitMapper invUnitMapper;
    @Autowired
    private InvRelatedMapper invRelatedMapper;
    @Autowired
    private IInvSortService invSortService;




     private static final Logger log = LoggerFactory.getLogger(InvItemsServiceImpl.class);

    /**
     * 查询物料名称
     * 
     * @param uniqueId 物料名称ID
     * @return 物料名称
     */
    @Override
    public InvItems selectInvItemsById(Long uniqueId)
    {
        return invItemsMapper.selectInvItemsById(uniqueId);
    }


    /**
     * 查询物料名称列表
     *
     * @param invItems 物料名称
     * @return 物料名称
     */
    @Override
    public List<InvItems> selectInvItemsList(InvItems invItems, Boolean type, ArrayList<Long> sortCodes)
    {
        String sortRoot = invItems.getSortRoot();
        if(StringUtils.isNotEmpty(sortRoot)){
            String[] split = sortRoot.split(",");
            invItems.setSortRoots(split);
        }
        if(StringUtils.isNull(sortCodes)){

            return invItemsMapper.selectInvItemsExport(invItems);

        }
        if(type){
            return invItemsMapper.selectInvItemsList(invItems, sortCodes);
        }
        else{
            return invItemsMapper.selectInvItemsListLeft( invItems, sortCodes);
        }
    }


    public List<InvItem> selectMachInvItemsList(InvItems invItems)
    {
        String sortRoot = invItems.getSortRoot();
        if(StringUtils.isNotEmpty(sortRoot)){
            String[] split = sortRoot.split(",");
            invItems.setSortRoots(split);
        }
            return invItemsMapper.selectInvExport(invItems);
    }

    /**
     * 新增物料
     * 
     * @param invItems 物料名称
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertInvItems(InvItems invItems)
    {
        trim(invItems);
        Long sortId = invItems.getSortId();
       String ancestors =  invSortMapper.selectAncestorsBySortId(sortId);
        String[] split = ancestors.split(",");
        if(split.length > 1){
            invItems.setSortRoot(split[1]);
        }
        else{
            invItems.setSortRoot(sortId.toString());
        }
        Long sortId1 = invItems.getSortId();
        String extracted = extracted(sortId1);
        String s = sysConfigServicel.selectConfigByKey("inv.code.length");
        if(extracted.length() + invItems.getCode().length() < Integer.parseInt(s)){
            int i = Integer.parseInt(s) - invItems.getCode().length();
            s ="%0"+i+"d";
            String format = String.format(s, Integer.valueOf(extracted));
            invItems.setCode(invItems.getCode()+format);
        }else{
            invItems.setCode(invItems.getCode()+extracted);
        }

        invItems.setCreateBy(SecurityUtils.getUsername());
        return invItemsMapper.insertInvItems(invItems);
    }

    private void trim(InvItems invItems) {
        String s1 = sysConfigServicel.selectConfigByKey("inv.name.space");
        if("true".equals(s1)){
            String replace = invItems.getName().trim().replace("（", "(");
            String replace1 = replace.replace("）", ")");
            invItems.setName(replace1);
            String replace2 = invItems.getProperty().trim().replace("（", "(").replace("）", ")");
            invItems.setProperty(replace2);
        }else if ("false".equals(s1)){
            String replace = invItems.getName().replace("（", "(");
            String replace1 = replace.replace("）", ")").replace(" ","");
            invItems.setName(replace1);
            String property = invItems.getProperty();
            if(StringUtils.isNotNull(property)){
                String replace2 = invItems.getProperty().replace("（", "(").replace("）", ")").replace(" ", "");
                invItems.setProperty(replace2);
            }
        }
    }


    /**
     * 生成物料编码
     * @param sortId
     */
    public String extracted(Long sortId) {
        InvSort invSort = invSortMapper.selectInvSortListBySortId(sortId);
        NumberFormat formart = NumberFormat.getNumberInstance();
        formart.setMinimumIntegerDigits(invSort.getSerialLength().intValue());
        formart.setGroupingUsed(false);
        String format = formart.format(invSort.getSerialNumber() + 1);
        invSortMapper.addSerialNumberBySortId(sortId);
        return format;
    }

    /**
     * 修改物料名称
     * @param invItems 物料名称
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateInvItems(InvItems invItems)
    {
        invItems.setUpdateBy(SecurityUtils.getUsername());
        int i = invItemsMapper.updateInvItems(invItems);
        if(i == 0){
            throw  new CustomException("更新未成功");
        }
        return i;
    }

    /**
     * 批量删除物料名称
     * 
     * @param uniqueIds 需要删除的物料名称ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteInvItemsByIds(Long[] uniqueIds)
    {
        return invItemsMapper.deleteInvItemsByIds(uniqueIds);
    }


    /**
     * 删除物料名称信息
     * 
     * @param code 物料名称ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteInvItemsByCode(String code)
    {
        try{
            return invItemsMapper.deleteInvItemsByCode(code);
        }catch (Exception e){
            throw  new CustomException("删除失败");
        }
    }

    /**
     * 判断物料名称是否相同
     * @param invItems
     * @return
     */
    @Override
    public String hasSameName(InvItems invItems) {
        invItems.setName(invItems.getName().trim());
        long l = invItems.getUniqueId() == null ? -1 : invItems.getUniqueId();
        InvItems ex =  invItemsMapper.selectInvItemsByName(invItems.getName(),invItems.getAttribute(),invItems.getSortId());
        if(ex != null && !(ex.getUniqueId().equals(l))){
            return  invItems.getName()+ "/" +invItems.getAttribute();
        }
         else{
               return UserConstants.NOT_UNIQUE;
        }
    }

    /**
     * 导入  用户数据
     * @param invItemsList
     * @param updateSupport
     * @param fileName
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importData(List<InvItems> invItemsList, boolean updateSupport, String fileName,Long sortId) {
        if(StringUtils.isNull(invItemsList) || invItemsList.size() == 0){
            throw  new CustomException("导入数据不能为空");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String[] split = fileName.split(" # ");
        String[] split2 = split[0].split("-");
        StringBuilder stringBuilder = new StringBuilder();
        InvSort invSort1 = new InvSort();
        List<InvSort> invSorts = invSortMapper.selectInvSortList(invSort1);
        ArrayList<InvSort> invSorts1 = new ArrayList<>();
        int length = split2.length;
        for (int i = 0; i < length-1; i++) {

            for (InvSort invSort : invSorts) {
                if(invSort.getSortName().equals(split2[i])){
                    stringBuilder.append(invSort.getSortCode());
                    for (InvSort sort : invSorts) {
                        if(sort.getAncestors().contains(invSort.getSortId().toString())){
                            invSorts1.add(sort);
                        }
                    }
                    break;
                }
            }
            invSorts = new ArrayList<>(invSorts1);
            invSorts1.clear();
        }
        for (InvSort invSort : invSorts) {
            if(split2[length-1].equals(invSort.getSortName()) ){
                stringBuilder.append(invSort.getSortCode());
                invSort1 = invSort;
                break;
            }
        }
        if(!invSort1.getSortId().equals(sortId)){
            failureMsg.append("很抱歉，导入失败！ 数据分类不正确!!");
            throw  new CustomException(failureMsg.toString());
        }
        for (InvItems invItems : invItemsList) {
            try{
                // 判断 是否 存在此条数据
                InvItems inv = invItemsMapper.selectInvItemsByName(invItems.getName(),invItems.getAttribute(),invItems.getSortId());
                if(StringUtils.isNull(inv) && ("".equals(invItems.getCode()) || StringUtils.isNull(invItems.getCode()))){
                    invItems.setCode(stringBuilder.toString());
                    trim(invItems);
                    insertData(invItems,invSort1);
                    successNum++;
                }
                else if(updateSupport){
                    if (!UserConstants.NOT_UNIQUE.equals(hasSameName(invItems))){
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、数据 " + invItems.getName() + " 已存在");
                    }else{
                     Long  version =  invItemsMapper.selectVersionByCode(invItems.getCode());
                     invItems.setVersion(version);
                        updateInvItems(invItems);
                        successNum++;
                    }
                }
            }

            catch (Exception e){
                failureNum++;
                String msg = "<br/>" + failureNum + "、数据 " + invItems.getName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }

        }
        if(failureNum > 0){
            failureMsg.insert(0,"很抱歉，导入失败！ 共 " +failureNum + " 条数据格式不正确,错误如下: ");
            throw  new CustomException(failureMsg.toString());
        }
        else{
            successMsg.insert(0,"恭喜您，数据已全部导入成功！ 共" +successNum  +" 条。");
        }
        return successMsg.toString();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInvItemsAllByCodeId(Long sortId) {
        invItemsMapper.deleteInvItemsBySortId(sortId);
        invSortMapper.updateSerialNumber(sortId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String importInvRelated(List<InvItems> invItemsList, boolean updateSupport, String fileName, Long sortId) {
        if (StringUtils.isNull(invItemsList) || invItemsList.size() == 0){
            throw new CustomException("导入数据不能为空");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String[] split = fileName.split(" # ");
        String[] split2 = split[0].split("-");
        StringBuilder stringBuilder = new StringBuilder();
        InvSort invSort1 = new InvSort();
        List<InvSort> invSorts = invSortMapper.selectInvSortList(invSort1);
        ArrayList<InvSort> invSorts1 = new ArrayList<>();
        int length = split2.length;

        for (int i = 0; i < length-1; i++) {
            for (InvSort invSort : invSorts) {
                if(invSort.getSortName().equals(split2[i])){
                    stringBuilder.append(invSort.getSortCode());
                    for (InvSort sort : invSorts) {
                        if(sort.getAncestors().contains(invSort.getSortId().toString())){
                            invSorts1.add(sort);
                        }
                    }
                }
            }
            invSorts = new ArrayList<>(invSorts1);
            invSorts1.clear();
        }
        for (InvSort invSort : invSorts) {
            if(split2[length-1].equals(invSort.getSortName()) ){
                stringBuilder.append(invSort.getSortCode());
                 invSort1 = invSort;
            }
        }
        if(StringUtils.isNull(invSort1.getSortId())|| !invSort1.getSortId().equals(sortId)){
            failureMsg.append("很抱歉，导入失败！ 数据分类不正确!!");
            throw  new CustomException(failureMsg.toString());
        }
        for (InvItems invItems : invItemsList) {
            allTrim(invItems);
            try {
                if(StringUtils.isNull(invItems.getCode()) || "".equals(invItems.getCode())){
                    invItems.setCode(stringBuilder.toString());
                    invItems.setSupplyType("1");
                    insertData(invItems, invSort1);
                    successNum++;
                }

                else if(StringUtils.isNull(invItemsMapper.selectInvItemByCodes(invItems.getCode()))){
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、数据 " + invItems.getName() + " 不存在");
                }
               else if (updateSupport) {
                   if(UserConstants.NOT_UNIQUE.equals(hasSameName(invItems))){
                       Long aLong = invItemsMapper.selectVersionByCode(invItems.getCode());
                       invItems.setVersion(aLong);
                       updateInvItems(invItems);
                       successNum++;
                   }
                }
                String name = invItems.getName();
                String[] s = name.split("\\+");
                for (String s1 : s) {
                    String itemName = s1.substring(s1.indexOf('%')+1);
                    String substring = s1.substring(0, s1.indexOf('%'));
                    List<InvItems> invItemsList1 = invItemsMapper.selectInvItemByAbsName(itemName);
                    if (invItemsList1.size() == 0) {
                        failureNum++;
                       failureMsg.append("<br/>" + failureNum + "、数据 " + itemName + " 不存在");
                    }
                    else{
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、数据 " + itemName + "不唯一");
                    }
                }
            }catch (Exception e){
                failureNum++;
                String msg = "<br/>" + failureNum + "、数据 " + invItems.getName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }

        }
        if(failureNum >0 ){
            failureMsg.insert(0,"很抱歉，导入失败！ 共 " +failureNum + " 条数据格式不正确,错误如下: ");
            throw  new CustomException(failureMsg.toString());
        }
        else if(successNum == 0 && failureNum == 0){
            successMsg.insert(0,"导入数据无变化");
        }
            else{
            successMsg.insert(0,"恭喜您，数据已全部导入成功！ 共" +successNum  +" 条。");
        }
        return successMsg.toString();
    }

    private void allTrim(InvItems invItems) {
            String replace = invItems.getName().replace("（", "(");
            String replace1 = replace.replace("）", ")").replace(" ","");
            invItems.setName(replace1);
            String replace2 = invItems.getProperty().replace("（", "(").replace("）", ")").replace(" ", "");
            invItems.setProperty(replace2);
        }

    /**
     * 导入到related表中
     * @param invItems
     */
    private InvRelated insertRelated(InvItems invItems,InvItems ex,String per,InvSort invSort) {
            InvRelated invRelated = new InvRelated();
                String ancestors = invSort.getAncestors();
                String[] split1 = ancestors.split(",");
                if(split1.length > 1){
                    invRelated.setInvSortRoot(Long.valueOf(split1[1]));
                }else{
                    invRelated.setInvSortRoot(invSort.getSortId());
                }
            invRelated.setInvSortId(invItems.getSortId());
            invRelated.setInvName(invItems.getName());
            invRelated.setInvCode(invItems.getCode());
            invRelated.setInvAttribute(invItems.getAttribute());
            invRelated.setRelName(ex.getName());
            invRelated.setRelParam(per);
            invRelated.setRelAttribute(ex.getAttribute());
            invRelated.setRelCode(ex.getCode());
            invRelated.setRelSortId(ex.getSortId());
            invRelated.setRelSortRoot(Long.valueOf(ex.getSortRoot()));
            invItems.setSortRoot(ex.getSortRoot());
            return invRelated;
        }


    /**
     * 查询related列表
     * @param query
     * @return
     */
    @Override
    public List<InvItems> selectInvItemsAndRelated(Map<String, Object> query) {
        List<InvItems> invItemsList = invItemsMapper.selectInvItemsAndRelated(query);
        for (InvItems items : invItemsList) {
            String name = items.getName();
            String[] split = name.split("\\+");
            ArrayList<String> ex = new ArrayList<>();
            for (String s : split) {
                ex.add( s.substring(s.indexOf('%')+1));
            }
            items.setChildren(invRelatedMapper.selectInvRelated(ex, items.getCode()));
        }
        return invItemsList;
    }

    @Override
    public List<InvItems> getItems(String name,String code) {
        return  invItemsMapper.selectItemsByName(name,code);

    }

    @Override
    public ArrayList<InvItems> selectInvItemsBySortId(String sortId) {

        return invItemsMapper.selectInvItemsBySortId(sortId);

    }

    @Override
    public List<InvItems> selectInvItemsByContainSortId(HashMap<String,String> query) {
         return     invItemsMapper.selectInvItemByContainsSortId(query.get("sortId"),query.get("invName"),query.get("invCode"));

    }


    /**
     * 导入 数据库
     * @param invItems
     */
    private void insertData(InvItems invItems,InvSort invSort) {
        String sortRoot = invSortService.getSortRoot(invSort.getSortId().toString());
        InvSort invSort1 = invSortMapper.selectInvSortListBySortId(Long.valueOf(sortRoot));
        String[] split = invSort.getAncestors().split(",");
        if(split.length > 1){
            invItems.setSortRoot(split[1]);
        }
        else{
            invItems.setSortRoot(invSort.getSortId().toString());
        }

        if(StringUtils.isNull(invItems.getSortId())){
            invItems.setSortId(invSort.getSortId());
        }
        String s = hasSameName(invItems);
        if(UserConstants.NOT_UNIQUE.equals(s)){
            if(!("".equals(invItems.getUnitName())) && StringUtils.isNotNull(invItems.getUnitName()) && ("".equals(invItems.getUnitCode())) ){
              String code =  invUnitMapper.selectCodeByName(invItems.getUnitName());
              invItems.setUnitCode(code);
            }
          else{
              if(StringUtils.isNotEmpty(invSort.getUnitCode())){
                  invItems.setUnitCode(invSort.getUnitCode());
                  invItems.setUnitName(invSort.getUnitName());
              }else{
                  invItems.setUnitCode(invSort1.getUnitCode());
                  invItems.setUnitName(invSort1.getUnitName());
              }
            }
          if("".equals(invSort.getSupplyType()) || StringUtils.isNull(invSort.getSupplyType())){
              invItems.setSupplyType(invSort1.getSupplyType());
            }
          else {
              invItems.setSupplyType(invSort.getSupplyType());
          }
            /**  生成流水号 */
            generate(invItems);
            invItems.setName(invItems.getName().trim());
            invItems.setCreateBy(SecurityUtils.getUsername());
            invItemsMapper.insertInvItems(invItems);
        }else{
            throw new CustomException(invItems.getName()+"已存在");
        }

    }

    /**
     * 生成流水号
     * @param invItems
     */
    @Transactional(rollbackFor = Exception.class)
    public void generate(InvItems invItems) {
        StringBuilder code = new StringBuilder();
        InvSort invSort = invSortMapper.selectInvSortListBySortId(invItems.getSortId());
        String ancestors = invSort.getAncestors();
        if(ancestors.length() > 1 ){
            String[] split = ancestors.split(",");
            for (int i = 1; i < split.length; i++) {
                InvSort sort = invSortMapper.selectInvSortListBySortId(Long.valueOf(split[i]));
                code.append(sort.getSortCode());
            }
            code.append(invSort.getSortCode());
        }
        else{
        code.append(invSort.getSortCode());
        }
        if(StringUtils.isNull(invItems.getCode()) || invItems.getCode().equals("")){
            invItems.setCode(code.toString());
        }
        NumberFormat formart = NumberFormat.getNumberInstance();
        formart.setMinimumIntegerDigits(invSort.getSerialLength().intValue());
        formart.setGroupingUsed(false);
        String format = formart.format(invSort.getSerialNumber() + 1);
        String s = sysConfigServicel.selectConfigByKey("inv.code.length");
        if(format.length() + invItems.getCode().length() < Integer.parseInt(s)){
            int i = Integer.parseInt(s) - invItems.getCode().length();
            s ="%0"+i+"d";
            String uuid = String.format(s, Integer.valueOf(format));
            invItems.setCode(invItems.getCode()+uuid);
        }else{
            invItems.setCode(invItems.getCode()+format);
        }
        invSortMapper.addSerialNumberBySortId(invItems.getSortId());
    }

    @Override
    public InvItems selectInvItemsByCode(String invCode) {
        return invItemsMapper.selectInvItemByCodes(invCode);
    }

    @Override
    public List<InvItems> selectBatchBySortId(Map<String,String> query) {
        return invItemsMapper.selectBatchBySortId(query);
    }

    @Override
    public List<HashMap<String, String>> selectInvItemsDetailByCode(String invCode) {
        return invItemsMapper.selectInvItemsDetailByCode(invCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArrayList<InvItems> importInv(String originalFilename, InputStream is) {
        ArrayList<InvItems> invItemList = new ArrayList<>();
        Workbook work = getWorkbook(is, originalFilename);
        try{
            if (null == work) {
                throw new Exception("创建Excel工作薄为空！");
            }
            Sheet sheet = null;
            Row row = null;
            Cell cell = null;
            for (int i = 0; i < work.getNumberOfSheets(); i++) {
                sheet = work.getSheetAt(i);
                if (sheet == null) {
                    continue;
                }
                DataFormatter dataFormatter = new DataFormatter();
                int maxRow = sheet.getLastRowNum();
                for (int j = sheet.getFirstRowNum(); j <= maxRow; j++) {
                    Row row1 = sheet.getRow(j);
                    Cell cell1 = row1.getCell(1);
                    String s = dataFormatter.formatCellValue(cell1);
                    if (s.equals("母件名称")) {
                        j++;
                        Row row2 = sheet.getRow(j);
                        InvItems invItems = new InvItems();
                        Cell cell2 = row2.getCell(1);
                        String invName = cell2.getStringCellValue();
                        Cell cell3 = row2.getCell(13);
                        String sort = dataFormatter.formatCellValue(cell3);
                        Cell cell4 = row2.getCell(10);
                        String attribute = cell4.getStringCellValue();
                        Cell cell5 = row2.getCell(3);
                        String unitName = cell5.getStringCellValue();
                        String unitCode = invUnitMapper.selectCodeByName(unitName);
                        invItems.setSortRoot("101");
                        invItems.setName(invName);
                        invItems.setSortId(Long.valueOf(sort));
                        invItems.setAttribute(attribute);
                        invItems.setUnitName(unitName);
                        invItems.setUnitCode(unitCode);
                        invItems.setSupplyType("1");
                        generate(invItems);
                        invItems.setCreateBy(SecurityUtils.getUsername());
                        invItemsMapper.insertInvItems(invItems);
                    }
                }
            }
        }catch (Exception e){
            throw new CommonException("错误");
        }finally {
            try {
                work.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //创建Excel工作薄
        return invItemList;
    }

    @Override
    public void checkDuplicat(String sortId) {
      List<InvItems> list =    invItemsMapper.selectInvBySortId(sortId);

        StringBuilder msg = new StringBuilder();
        Integer i = 0;
        HashSet<String> strings = new HashSet<>();
        for (InvItems invItems : list) {
            String result =   invItems.getName() + invItems.getAttribute();
            String replace = result.replace(" ", "");
            if(strings.contains(replace)){
                msg.append((++i) + "、 物料名称：" +invItems.getName() + " 规格：" + invItems.getAttribute() +"</br>");
            }else{
                strings.add(replace);
            }
        }
        if(i > 0 ){
            throw  new CommonException(msg.toString());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArrayList<InvItems> importMachInv(String originalFilename, InputStream inputStream,String sortId) {
        String sortRoot =   invSortService.getSortRoot(sortId);
        ArrayList<InvItems> invItemList = new ArrayList<>();
        Workbook work = getWorkbook(inputStream, originalFilename);
        try{
            if (null == work) {
                throw new Exception("创建Excel工作薄为空！");
            }
            Sheet sheet = null;
            Row row = null;
            Cell cell = null;
            for (int i = 0; i < work.getNumberOfSheets(); i++) {
                sheet = work.getSheetAt(i);
                if (sheet == null) {
                    continue;
                }
                DataFormatter dataFormatter = new DataFormatter();
                int maxRow = sheet.getLastRowNum();
                for (int j = sheet.getFirstRowNum(); j <= maxRow; j++) {
                    Row row1 = sheet.getRow(j);
                    Cell cell1 = row1.getCell(1);
                    String s = dataFormatter.formatCellValue(cell1);
                    if (s.equals("母件名称")) {
                        j++;
                        Row row2 = sheet.getRow(j);
                        Cell cell6 = row2.getCell(8);
                        String value = cell6.getStringCellValue();
                        if(value.equals("审核")){
                            InvItems invItems = new InvItems();
                            Cell cell2 = row2.getCell(1);
                            String invName = cell2.getStringCellValue();
                            Cell cell3 = row2.getCell(11);
                            String sort = dataFormatter.formatCellValue(cell3);
                            Cell cell4 = row2.getCell(2);
                            String attribute = cell4.getStringCellValue();
                            Cell cell5 = row2.getCell(3);
                            String unitName = cell5.getStringCellValue();
                            String unitCode = invUnitMapper.selectCodeByName(unitName);
                            Cell cell7 = row2.getCell(10);
                            String drawingNo = cell7.getStringCellValue();
                            Cell cell8 = row2.getCell(13);
                            String supplyType = cell8.getStringCellValue();
                            if(supplyType.equals("加工")){
                                invItems.setSupplyType("2");
                            }else{
                                invItems.setSupplyType("1");
                            }
                            invItems.setSortRoot(sortRoot);
                            invItems.setName(invName);
                            invItems.setSortId(Long.valueOf(sort));
                            invItems.setAttribute(attribute);
                            invItems.setUnitName(unitName);
                            invItems.setUnitCode(unitCode);
                            invItems.setSupplyType("1");
                            invItems.setDrawingNo(drawingNo);
                            generate(invItems);
                            invItems.setCreateBy(SecurityUtils.getUsername());
                            invItemsMapper.insertInvItems(invItems);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new CommonException("错误");
        }finally {
            try {
                work.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //创建Excel工作薄
        return invItemList;
    }

    @Override
    public ArrayList<InvSort> selectInvSort() {

        return  invItemsMapper.selectInvSort();


    }

    private Workbook getWorkbook(InputStream is, String originalFilename) {
        Workbook workbook = null;
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        try {
            if (".xls".equals(fileType)) {
                workbook = new HSSFWorkbook(is);
            } else if (".xlsx".equals(fileType)) {
                workbook = new XSSFWorkbook(is);
            } else {
                throw new Exception("请上传excel文件！");
            }
        }catch (Exception e){
            throw  new CommonException("文件错误");
        }
        return workbook;
    }


    public InvItems selectInvByName(String name) {
        return  invItemsMapper.selectInvByName(name);

    }

    public List<InvItems> selectInvBySortId(String sortId) {

        return  invItemsMapper.selectInvPesticideBySortId(sortId);


    }
}

