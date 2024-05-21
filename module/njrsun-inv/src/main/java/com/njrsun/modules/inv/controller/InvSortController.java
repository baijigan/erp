package com.njrsun.modules.inv.controller;

import java.util.List;
import java.util.Map;

import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.modules.inv.domain.InvSort;
import com.njrsun.modules.inv.mapper.InvSortMapper;
import com.njrsun.modules.inv.service.IInvSortService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * 物料分类Controller
 * 
 * @author njrsun
 * @date 2021-04-06
 */
@Api(value = "物料分类",tags = "物料分类")
@RestController
@RequestMapping("/inv")
public class InvSortController extends BaseController
{
    @Autowired
    private IInvSortService invSortService;

    @Autowired
    private InvSortMapper invSortMapper;


    /**
     * 查询物料分类列表
     */
    @PreAuthorize("@ss.hasPermi('inv:sort:list')")
    @ApiOperation("物料分类查询")
    @GetMapping("/sort/list")
    public TableDataInfo list(InvSort invSort)
    {
        startPage();
        List<InvSort> list = invSortService.selectInvSortList(invSort);
        return getDataTable(list);
    }


    /**
     * 新增物料分类
     */
    @PreAuthorize("@ss.hasPermi('inv:sort:add')")
    @Log(title = "物料分类", businessType = BusinessType.INSERT)
    @ApiOperation("物料分类新增")
    @PostMapping("/sort/add")
    public AjaxResult add(@RequestBody InvSort invSort)
    {
         if(UserConstants.UNIQUE.equals(invSortService.isSameCode(invSort))){
             return AjaxResult.error("新增失败： "+invSort.getSortCode()+" 重复");
         }
         else if(UserConstants.UNIQUE.equals(invSortService.isSameName(invSort))){
             return AjaxResult.error("新增失败: " + invSort.getSortName()+"重复");
        }
        InvSort ex =  invSortService.insertInvSort(invSort);
        return  AjaxResult.success(ex);
    }


    /**
     * 修改物料分类
     */
   @PreAuthorize("@ss.hasPermi('inv:sort:edit')")
    @Log(title = "物料分类", businessType = BusinessType.UPDATE)
    @ApiOperation("物料分类修改")
    @PutMapping("/sort/edit")
    public AjaxResult edit(@RequestBody InvSort invSort)
    {
        if(UserConstants.UNIQUE.equals(invSortService.isSameName(invSort))){
            return AjaxResult.error("修改失败 " +invSort.getSortName()+"  相同");
        }
         else if(UserConstants.UNIQUE.equals(invSortService.isSameCode(invSort))){
            return AjaxResult.error("修改失败 "+invSort.getSortCode()+ " 相同");
        }
        InvSort ex = invSortService.updateInvSort(invSort);
        if(StringUtils.isNull(ex)){
            return AjaxResult.error(" 数据有变动，请重新获取最新数据 ");
        }
        else
        {
            return AjaxResult.success(ex);
        }
    }


    /**
     * 删除物料分类
     */
    @PreAuthorize("@ss.hasPermi('inv:sort:remove')")
    @ApiOperation("物料分类删除")
    @Log(title = "物料分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/sort/{sortId}")
    public AjaxResult remove(@PathVariable Long sortId)
    {
        if(invSortMapper.hasChild(sortId) >0) {
            return  AjaxResult.error("存在子项");
        }
        else if(UserConstants.UNIQUE.equals(invSortService.hasItem(sortId)) ){
            return AjaxResult.error("该类别下存在物料，无法删除...");
        }
        else{
            return toAjax(invSortService.deleteInvSortByIds(sortId));
        }
    }

    /**
     * 构建物料分类树
     */
    @ApiOperation("构建物料分类树")
    @GetMapping("/sort/tree")
    public AjaxResult tree(){
        return AjaxResult.success(invSortService.buildTree());
    }


    @ApiOperation("导出分类")
    @Log(title ="物料管理",businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    // @PreAuthorize("@ss.hasPermi('inv:items:export')")
    public AjaxResult export(){
        InvSort invSort = new InvSort();
        List<InvSort> ex = invSortService.selectInvSortList(invSort);
        ExcelUtil<InvSort> excelUtil = new ExcelUtil<>(InvSort.class);
        return excelUtil.exportExcel(ex,"物料分类");
    }

    @ApiOperation("导入分类")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<InvSort> util = new ExcelUtil<InvSort>(InvSort.class);
        List<InvSort> invSortList = util.importExcel(file.getInputStream());
        String fileName = file.getOriginalFilename();
        String message = invSortService.importData(invSortList, fileName);
        return AjaxResult.success(message);
     }



    @ApiOperation("根据sortName查找child")
    @GetMapping("/selectChild")
     public AjaxResult getChildSortByName(String sortName){
        List<Map<String,String>> list =   invSortService.selectInvChildBySortName(sortName);
        return AjaxResult.success(list);
     }

}
