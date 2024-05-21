package com.njrsun.modules.inv.controller;

import java.util.List;

import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.inv.domain.InvUnit;
import com.njrsun.modules.inv.service.IInvUnitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.enums.BusinessType;

import com.njrsun.common.core.page.TableDataInfo;

/**
 * 计量单位Controller
 * 
 * @author njrsun
 * @date 2021-04-06
 */
@Api(tags = "计量单位")
@RestController
@RequestMapping("/inv/unit")
public class InvUnitController extends BaseController {
    @Autowired
    private IInvUnitService invUnitService;

    /**
     * 查询计量单位列表
     */
    // @PreAuthorize("@ss.hasPermi('inv:unit:list')")
    @ApiOperation("查询计量单位列表")
    @GetMapping("/list")
    public TableDataInfo list(InvUnit invUnit) {
        startPage();
        List<InvUnit> list = invUnitService.selectInvUnitList(invUnit);
        return getDataTable(list);
    }

    /**
     * 新增计量单位
     */
    //  @PreAuthorize("@ss.hasPermi('inv:unit:add')")
    @ApiOperation("新增计量单位")
    @Log(title = "计量单位", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody InvUnit invUnit) {
        if(UserConstants.UNIQUE.equals(invUnitService.isSameName(invUnit))){
            return AjaxResult.error(" 新增失败: " + invUnit.getName()+"已存在");
        }
        else if(UserConstants.UNIQUE.equals(invUnitService.isSameCode(invUnit))){
            return AjaxResult.error(" 新增失败： " + invUnit.getCode()+ "已存在");
        }
        return  AjaxResult.success(invUnitService.insertInvUnit(invUnit));
    }

    /**
     * 修改计量单位
     */
    //   @PreAuthorize("@ss.hasPermi('inv:unit:edit')")
    @ApiOperation("修改计量单位")
    @Log(title = "计量单位", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody InvUnit invUnit) {
        if(UserConstants.UNIQUE.equals(invUnitService.isSameName(invUnit))){
            return AjaxResult.error(" 修改失败： " + invUnit.getName() + " 已存在");
        }
        else if(UserConstants.UNIQUE.equals(invUnitService.isSameCode(invUnit))){
            return AjaxResult.error(" 修改失败 " + invUnit.getCode() + " 已存在");
        }
        InvUnit ex = invUnitService.updateInvUnit(invUnit);
        if(StringUtils.isNotNull(ex)){
            return AjaxResult.success(ex);
        }
        else{
            return AjaxResult.error("数据未发生改变");
        }

    }

    /**
     * 删除计量单位
     */
    //  @PreAuthorize("@ss.hasPermi('inv:unit:remove')")
    @ApiOperation("删除计量单位")
    @Log(title = "计量单位", businessType = BusinessType.DELETE)
    @DeleteMapping("/{uniqueIds}")
    public AjaxResult remove(@PathVariable Long[] uniqueIds) {
        return toAjax(invUnitService.deleteInvUnitByIds(uniqueIds));
    }
}