package com.njrsun.modules.prs.controller;

import java.util.List;

import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.core.page.TableDataInfo;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.modules.prs.domain.PrsEquipment;
import com.njrsun.modules.prs.service.IPrsEquipmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 设备列Controller
 * 
 * @author njrsun
 * @date 2021-06-25
 */
@RestController
@RequestMapping("/prs/equipment")
@Api(tags = "设备")
public class PrsEquipmentController extends BaseController
{
        @Autowired
        private IPrsEquipmentService prsEquipmentService;

    /**
     * 查询设备列列表
     */
   @PreAuthorize("@ss.hasPermi('prs:equipment:list')")
    @ApiOperation("查询设备列表")
    @GetMapping("/list")
    public TableDataInfo list(PrsEquipment prsEquipment)
    {
        startPage();
        List<PrsEquipment> list = prsEquipmentService.selectPrsEquipmentList(prsEquipment);
        return getDataTable(list);
    }
    /**
     * 获取设备列详细信息
     */
   @PreAuthorize("@ss.hasPermi('prs:equipment:entity:view')")
    @GetMapping(value = "/{uniqueId}")
    public AjaxResult getInfo(@PathVariable("uniqueId") Long uniqueId)
    {
        return AjaxResult.success(prsEquipmentService.selectPrsEquipmentById(uniqueId));
    }

    /**
     * 新增设备列
     */
  @PreAuthorize("@ss.hasPermi('prs:equipment:entity:add')")
    @ApiOperation("新增设备列表")
    @Log(title = "设备列", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsEquipment prsEquipment)
    {
        return toAjax(prsEquipmentService.insertPrsEquipment(prsEquipment));
    }

    /**
     * 修改设备列
     */
   @PreAuthorize("@ss.hasPermi('prs:equipment:entity:edit')")
    @ApiOperation("修改设备列表")
    @Log(title = "设备列", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsEquipment prsEquipment)
    {
        return toAjax(prsEquipmentService.updatePrsEquipment(prsEquipment));
    }

    /**
     * 删除设备列
     */
   @PreAuthorize("@ss.hasPermi('prs:equipment:remove')")
    @ApiOperation("删除设备列表")
    @Log(title = "设备列", businessType = BusinessType.DELETE)
	@DeleteMapping("/{uniqueId}")
    public AjaxResult remove(@PathVariable Long uniqueId)
    {
        return toAjax(prsEquipmentService.deletePrsEquipmentById(uniqueId));
    }
}
