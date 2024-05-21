package com.njrsun.modules.inv.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.modules.inv.domain.InvItem;
import com.njrsun.modules.inv.domain.InvItems;
import com.njrsun.modules.inv.domain.InvSort;
import com.njrsun.modules.inv.service.IInvItemsService;

import com.njrsun.modules.inv.service.IInvRelatedService;
import com.njrsun.modules.inv.service.IInvSortService;
import com.njrsun.system.service.ISysDictTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.enums.BusinessType;

import com.njrsun.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;


/**
 * 物料名称Controller
 * 
 * @author njrsun
 * @date 2021-04-06
 */
@Slf4j
@Api( tags = "物料")
@RestController
@RequestMapping("/inv/items")
public class InvItemsController extends BaseController
{
    @Autowired
    private IInvItemsService invItemsService;

    @Autowired
    private IInvSortService invSortService;

    @Autowired
    private ISysDictTypeService iSysDictTypeService;

    @Autowired
    private IInvRelatedService invRelatedService;


    /**
     * 查询物料名称列表
     */
    @ApiOperation(value = "物料名称列表查询",httpMethod = "GET")
    @GetMapping("/list")
    public TableDataInfo list(InvItems invItems,@RequestParam("type") Boolean type)
    {
        startPage();
        ArrayList<Long> longs = getLongs(invItems);
        List<InvItems> list = invItemsService.selectInvItemsList(invItems,type,longs);
        HashMap<String, String> inv_supply_type = iSysDictTypeService.transform("inv_supply_type");
        for (InvItems items : list) {
            HashMap<String, Object> stringStringHashMap = new HashMap<>();
            BigDecimal multiply = items.getQuantity().add(items.getWayQuantity()).subtract(items.getLockQuantity());
            stringStringHashMap.put("inv_supply_type",inv_supply_type.get(items.getSupplyType()));
            stringStringHashMap.put("availableQuantity",multiply);
            items.setParams(stringStringHashMap);
        }
        return getDataTable(list);
    }


    public ArrayList<Long> getLongs(InvItems invItems) {
        ArrayList<Long> longs = new ArrayList<>();
        if (   StringUtils.isNotNull(invItems.getSortRoot()) && invItems.getSortRoot().length() > 0) {
            String sortRoot = invItems.getSortRoot();
            String[] split = sortRoot.split(",");
            for (String s : split) {
                longs.add(Long.valueOf(s));
            }
        }
        return longs;
    }


    /**
     * 获取物料名称详细信息
     */
    @ApiOperation("物料名称详情")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long uniqueId)
    {
        InvItems invItems = invItemsService.selectInvItemsById(uniqueId);
        return AjaxResult.success(invItems);
    }


    /**
     * 新增物料名称
     */
    @ApiOperation("新增物料")
    @PreAuthorize("@ss.hasPermi('inv:items:add')")
    @Log(title = "物料名称", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody InvItems invItems)
    {
        String s = invItemsService.hasSameName(invItems);
        if(s.equals(UserConstants.NOT_UNIQUE)){
            invItemsService.insertInvItems(invItems);
            InvItems ex = invItemsService.selectInvItemsById(invItems.getUniqueId());
            return AjaxResult.success(ex);
        }
        else{
            return AjaxResult.error(s + "重复");
        }
    }


    /**
     * 修改物料名称
     */
    @ApiOperation("修改物料")
    @PreAuthorize("@ss.hasPermi('inv:items:edit')")
    @Log(title = "物料名称", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody InvItems invItems)
    {
        String s = invItemsService.hasSameName(invItems);
        if(!(UserConstants.NOT_UNIQUE.equals(s))){
            return AjaxResult.error("修改失败,"+s+"重复");
        }
        invItemsService.updateInvItems(invItems);
        return AjaxResult.success(invItemsService.selectInvItemsById(invItems.getUniqueId()));
    }


    /**
     * 删除物料
     */
    @ApiOperation("删除物料")
    @PreAuthorize("@ss.hasPermi('inv:items:remove')")
    @Log(title = "物料名称", businessType = BusinessType.DELETE)
	@DeleteMapping("/{code}")
    public AjaxResult remove(@PathVariable String code)
    {
        return toAjax(invItemsService.deleteInvItemsByCode(code));
    }

    @PreAuthorize("@ss.hasPermi('inv:items:dump')")
    @ApiOperation("删除所有")
    @DeleteMapping("/all/{sortId}")
    public AjaxResult removeAll(@PathVariable Long sortId){
       invItemsService.deleteInvItemsAllByCodeId(sortId);
       return AjaxResult.success("删除成功");
    }

    /**
     * 导出物料
     * @param invItems
     * @return
     */

    @ApiOperation("导出物料")
    @Log(title ="物料管理",businessType = BusinessType.EXPORT)
    @GetMapping("/export")
   @PreAuthorize("@ss.hasPermi('inv:items:export')")
    public AjaxResult export(InvItems invItems,Boolean type){
        String sheetName;
        if(StringUtils.isNull(invItems.getSortId())){
            sheetName = "物料数据";
        }
        else{
            sheetName = invSortService.getExcelName(invItems.getSortId());
        }
        ArrayList<Long> longs = getLongs(invItems);
        List<InvItems> ex = invItemsService.selectInvItemsList(invItems,type, longs);
        ExcelUtil<InvItems> excelUtil = new ExcelUtil<>(InvItems.class);
        return excelUtil.exportExcel(ex,sheetName);
    }

    /**
     * 导入物料
     * @param file  文件流
     * @param updateSupport 是否支持更新
     * @param sortId   分类
     * @return
     * @throws Exception
     */

   @PreAuthorize("@ss.hasPermi('inv:items:import')")
    @ApiOperation("导入物料表")
    @PostMapping("/import")
    public AjaxResult importData(MultipartFile file, boolean updateSupport,Long sortId) throws Exception
    {
        ExcelUtil<InvItems> util = new ExcelUtil<>(InvItems.class);
        List<InvItems> invItemsList = util.importExcel(file.getInputStream());
        String fileName = file.getOriginalFilename();
        String message = invItemsService.importData(invItemsList, updateSupport, fileName,sortId);
        return AjaxResult.success(message);
    }


    /**
     * 生成模板
     * @return
     */
    @ApiOperation("生成模板")
    @GetMapping("/importTemplate")
    public AjaxResult importTemplate(Long sortId){
      String  sheetName = invSortService.getExcelName(sortId);
        ExcelUtil<InvItems> util = new ExcelUtil<>(InvItems.class);
        return util.importTemplateExcel(sheetName);
    }

    @PreAuthorize("@ss.hasPermi('inv:related:list')")
    @ApiOperation("查询物料TC表")
    @GetMapping("/list/tc")
    public TableDataInfo list(@RequestParam Map<String,Object> query)
    {
        startPage();
        List<InvItems> list = invItemsService.selectInvItemsAndRelated(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inv:related:view')")
    @ApiOperation("根据invCode获取物料关联详情")
    @GetMapping("/getDetailItems")
    public AjaxResult getDetailItems(@RequestParam String invCode){
        List<HashMap<String,String>> result = invItemsService.selectInvItemsDetailByCode(invCode);
        return AjaxResult.success(result);
    }

    @PreAuthorize("@ss.hasPermi('inv:items:import')")
    @ApiOperation("导入成品")
    @Log(title ="物料管理",businessType = BusinessType.IMPORT)
    @PostMapping("/import/tc")
    // @PreAuthorize("@ss.hasPermi('inv:items:export')")
    public AjaxResult exportEnd(MultipartFile file) throws Exception {
        ArrayList<InvItems> rdEbomMasters = invItemsService.importInv(file.getOriginalFilename(), file.getInputStream());
        return AjaxResult.success();
    }


    @PreAuthorize("@ss.hasPermi('inv:items:check')")
    @ApiOperation("查重")
    @GetMapping("/checkDuplicat")
    public AjaxResult checkDuplicat(@RequestParam String sortId){
        invItemsService.checkDuplicat(sortId);
        return AjaxResult.success("通过");
    }

    @PreAuthorize("@ss.hasPermi('inv:items:import')")
    @ApiOperation("导入成品")
    @Log(title ="物料管理",businessType = BusinessType.IMPORT)
    @PostMapping("/import/mach")
    // @PreAuthorize("@ss.hasPermi('inv:items:export')")
    public AjaxResult ultimateImport(MultipartFile file,String sortId) throws Exception {
        ArrayList<InvItems> rdEbomMasters = invItemsService.importMachInv(file.getOriginalFilename(), file.getInputStream(),sortId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('inv:items:export')")
    @ApiOperation("导出成品")
    @Log(title ="物料管理",businessType = BusinessType.EXPORT)
    @GetMapping("/export/mach")
    public AjaxResult ultimateExport(InvItems invItems){
        String sheetName;
        if(StringUtils.isNull(invItems.getSortId())){
            sheetName = "成品数据";
        }
        else{
            sheetName = invSortService.getExcelName(invItems.getSortId());
        }
        List<InvItem> ex = invItemsService.selectMachInvItemsList(invItems);

        ExcelUtil<InvItem> excelUtil = new ExcelUtil<>(InvItem.class);
        return excelUtil.exportExcel(ex,sheetName);

    }

    @PreAuthorize("@ss.hasPermi('inv:related:export')")
    @Log(title = "物料关联", businessType = BusinessType.EXPORT)
    @ApiOperation("导出物料关联列表")
    @GetMapping("/export/tc")
    public AjaxResult exportTc(InvItems invItems,Boolean type)
    {
        String sheetName;
        if(StringUtils.isNull(invItems.getSortId())){
            sheetName = "物料数据";
        }
        else{
            sheetName = invSortService.getExcelName(invItems.getSortId());
        }
        List<InvItems> ex = invItemsService.selectInvItemsList(invItems,type,null);
        ExcelUtil<InvItems> excelUtil = new ExcelUtil<>(InvItems.class);
        return excelUtil.exportExcel(ex,sheetName);
    }


    @ApiOperation("核名")
    @GetMapping("/checkName")
    public  AjaxResult review(@RequestParam String  sortId){
        invRelatedService.review(sortId);
        return  AjaxResult.success("通过");
    }

    @ApiOperation("非委外")
    @GetMapping("/selectInvSort")
    public AjaxResult selectInvSort(){

     ArrayList<InvSort>  list  =   invItemsService.selectInvSort();

        return AjaxResult.success(list);
    }


}
