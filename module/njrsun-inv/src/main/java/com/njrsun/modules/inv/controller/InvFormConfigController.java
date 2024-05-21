package com.njrsun.modules.inv.controller;

import java.util.List;
import java.util.Map;

import com.njrsun.modules.inv.domain.InvFormConfig;
import com.njrsun.modules.inv.service.IInvFormConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 单据配置Controller
 * 
 * @author njrsun
 * @date 2021-08-10
 */
@Api(tags = "单据配置")
@RestController
@RequestMapping("/inv/form/config")
public class InvFormConfigController extends BaseController
{
    @Autowired
    private IInvFormConfigService invFormConfigService;

    /**
     * 查询单据配置列表
     */
    @ApiOperation("列表")
  //  @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam Map<String,String > query)
    {
        startPage();
        List<InvFormConfig> list = invFormConfigService.selectInvFormConfigList(query);
        return getDataTable(list);
    }

    /**
     * 导出单据配置列表
     */
  //  @PreAuthorize("@ss.hasPermi('system:config:export')")
    @Log(title = "单据配置", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(@RequestParam Map<String,String > query)
    {
        List<InvFormConfig> list = invFormConfigService.selectInvFormConfigList(query);
        ExcelUtil<InvFormConfig> util = new ExcelUtil<InvFormConfig>(InvFormConfig.class);
        return util.exportExcel(list, "config");
    }

    /**
     * 获取单据配置详细信息
     */
    @ApiOperation("详情")
  //  @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configKey}")
    public AjaxResult getInfo(@PathVariable("configKey") String key)
    {
        return AjaxResult.success(invFormConfigService.selectInvFormConfigByKey(key));
    }

    /**
     * 新增单据配置
     */
    @ApiOperation("新增")
  //  @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "单据配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody InvFormConfig invFormConfig)
    {
        int i =  invFormConfigService.isSameConfig(invFormConfig);
        if( i  > 0){
            return AjaxResult.error("参数键名需为唯一值");
        }else{
            invFormConfigService.insertInvFormConfig(invFormConfig);
            return AjaxResult.success(invFormConfigService.selectInvFormConfigByKey(invFormConfig.getConfigKey()));
        }
    }

    /**
     * 修改单据配置
     */
    @ApiOperation("修改")
   // @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "单据配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody InvFormConfig invFormConfig)
    {
        int i =  invFormConfigService.isSameConfig(invFormConfig);
        if( i  > 0){
            return AjaxResult.error("参数键名需为唯一值");
        }else {
           invFormConfigService.updateInvFormConfig(invFormConfig);
           return AjaxResult.success(invFormConfigService.selectInvFormConfigByKey(invFormConfig.getConfigKey()));
        }
    }

    /**
     * 删除单据配置
     */
    @ApiOperation("删除")
   // @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "单据配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        return toAjax(invFormConfigService.deleteInvFormConfigByIds(configIds));
    }
}
