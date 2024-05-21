package com.njrsun.modules.om.controller;

import java.util.*;

import com.njrsun.common.annotation.DataScope;
import com.njrsun.common.core.domain.entity.SysDictData;
import com.njrsun.modules.om.constant.OmConstant;
import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.domain.*;
import com.njrsun.modules.om.service.IOmOrderMasterService;
import com.njrsun.system.service.ISysDictTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 销售订单Controller
 * 
 * @author njrsun
 * @date 2021-08-28
 */
@Api(tags = "销售订单")
@RestController
@RequestMapping("/om/order")
public class OmOrderMasterController extends BaseController
{
    @Autowired
    private IOmOrderMasterService omOrderMasterService;

    @Autowired
    private ISysDictTypeService iSysDictTypeService;

    /**
     * 查询销售订单列表
     */
    @PreAuthorize("@ss.hasPermi('om:order:list')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @Log(title = "查询销售订单", businessType = BusinessType.SELECT)
    @ApiOperation("查询列表")
    @GetMapping("/list")
    public TableDataInfo list(OmOrderMaster omOrderMaster)
    {
        startPage();
        List<OmOrderMaster> list = omOrderMasterService.selectOmOrderMasterList(omOrderMaster);
        return getDataTable(list);
    }

    /**
     * 导出销售订单列表
     */
    @PreAuthorize("@ss.hasPermi('om:order:export')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @Log(title = "导出销售订单", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(OmOrderMaster omOrderMaster)
    {
        List<ExportOrder> list = omOrderMasterService.export(omOrderMaster);
        ExcelUtil<ExportOrder> util = new ExcelUtil<>(ExportOrder.class);
        return util.exportExcel(list, "销售订单");
    }

    /**
     * 获取销售订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('om:order:entity:view')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @Log(title = "查询销售订单", businessType = BusinessType.SELECT)
    @ApiOperation("获取详情")
    @GetMapping
    public AjaxResult getInfo(OmOrderMaster omOrderMaster)
    {
        return AjaxResult.success(omOrderMasterService.selectOmOrderMasterByCode(omOrderMaster));
    }

    /**
     * 新增销售订单
     */
   @PreAuthorize("@ss.hasPermi('om:order:entity:add')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @ApiOperation("新增销售订单")
    @Log(title = "销售销售订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OmOrderMaster omOrderMaster)
    {
        int i = omOrderMasterService.insertOmOrderMaster(omOrderMaster);
        if(i !=0){
            return AjaxResult.success(omOrderMasterService.selectOmOrderMasterByCode(omOrderMaster));
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 修改销售订单
     */
    @PreAuthorize("@ss.hasPermi('om:order:entity:edit')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @ApiOperation("修改订单")
    @Log(title = "修改销售订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OmOrderMaster omOrderMaster)
    {
        omOrderMasterService.updateOmOrderMaster(omOrderMaster);
        return AjaxResult.success(omOrderMasterService.selectOmOrderMasterByCode(omOrderMaster));
    }

    /**
     * 删除销售订单
     */
    @PreAuthorize("@ss.hasPermi('om:order:remove')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @ApiOperation("删除订单")
    @Log(title = "删除销售订单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<OmOrderMaster> list)
    {
        return toAjax(omOrderMasterService.deleteOmOrderMasterByCodes(list));
    }

    @PreAuthorize("@ss.hasPermi('om:order:next')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @ApiOperation("下一条")
    @GetMapping("/nextOrLast")
    public AjaxResult getNextOrLast(OmOrderMaster omOrderMaster){
        OmOrderMaster nextOrLast = omOrderMasterService.getNextOrLast(omOrderMaster);
        return AjaxResult.success(nextOrLast);
    }

    @PreAuthorize("@ss.hasAnyPermi('om:order:detail,om:deliver:import')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @ApiOperation("查询单据明细")
    @GetMapping("/getDetail")
    public TableDataInfo getDetail(OmOrderMaster omOrderMaster){
        startPage();
        List<Map<String,String>>  list =   omOrderMasterService.getDetail(omOrderMaster);
        return  getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('om:order:list')")
    @ApiOperation("根据明细Id查总表")
    @GetMapping("/query/{uniqueId}")
    public AjaxResult query(@PathVariable("uniqueId") Long uniqueId){
        OmOrderMaster omOrderMaster =  omOrderMasterService.query(uniqueId);
            return AjaxResult.success(omOrderMaster);
    }

    @PreAuthorize("@ss.hasPermi('om:order:permit')")
    @Log(title = "审核销售订单", businessType = BusinessType.PERMIT)
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @ApiOperation("审核")
    @PostMapping("/check")
    public AjaxResult batchCheck(@RequestBody List<OmOrderMaster> list){
        omOrderMasterService.check(list);
        OmOrderMaster omOrderMaster = omOrderMasterService.batchCheck(list);
        return AjaxResult.success(omOrderMaster);
    }


    @PreAuthorize("@ss.hasPermi('om:order:revoke')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @Log(title = "反审核销售订单", businessType = BusinessType.REVOKE)
    @ApiOperation("反审核")
    @PostMapping("/antiCheck")
    public AjaxResult batchAntiCheck(@RequestBody List<OmOrderMaster> list){
        omOrderMasterService.antiCheck(list);
        OmOrderMaster omOrderMaster = omOrderMasterService.batchAntiCheck(list);
        return AjaxResult.success(omOrderMaster);
    }

    @PreAuthorize("@ss.hasPermi('om:order:chain')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @ApiOperation("关联明细查询")
    @GetMapping("/chain")
    public TableDataInfo chainDetail(OmOrderSalve omOrderSalve){
        startPage();
        List<Map<String,String>> list = omOrderMasterService.chainDetail(omOrderSalve);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('om:report:detail')")
    @ApiOperation("销售明细报表")
    @GetMapping("/report")
    public TableDataInfo  orderReport(OmOrderReport omOrderReport){
        startPage();
       List<OmOrderReport> list =  omOrderMasterService.orderReport(omOrderReport);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('om:order:export')")
    @ApiOperation("导出销售明细报表")
    @GetMapping("/report/export")
    public AjaxResult  orderReportExport(OmOrderReport omOrderReport){
        List<OmOrderReport> list =  omOrderMasterService.orderReport(omOrderReport);
        ExcelUtil<OmOrderReport> util = new ExcelUtil<>(OmOrderReport.class);
        return util.exportExcel(list, "销售订单关联明细");
    }


    @PreAuthorize("@ss.hasPermi('om:report:detail')")
    @ApiOperation("产品销售排名")
    @GetMapping("/rank")
    public TableDataInfo rank(OmOrderRank omOrderRank){
      startPage();
      List<OmOrderRank> rank =   omOrderMasterService.rank(omOrderRank);
      return getDataTable(rank);
    }


    @PreAuthorize("@ss.hasPermi('om:report:detail')")
    @ApiOperation("导出产品销售排名")
    @GetMapping("/rank/export")
    public AjaxResult rankReport(OmOrderRank omOrderRank){
        List<OmOrderRank> rank =   omOrderMasterService.rank(omOrderRank);
        ExcelUtil<OmOrderRank> util = new ExcelUtil<>(OmOrderRank.class);
        return util.exportExcel(rank, "产品销售排名");
    }

    @PreAuthorize("@ss.hasPermi('om:report:detail')")
    @ApiOperation("销售员业绩统计表")
    @GetMapping("/statistics")
    public TableDataInfo statistics(OmWorkStaffStatistics omWorkStaffStatistics){
        startPage();
       List<OmWorkStaffStatistics> list =   omOrderMasterService.statistics(omWorkStaffStatistics);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('om:report:detail')")
    @ApiOperation("销售员业绩统计表")
    @GetMapping("/statistics/export")
    public AjaxResult statisticsExport(OmWorkStaffStatistics omWorkStaffStatistics){
        List<OmWorkStaffStatistics> list =   omOrderMasterService.statistics(omWorkStaffStatistics);
        ExcelUtil<OmWorkStaffStatistics> util = new ExcelUtil<>(OmWorkStaffStatistics.class);
        return util.exportExcel(list, "销售员业绩统计表");
    }

    @ApiOperation("更新业务状态")
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @Log(title = "挂起销售订单", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('om:order:operate')")
    @PostMapping("/operate")
    public AjaxResult changeWorkStatus(@RequestBody OmOrderMaster omOrderMaster){
        omOrderMasterService.changeWorkStatus(omOrderMaster);
        return AjaxResult.success(omOrderMasterService.selectOmOrderMasterByCode(omOrderMaster));
    }


    @ApiOperation("更新交付")
    @DataScope(roleAlias = "om_oper",workAlias = "om_order_type")
    @Log(title = "挂起销售订单", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('om:order:operate')")
    @PostMapping("/operate/changeDate")
    public AjaxResult changeDate(@RequestBody OmOrderMaster omOrderMaster){
        omOrderMasterService.changeDate(omOrderMaster);
        return AjaxResult.success(omOrderMasterService.selectOmOrderMasterByCode(omOrderMaster));
    }

    @ApiOperation("根据供货方式查询销售单")
    @Log(title = "根据供货方式查询销售单", businessType = BusinessType.HANG)
   // @PreAuthorize("@ss.hasPermi('om:order:changeStatus')")
    @GetMapping("/supplyTypeList")
    public TableDataInfo supplyTypeList(@RequestParam  Map<String,Object> map){
        startPage();
            List<Map<String,Object>>  list =   omOrderMasterService.selecDetailByWorkType(map);
            return getDataTable(list);

    }

    @GetMapping("/pipe/orderId")
    @PreAuthorize("@ss.hasPermi('om:order:pipe:orderid')")
    public AjaxResult ppNumber(@RequestParam("ppNumber") String ppNumber){
        OmOrderMaster omOrderMaster = new OmOrderMaster();
        omOrderMaster.setOmCode(ppNumber);
        if(!ppNumber.equals("")){
            OmOrderMaster omOrderMaster1 = omOrderMasterService.selectOmOrderMasterByCode(omOrderMaster);
            List<SysDictData> sysDictData = iSysDictTypeService.selectDictDataByType(OmConstant.ORDER);
            for (SysDictData sysDictDatum : sysDictData) {
                if(sysDictDatum.getDictValue().equals(omOrderMaster1.getWorkType()))
                omOrderMaster1.setWorkType(sysDictDatum.getDictLabel());
            }
            return AjaxResult.success(omOrderMaster1);
        }
        return AjaxResult.success();
    }


    @ApiOperation("销售订单明细表")
    @PreAuthorize("@ss.hasPermi('om:report:detail')")
    @GetMapping("/orderDetail")
    public TableDataInfo orderDetail(@RequestParam String supplyType){
             startPage();
            List<OmOrderDetail>  list =  omOrderMasterService.orderDetail(supplyType);
            return getDataTable(list);
    }

    @ApiOperation("销售订单明细表导出")
    @PreAuthorize("@ss.hasPermi('om:report:export')")
    @GetMapping("/orderDetail/export")
    public AjaxResult orderDetailExport(@RequestParam String supplyType){
        List<OmOrderDetail>  list =  omOrderMasterService.orderDetail(supplyType);
        ExcelUtil<OmOrderDetail> excelUtil = new ExcelUtil<>(OmOrderDetail.class);
       return   excelUtil.exportExcel(list,"销售订单明细表");

    }

    @ApiOperation("销售订单计划表")
    @PreAuthorize("@ss.hasPermi('om:report:detail')")
    @GetMapping("/orderDetail/mp")
    public TableDataInfo orderDetailMp(OmOrderMpReport omOrderMpReport){
        startPage();
        List<OmOrderMpReport>  list =  omOrderMasterService.orderDetailMp(omOrderMpReport);
        return getDataTable(list);
    }

    @ApiOperation("销售订单计划表")
    @PreAuthorize("@ss.hasPermi('om:report:detail')")
    @GetMapping("/orderDetail/mp/export")
    public AjaxResult orderDetailMpExport(OmOrderMpReport omOrderMpReport){

        List<OmOrderMpReport>  list =  omOrderMasterService.orderDetailMp(omOrderMpReport);
        ExcelUtil<OmOrderMpReport> excelUtil = new ExcelUtil<>(OmOrderMpReport.class);
       return   excelUtil.exportExcel(list,"销售订单计划表");
    }

}
