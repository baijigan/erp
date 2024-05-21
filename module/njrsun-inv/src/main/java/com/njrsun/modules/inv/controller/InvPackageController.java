package com.njrsun.modules.inv.controller;


import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.inv.domain.InvPackage;
import com.njrsun.modules.inv.service.IInvPackageService;
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

import java.util.List;

/**
 * 包装单位Controller
 * 
 * @author njrsun
 * @date 2021-04-06
 */
@Api( tags = "包装单位")
@RestController
@RequestMapping("/inv/package")
public class InvPackageController extends BaseController
{
    @Autowired
    private IInvPackageService invPackageService;

    /**
     * 查询包装单位列表
     */
  //  @PreAuthorize("@ss.hasPermi('inv:package:list')")
    @ApiOperation("查询包装单位列表")
    @GetMapping("/list")
    public TableDataInfo list(InvPackage invPackage)
    {
        startPage();
        List<InvPackage> list = invPackageService.selectInvPackageList(invPackage);
        return getDataTable(list);
    }


    /**
     * 新增包装单位
     */
   // @PreAuthorize("@ss.hasPermi('inv:package:add')")
    @ApiOperation("新增包装单位")
    @Log(title = "包装单位", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody InvPackage invPackage)
    {
        if(UserConstants.UNIQUE.equals(invPackageService.isSameName(invPackage))){
            return AjaxResult.error("新增失败" + invPackage.getName()+" 重复");
        }
        else if( UserConstants.UNIQUE.equals(invPackageService.isSameCode(invPackage))){
            return AjaxResult.error("新增失败 " + invPackage.getCode() + " 重复");
        }
        else{
          InvPackage ex  = invPackageService.insertInvPackage(invPackage);
          return AjaxResult.success(ex);
        }
    }


    /**
     * 修改包装单位
     */
  //  @PreAuthorize("@ss.hasPermi('inv:package:edit')")
    @ApiOperation("修改包装单位")
    @Log(title = "包装单位", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody InvPackage invPackage)
    {
         if(UserConstants.UNIQUE.equals(invPackageService.isSameName(invPackage))) {
                return AjaxResult.error(invPackage.getName()+"  重复,请重新输入");
         }
         else if(UserConstants.UNIQUE.equals(invPackageService.isSameCode(invPackage))){
             return AjaxResult.error(invPackage.getCode() + " 重复,请重新输入");
         }
         else {
             InvPackage ex = invPackageService.updateInvPackage(invPackage);
             if(StringUtils.isNull(ex)){
                 return AjaxResult.error("更新失败");
             }
             else{
                 return AjaxResult.success(ex);
             }
         }
    }

    /**
     * 删除包装单位
     */
 //   @PreAuthorize("@ss.hasPermi('inv:package:remove')")
    @ApiOperation("删除包装单位")
    @Log(title = "包装单位", businessType = BusinessType.DELETE)
	@DeleteMapping("/{uniqueIds}")
    public AjaxResult remove(@PathVariable Long[] uniqueIds)
    {
        return toAjax(invPackageService.deleteInvPackageByIds(uniqueIds));
    }


}
