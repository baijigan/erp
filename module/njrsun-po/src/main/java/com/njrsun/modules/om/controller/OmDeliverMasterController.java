package com.njrsun.modules.om.controller;


import java.util.List;
import java.util.Map;

import com.njrsun.common.annotation.DataScope;
import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.domain.ExportDeliver;
import com.njrsun.modules.om.domain.OmDeliverMaster;
import com.njrsun.modules.om.domain.OmDeliverReport;
import com.njrsun.modules.om.domain.OmDeliverSalve;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.modules.om.service.IOmDeliverMasterService;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 销售订单主Controller
 * 
 * @author njrsun
 * @date 2021-08-31
 */
@RestController
@Api(tags = "发货通知单")
@RequestMapping("/om/deliver")
public class OmDeliverMasterController extends BaseController
{

    @Autowired
    private IOmDeliverMasterService omDeliverMasterService;

    /**
     * 查询销售订单主列表
     */
   @PreAuthorize("@ss.hasPermi('om:deliver:list')")
    @ApiOperation("列表")
    @DataScope(roleAlias = "om_oper",workAlias = "om_deliver_type")
    @Log(title = "查询发货订单", businessType = BusinessType.SELECT)
    @GetMapping("/list")
    public TableDataInfo list(OmDeliverMaster omDeliverMaster)
    {
        startPage();
        List<OmDeliverMaster> list = omDeliverMasterService.selectOmDeliverMasterList(omDeliverMaster);
        return getDataTable(list);
    }


    /**
     * 导出销售订单主列表
     */
    @PreAuthorize("@ss.hasPermi('om:deliver:export')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_deliver_type")
    @Log(title = "导出发货订单", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(OmDeliverMaster omDeliverMaster)
    {
        List<ExportDeliver> list = omDeliverMasterService.export(omDeliverMaster);
        ExcelUtil<ExportDeliver> util = new ExcelUtil<>(ExportDeliver.class);
        return util.exportExcel(list, "销售发货单");
    }


    /**
     * 获取销售订单主详细信息
     */
   @PreAuthorize("@ss.hasPermi('om:deliver:entity:view')")
   @DataScope(roleAlias = "om_oper",workAlias = "om_deliver_type")
   @Log(title = "查询发货订单", businessType = BusinessType.SELECT)
    @ApiOperation("详情")
    @GetMapping
    public AjaxResult getInfo(OmDeliverMaster omDeliverMaster)
    {
        return AjaxResult.success(omDeliverMasterService.selectOmDeliverMasterByCode(omDeliverMaster));
    }

    /**
     * 新增销售订单主
     */
    @PreAuthorize("@ss.hasPermi('om:deliver:entity:add')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_deliver_type")
    @ApiOperation("新增")
    @Log(title = "新增发货订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OmDeliverMaster omDeliverMaster)
    {
        omDeliverMasterService.insertOmDeliverMaster(omDeliverMaster);
        return AjaxResult.success(omDeliverMasterService.selectOmDeliverMasterByCode(omDeliverMaster));
    }

    /**
     * 修改销售订单主
     */
    @PreAuthorize("@ss.hasPermi('om:deliver:entity:edit')")
    @ApiOperation("修改")
    @DataScope(roleAlias = "om_oper",workAlias = "om_deliver_type")
    @Log(title = "修改发货订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OmDeliverMaster omDeliverMaster)
    {
        omDeliverMasterService.updateOmDeliverMaster(omDeliverMaster);

        return AjaxResult.success(omDeliverMasterService.selectOmDeliverMasterByCode(omDeliverMaster));

    }

    /**
     * 删除销售订单主
     */
   @PreAuthorize("@ss.hasPermi('om:deliver:remove')")
    @ApiOperation("删除")
    @DataScope(roleAlias = "om_oper",workAlias = "om_deliver_type")
    @Log(title = "删除发货订单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<OmDeliverMaster> list)
    {
        return toAjax(omDeliverMasterService.deleteOmDeliverMasterByCodes(list));
    }

    @PreAuthorize("@ss.hasPermi('om:deliver:next')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_deliver_type")
    @ApiOperation("下一条")
    @GetMapping("/nextOrLast")
    public AjaxResult getNextOrLast(OmDeliverMaster omDeliverMaster){
        OmDeliverMaster nextOrLast = omDeliverMasterService.getNextOrLast(omDeliverMaster);
        return AjaxResult.success(nextOrLast);

    }

    @PreAuthorize("@ss.hasPermi('om:deliver:permit')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_deliver_type")
    @Log(title = "审核发货订单", businessType = BusinessType.PERMIT)
    @ApiOperation("审核")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody List<OmDeliverMaster> list){
        OmDeliverMaster omDeliverMaster = omDeliverMasterService.batchCheck(list);
        return AjaxResult.success(omDeliverMaster);
    }

    @PreAuthorize("@ss.hasPermi('om:deliver:revoke')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_deliver_type")
    @Log(title = "反审核发货订单", businessType = BusinessType.REVOKE)
    @ApiOperation("反审核")
    @PostMapping("/antiCheck")
    public AjaxResult antiCheck(@RequestBody  List<OmDeliverMaster> list){
        OmDeliverMaster omDeliverMaster = omDeliverMasterService.batchAntiCheck(list);
        return AjaxResult.success(omDeliverMaster);
    }

    @PreAuthorize("@ss.hasPermi('om:deliver:detail')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_deliver_type")
    @ApiOperation("明细")
    @GetMapping("/getDetail")
    public TableDataInfo getDetail(OmDeliverMaster omDeliverMaster){
        startPage();
        List<Map<String,String>>  list =   omDeliverMasterService.getDetail(omDeliverMaster);
        return  getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('om:deliver:chain')")
    @ApiOperation("关联明细查询")
    @GetMapping("/chain")
    public TableDataInfo chainDetail(OmDeliverSalve omDeliverSalve){
        startPage();
        List<Map<String, String>> maps = omDeliverMasterService.chainDetail(omDeliverSalve);
        return getDataTable(maps);
    }

    @PreAuthorize("@ss.hasPermi('om:report:detail')")
    @ApiOperation("发货单关联明细")
    @GetMapping("/report")
    public TableDataInfo reportDeliver(OmDeliverReport omDeliverReport){
        startPage();
       List<OmDeliverReport> list =   omDeliverMasterService.reportDeliver(omDeliverReport);
       return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('om:report:export')")
    @ApiOperation("导出销售明细报表")
    @GetMapping("/report/export")
    public AjaxResult  orderReportExport(OmDeliverReport omDeliverReport){
        List<OmDeliverReport> list =   omDeliverMasterService.reportDeliver(omDeliverReport);
        ExcelUtil<OmDeliverReport> util = new ExcelUtil<>(OmDeliverReport.class);
        return util.exportExcel(list, "发货订单关联明细");

    }

    @ApiOperation("更新业务状态")
    @Log(title = "挂起发货订单", businessType = BusinessType.HANG)
    @DataScope(roleAlias = "om_oper",workAlias = "om_deliver_type")
    @PreAuthorize("@ss.hasPermi('om:deliver:operate')")
    @PostMapping("/operate")
    public AjaxResult changeWorkStatus(@RequestBody OmDeliverMaster omDeliverMaster){
        omDeliverMasterService.changeWorkStatus(omDeliverMaster);
        return AjaxResult.success(omDeliverMasterService.selectOmDeliverMasterByCode(omDeliverMaster));
    }

}
