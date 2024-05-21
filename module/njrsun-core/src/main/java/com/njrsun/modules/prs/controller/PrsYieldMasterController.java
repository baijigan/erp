package com.njrsun.modules.prs.controller;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.modules.prs.domain.PrsYieldMaster;
import com.njrsun.modules.prs.service.IPrsYieldMasterService;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 生产报工单主Controller
 * 
 * @author njrsun
 * @date 2022-01-13
 */
@RestController
@Api(tags = "生产报工单")
@RequestMapping("/prs/yield")
public class PrsYieldMasterController extends BaseController
{
    @Autowired
    private IPrsYieldMasterService prsYieldMasterService;

    /**
     * 查询生产报工单主列表
     */
    @PreAuthorize("@ss.hasPermi('prs:yield:list')")
    @GetMapping("/list")
    public TableDataInfo list(PrsYieldMaster prsYieldMaster)
    {
        startPage();
        List<PrsYieldMaster> list = prsYieldMasterService.selectPrsYieldMasterList(prsYieldMaster);
        return getDataTable(list);
    }

    /**
     * 导出生产报工单主列表
     */
    @PreAuthorize("@ss.hasPermi('prs:yield:export')")
    @Log(title = "生产报工单主", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PrsYieldMaster prsYieldMaster)
    {
        List<PrsYieldMaster> list = prsYieldMasterService.selectPrsYieldMasterList(prsYieldMaster);
        ExcelUtil<PrsYieldMaster> util = new ExcelUtil<>(PrsYieldMaster.class);
        return util.exportExcel(list, "yield");
    }

    /**
     * 获取生产报工单主详细信息
     */
    @PreAuthorize("@ss.hasPermi('prs:yield:entity:view')")
    @GetMapping
    public AjaxResult getInfo(PrsYieldMaster prsYieldMaster)
    {
        return AjaxResult.success(prsYieldMasterService.selectPrsYieldMasterById(prsYieldMaster));
    }

    /**
     * 新增生产报工单主
     */
    @PreAuthorize("@ss.hasPermi('prs:yield:entity:add')")
    @Log(title = "生产报工单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsYieldMaster prsYieldMaster)
    {
        prsYieldMasterService.insertPrsYieldMaster(prsYieldMaster);
        return AjaxResult.success(prsYieldMasterService.selectPrsYieldMasterById(prsYieldMaster));
    }

    /**
     * 修改生产报工单主
     */
    @PreAuthorize("@ss.hasPermi('prs:yield:entity:edit')")
    @Log(title = "生产报工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsYieldMaster prsYieldMaster)
    {
        prsYieldMasterService.updatePrsYieldMaster(prsYieldMaster);
        return AjaxResult.success(prsYieldMasterService.selectPrsYieldMasterById(prsYieldMaster));
    }

    /**
     * 删除生产报工单主
     */
    @PreAuthorize("@ss.hasPermi('prs:yield:remove')")
    @Log(title = "生产报工单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<PrsYieldMaster> list)
    {
        return toAjax(prsYieldMasterService.deletePrsYieldMasterByIds(list));
    }

    @PreAuthorize("@ss.hasPermi('prs:yield:permit')")
    @Log(title = "审核订单", businessType = BusinessType.PERMIT)
    @ApiOperation("审核")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody List<PrsYieldMaster> list){
        PrsYieldMaster prsYieldMaster = prsYieldMasterService.batchCheck(list);
        return AjaxResult.success(prsYieldMaster);
    }


    @PreAuthorize("@ss.hasPermi('prs:yield:revoke')")
    @Log(title = "反审核订单", businessType = BusinessType.REVOKE)
    @ApiOperation("反审核")
    @PostMapping("/antiCheck")
    public AjaxResult antiCheck(@RequestBody  List<PrsYieldMaster> list){
        PrsYieldMaster prsYieldMaster = prsYieldMasterService.batchAntiCheck(list);
        return AjaxResult.success(prsYieldMaster);
    }
}
