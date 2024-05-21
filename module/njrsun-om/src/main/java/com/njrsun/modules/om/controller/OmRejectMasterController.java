package com.njrsun.modules.om.controller;

import java.util.List;
import java.util.Map;

import com.njrsun.common.annotation.DataScope;
import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.service.IOmDeliverMasterService;
import com.njrsun.modules.om.service.impl.OmOrderMasterServiceImpl;
import com.njrsun.modules.om.domain.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.modules.om.service.IOmRejectMasterService;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 销售订单主Controller
 * 
 * @author njrsun
 * @date 2021-08-31
 */
@Api(tags = "退货通知单")
@RestController
@RequestMapping("/om/reject")
public class OmRejectMasterController extends BaseController
{

    @Autowired
    private IOmRejectMasterService omRejectMasterService;

    @Autowired
    private OmOrderMasterServiceImpl omOrderMasterService;

    @Autowired
    private IOmDeliverMasterService omDeliverMasterService;

    /**
     * 查询销售订单主列表
     */
    @PreAuthorize("@ss.hasPermi('om:reject:list')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @Log(title = "查询退货订单", businessType = BusinessType.SELECT)
    @ApiOperation("列表")
    @GetMapping("/list")
    public TableDataInfo list(OmRejectMaster omRejectMaster)
    {
        startPage();
        List<OmRejectMaster> list = omRejectMasterService.selectOmRejectMasterList(omRejectMaster);
        return getDataTable(list);
    }

    /**
     * 导出销售订单主列表
     */
    @PreAuthorize("@ss.hasPermi('om:reject:export')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @Log(title = "导出退货订单", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(OmRejectMaster omRejectMaster)
    {
        List<ExportReject> list = omRejectMasterService.export(omRejectMaster);
        ExcelUtil<ExportReject> util = new ExcelUtil<ExportReject>(ExportReject.class);
        return util.exportExcel(list, "销售退货单");
    }

    /**
     * 获取销售订单主详细信息
     */
    @PreAuthorize("@ss.hasPermi('om:reject:entity:view')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @Log(title = "查询退货订单", businessType = BusinessType.SELECT)
    @ApiOperation("详情")
    @GetMapping
    public AjaxResult getInfo(OmRejectMaster omRejectMaster)
    {
        return AjaxResult.success(omRejectMasterService.selectOmRejectMasterByCode(omRejectMaster));
    }

    /**
     * 新增销售订单主表
     */
    @PreAuthorize("@ss.hasPermi('om:reject:entity:add')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @Log(title = "新增退货订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OmRejectMaster omRejectMaster)
    {
        int i = omRejectMasterService.insertOmRejectMaster(omRejectMaster);
        if(i != 0){
            return AjaxResult.success(omRejectMasterService.selectOmRejectMasterByCode(omRejectMaster));
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 修改销售订单主表
     */
    @PreAuthorize("@ss.hasPermi('om:reject:entity:edit')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @Log(title = "修改退货订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OmRejectMaster omRejectMaster)
    {
        int i = omRejectMasterService.updateOmRejectMaster(omRejectMaster);
        if(i != 0){
            return AjaxResult.success(omRejectMasterService.selectOmRejectMasterByCode(omRejectMaster));
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 删除销售订单主
     */
    @PreAuthorize("@ss.hasPermi('om:reject:remove')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @Log(title = "删除退货订单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<OmRejectMaster> list)
    {

        return toAjax(omRejectMasterService.deleteOmRejectMasterByCodes(list));
    }


    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @PreAuthorize("@ss.hasPermi('om:reject:detail')")
    @GetMapping("/getDetail")
    public TableDataInfo getDetail(OmRejectMaster omRejectMaster){
        startPage();
       List<Map<String,String>>  list =  omRejectMasterService.getDetail(omRejectMaster);
       return getDataTable(list);
    }

    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @Log(title = "审核退货订单", businessType = BusinessType.PERMIT)
    @PreAuthorize("@ss.hasPermi('om:reject:permit')")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody  List<OmRejectMaster> list){
        OmRejectMaster omRejectMaster = omRejectMasterService.batchCheck(list);
        return AjaxResult.success(omRejectMaster);
    }

    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @Log(title = "反审核退货订单", businessType = BusinessType.REVOKE)
    @PreAuthorize("@ss.hasPermi('om:reject:revoke')")
    @PostMapping("/antiCheck")
    public AjaxResult antiCheck(@RequestBody  List<OmRejectMaster> list){
        OmRejectMaster omRejectMaster = omRejectMasterService.batchAntiCheck(list);
        return AjaxResult.success(omRejectMaster);
    }

    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @PreAuthorize("@ss.hasPermi('om:reject:next')")
    @GetMapping("/nextOrLast")
    public AjaxResult getNextOrLast(OmRejectMaster omRejectMaster){
        OmRejectMaster om =  omRejectMasterService.gettNextOrLast(omRejectMaster);
        return  AjaxResult.success(om);
    }

    @PreAuthorize("@ss.hasPermi('om:reject:chain')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @ApiOperation("关联明细查询")
    @GetMapping("/chain")
    public TableDataInfo chainDetail(OmRejectSalve omRejectSalve){
        startPage();
        List<Map<String,String>> list = omRejectMasterService.chainDetail(omRejectSalve);
        return getDataTable(list);
    }
    @PreAuthorize("@ss.hasPermi('om:report:export')")
    @ApiOperation("退货明细报表")
    @GetMapping("/report")
    public TableDataInfo rejectReport(OmRejectReport omRejectReport){
        startPage();
       List<OmRejectReport> list  =  omRejectMasterService.rejectReport(omRejectReport);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('om:reject:export')")
    @ApiOperation("退货明细报表导出")
    @GetMapping("/report/export")
    public AjaxResult reportExport(OmRejectReport omRejectReport){
        List<OmRejectReport> list  =  omRejectMasterService.rejectReport(omRejectReport);
        ExcelUtil<OmRejectReport> util = new ExcelUtil<>(OmRejectReport.class);
        return util.exportExcel(list, "退货订单关联明细");
    }


    @Log(title = "挂起退货订单", businessType = BusinessType.HANG)
    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @PreAuthorize("@ss.hasPermi('om:reject:operate')")
    @ApiOperation("更新业务状态")
    @PostMapping("/operate")
     public AjaxResult changeWorkStatus(@RequestBody OmRejectMaster omRejectMaster){
        omRejectMasterService.changeWorkStatus(omRejectMaster);
        return AjaxResult.success();
    }

    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @PreAuthorize("@ss.hasPermi('om:reject:import')")
    @ApiOperation("引入")
    @GetMapping("/leadInto")
    public TableDataInfo leadInto(OmDeliverMaster omDeliverMaster){
         startPage();
         List<Map<String,String>> map = omDeliverMasterService.leadInto(omDeliverMaster);
         return getDataTable(map);
    }
}
