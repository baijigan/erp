package com.njrsun.modules.inv.controller;

import com.njrsun.modules.inv.service.IInvItemsService;
import com.njrsun.modules.inv.service.IInvRelatedService;
import com.njrsun.modules.inv.service.IInvSortService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.njrsun.common.core.controller.BaseController;

/**
 * 物料关联Controller
 * 
 * @author njrsun
 * @date 2021-05-31
 */
@Api( tags = "物料关联表")
@RestController
@RequestMapping("/inv/items")
public class InvRelatedController extends BaseController
{
    @Autowired
    private IInvRelatedService invRelatedService;
    @Autowired
    private IInvItemsService invItemsService;
    @Autowired
    private IInvSortService invSortService;


    /**
     * 导出物料关联列表
     */
//    @PreAuthorize("@ss.hasPermi('inv:related:export')")
//    @Log(title = "物料关联", businessType = BusinessType.EXPORT)
//    @ApiOperation("导出物料关联列表")
//    @GetMapping("/export/tc")
//    public AjaxResult export(InvItems invItems,Boolean type)
//    {
//        String sheetName;
//        if(StringUtils.isNull(invItems.getSortId())){
//            sheetName = "物料数据";
//        }
//        else{
//            sheetName = invSortService.getExcelName(invItems.getSortId());
//        }
//        List<InvItems> ex = invItemsService.selectInvItemsList(invItems,type,null);
//        ExcelUtil<InvItems> excelUtil = new ExcelUtil<>(InvItems.class);
//        return excelUtil.exportExcel(ex,sheetName);
//    }

//
//    @PreAuthorize("@ss.hasPermi('inv:related:import')")
//    @ApiOperation("导入物料表")
//    @PostMapping("/importData/tc")
//    public AjaxResult importData(MultipartFile file, boolean updateSupport, Long sortId) throws Exception
//    {
//        ExcelUtil<InvItems> util = new ExcelUtil<>(InvItems.class);
//        List<InvItems> invItemsList = util.importExcel(file.getInputStream());
//        String originalFilename = file.getOriginalFilename();
//        String message = invItemsService.importInvRelated(invItemsList, updateSupport, originalFilename,sortId);
//        return AjaxResult.success(message);
//    }
//
//
//
//    /**
//     * 获取物料关联详细信息
//     */
//   // @PreAuthorize("@ss.hasPermi('system:related:query')")
//    @ApiOperation("获取关联")
//    @GetMapping(value = "/tc/{invCode}")
//    public AjaxResult getInfo(@PathVariable("invCode") String invCode)
//    {
//        return AjaxResult.success(invRelatedService.selectInvRelatedByCode(invCode));
//    }
//
//    /**
//     * 新增物料关联
//     */
//    @PreAuthorize("@ss.hasPermi('inv:related:add')")
//    @ApiOperation("新增")
//    @Log(title = "物料关联", businessType = BusinessType.INSERT)
//    @PostMapping("/tc")
//    public AjaxResult add(@RequestBody InvItems invItems)
//    {
//         if(invRelatedService.containSameInv(invItems)) {
//             String s = invRelatedService.insertInvRelated(invItems);
//             return  AjaxResult.success(invRelatedService.selectInvRelatedByCode(s));
//         }
//         else{
//             return AjaxResult.error("农药中含有相同成分,请重新选择");
//         }
//
//    }
//
//    /**
//     * 修改物料关联
//     */
//    @PreAuthorize("@ss.hasPermi('inv:related:edit')")
//    @Log(title = "物料关联", businessType = BusinessType.UPDATE)
//    @ApiOperation("修改关联")
//    @PutMapping("/tc")
//    public AjaxResult edit(@RequestBody InvItems invRelated)
//    {
//        if(invRelatedService.containSameInv(invRelated)){
//            InvItems items = invRelatedService.updateInvRelated(invRelated);
//            return AjaxResult.success(items);
//        }
//        else{
//            return AjaxResult.error("农药中含有相同成分,请重新编辑");
//        }
//
//    }
//
//    /**
//     * 删除物料关联
//     */
//    @PreAuthorize("@ss.hasPermi('inv:related:remove')")
//    @Log(title = "物料关联", businessType = BusinessType.DELETE)
//	@DeleteMapping("/tc/{invCode}")
//    @ApiOperation("删除关联")
//    public AjaxResult remove(@PathVariable String invCode)
//    {
//        return toAjax(invRelatedService.deleteInvRelatedByCode(invCode));
//    }
//
//    @ApiOperation("查询原药物料")
//    @GetMapping(value = "/tc/getItems/{name}")
//    public AjaxResult getItems(@PathVariable("name") String name){
//        return AjaxResult.success(invRelatedService.getItems(name));
//    }

//    @ApiOperation("构建原药物料分类树")
//    @GetMapping("/sort/tc/tree")
//    public AjaxResult tree(@RequestParam String feature){
//        return AjaxResult.success(invSortService.buildTree(feature));
//    }

//    @PreAuthorize("@ss.hasPermi('inv:related:effc')")
//    @ApiOperation("检验")
//    @GetMapping("/check/tc/{sortId}")
//    public  AjaxResult check(@PathVariable("sortId") String sortId){
//        String re =  invRelatedService.checkRelated(sortId);
//        return  AjaxResult.success(re);
//    }

//    @PreAuthorize("@ss.hasPermi('inv:related:effc')")
//    @ApiOperation("核名")
//    @GetMapping("/review")
//    public  AjaxResult review(@RequestParam String  sortId){
//        invRelatedService.review(sortId);
//        return  AjaxResult.success("通过");
//    }

}
