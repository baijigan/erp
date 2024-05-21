package com.njrsun.modules.om.controller;

import com.alibaba.fastjson.JSON;
import com.njrsun.common.annotation.DataScope;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.core.page.TableDataInfo;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.modules.om.domain.ExportFapiao;
import com.njrsun.modules.om.domain.OmFapiaoMaster;
import com.njrsun.modules.om.domain.OmFapiaoReport;
import com.njrsun.modules.om.domain.OmOrderSalve;
import com.njrsun.modules.om.service.IOmFapiaoMasterService;
import com.njrsun.modules.om.service.IOmOrderMasterService;
import com.njrsun.api.wm.service.IWmOutApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 销售开票主Controller
 * 
 * @author njrsun
 * @date 2022-04-04
 */
@RestController
@Api(tags =  "销售开票")
@RequestMapping("/om/fapiao")
public class OmFapiaoMasterController extends BaseController
{
    @Autowired
    private IOmFapiaoMasterService omFapiaoMasterService;

    @Autowired
    private IOmOrderMasterService omOrderMasterService;

    @Autowired
    private IWmOutApiService wmOutApiService;

    /**
     * 查询销售开票主列表
     */
    @PreAuthorize("@ss.hasPermi('om:fapiao:list')")
    @ApiOperation("列表")
    @GetMapping("/list")
    public TableDataInfo list(OmFapiaoMaster omFapiaoMaster)
    {
        startPage();
        List<OmFapiaoMaster> list = omFapiaoMasterService.selectOmFapiaoMasterList(omFapiaoMaster);
        return getDataTable(list);
    }

    /**
     * 导出销售开票主列表
     */
    @PreAuthorize("@ss.hasPermi('om:fapiao:export')")
    @Log(title = "导出销售开票", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(OmFapiaoMaster omFapiaoMaster)
    {
        List<ExportFapiao> list = omFapiaoMasterService.export(omFapiaoMaster);
        ExcelUtil<ExportFapiao> util = new ExcelUtil<>(ExportFapiao.class);
        return util.exportExcel(list, "销售开票通知单");
    }

    /**
     * 获取销售开票主详细信息
     */
    @PreAuthorize("@ss.hasPermi('om:fapiao:query')")
    @ApiOperation("详情")
    @GetMapping
    public AjaxResult getInfo(@RequestParam String omCode)
    {
        return AjaxResult.success(omFapiaoMasterService.selectOmFapiaoMasterById(omCode));
    }

    /**
     * 新增销售开票主
     */
    @PreAuthorize("@ss.hasPermi('om:fapiao:add')")
    @Log(title = "新增销售开票", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OmFapiaoMaster omFapiaoMaster)
    {
        omFapiaoMasterService.insertOmFapiaoMaster(omFapiaoMaster);

        return AjaxResult.success(omFapiaoMasterService.selectOmFapiaoMasterById(omFapiaoMaster.getOmCode()));
    }

    /**
     * 修改销售开票主
     */
    @PreAuthorize("@ss.hasPermi('om:fapiao:edit')")
    @Log(title = "修改销售开票", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OmFapiaoMaster omFapiaoMaster)
    {
        omFapiaoMasterService.updateOmFapiaoMaster(omFapiaoMaster);
        return AjaxResult.success(omFapiaoMasterService.selectOmFapiaoMasterById(omFapiaoMaster.getOmCode()));
    }

    /**
     * 删除销售开票主
     */
    @PreAuthorize("@ss.hasPermi('om:fapiao:remove')")
    @Log(title = "销售开票主", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<OmFapiaoMaster> list)
    {
        List<String> collect = list.stream().map(OmFapiaoMaster::getOmCode).collect(Collectors.toList());
        return toAjax(omFapiaoMasterService.deleteOmFapiaoMasterByIds(collect));
    }

    @ApiOperation("审核")
    @DataScope(roleAlias = "om_oper",workAlias = "om_fapiao_type")
    //   @PreAuthorize("@ss.hasPermi('po:fapiao:permit')")
    @PostMapping("/batchCheck")
    public AjaxResult check(@RequestBody List<OmFapiaoMaster> list){
        OmFapiaoMaster check = omFapiaoMasterService.check(list);
        return AjaxResult.success(check);
    }

    @ApiOperation("反审核")
    @DataScope(roleAlias = "om_oper",workAlias = "om_fapiao_type")
    // @PreAuthorize("@ss.hasPermi('po:fapiao:revoke')")
    @PostMapping("/antiBatchCheck")
    public AjaxResult antiCheck(@RequestBody List<OmFapiaoMaster> list){
        OmFapiaoMaster check = omFapiaoMasterService.antiCheck(list);
        return AjaxResult.success(check);
    }

    @ApiOperation("入账")
    @DataScope(roleAlias = "om_oper",workAlias = "om_fapiao_type")
    @PreAuthorize("@ss.hasPermi('om:fapiao:entry')")
    @PostMapping("/batchEntry")
    public AjaxResult entry(@RequestBody List<OmFapiaoMaster> list){
        OmFapiaoMaster entry = omFapiaoMasterService.entry(list);
        return AjaxResult.success(entry);
    }

    @ApiOperation("反入账")
    @DataScope(roleAlias = "om_oper",workAlias = "om_fapiao_type")
    @PreAuthorize("@ss.hasPermi('om:fapiao:entry')")
    @PostMapping("/antiBatchEntry")
    public AjaxResult antiEntry(@RequestBody List<OmFapiaoMaster> list){
        OmFapiaoMaster entry = omFapiaoMasterService.antiEntry(list);
        return AjaxResult.success(entry);
    }

    @ApiOperation("引入")
    @GetMapping("/leadInto")
    public TableDataInfo leadInto(@RequestParam Map<String,String> query){
        startPage();
        List<Map<String,String>> re =  wmOutApiService.leadIntoOmfaPiao(query);
        re.forEach(r ->{
            OmOrderSalve salve= new OmOrderSalve();
            salve.setOmCode(r.get("pp_number"));
            salve.setInvCode(r.get("inv_code"));
            OmOrderSalve omOrderSalve= omOrderMasterService.selectOrderSalveBySalve(salve);
            if(omOrderSalve!=null)r.put("price", omOrderSalve.getPrice().toString());
            else r.put("price", "0.0");
        });

        return getDataTable(re);
    }

    @DataScope(roleAlias = "po_oper",workAlias = "om_fapiao_type")
    //  @PreAuthorize("@ss.hasPermi('po:order:next')")
    @ApiOperation("上下一条")
    @GetMapping("/nextOrLast")
    public AjaxResult nextOrLast(OmFapiaoMaster omFapiaoMaster){
        String s = JSON.toJSONString(omFapiaoMaster);
        OmFapiaoMaster poFapiaoMaster1 = JSON.parseObject(s, OmFapiaoMaster.class);
        poFapiaoMaster1.setOmCode(null);
        String poCode = omFapiaoMaster.getOmCode();
        Boolean type = omFapiaoMaster.getType();
        List<OmFapiaoMaster> omFapiaoMaster1 = omFapiaoMasterService.selectOmFapiaoMasterList(poFapiaoMaster1);
        int i1 = omFapiaoMaster1.size() - 1;
        int flag  = 0;
        for (int i = 0; i < omFapiaoMaster1.size(); i++) {
            if(omFapiaoMaster1.get(i).getOmCode().equals(poCode)){
                flag = i;
            }
        }
        if((flag == 0 && type)  ||  (flag==i1 &&  !type)){
            throw  new CommonException("已经是最后一条单据了");
        }
        else if(type){
            return AjaxResult.success(omFapiaoMasterService.selectOmFapiaoMasterById(omFapiaoMaster1.get(flag-1).getOmCode()) );
        }
        else{
            return AjaxResult.success( omFapiaoMasterService.selectOmFapiaoMasterById(omFapiaoMaster1.get(flag+1).getOmCode()) );
        }
    }

    @PreAuthorize("@ss.hasAnyPermi('om:fapiao:detail,om:fapiao:import')")
    @DataScope(roleAlias = "om_oper",workAlias = "om_fapiao_type")
    @ApiOperation("查询单据明细")
    @GetMapping("/getDetail")
    public TableDataInfo getDetail(OmFapiaoMaster omFapiaoMaster){
        startPage();
        List<Map<String,String>>  list =   omFapiaoMasterService.getDetail(omFapiaoMaster);
        return  getDataTable(list);
    }

    @ApiOperation("更新业务状态")
    @Log(title = "挂起发票订单", businessType = BusinessType.HANG)
    @DataScope(roleAlias = "om_oper",workAlias = "om_reject_type")
    @PreAuthorize("@ss.hasPermi('om:fapiao:operate')")
    @PostMapping("/operate")
    public AjaxResult changeWorkStatus(@RequestBody OmFapiaoMaster omFapiaoMaster){
        omFapiaoMasterService.changeWorkStatus(omFapiaoMaster);
        return AjaxResult.success(omFapiaoMasterService.selectOmFapiaoMasterByCode(omFapiaoMaster));
    }

    @PreAuthorize("@ss.hasPermi('om:report:detail')")
    @ApiOperation("开票明细报表")
    @GetMapping("/report")
    public TableDataInfo  fapiaoReport(OmFapiaoReport omFapiaoReport){
        startPage();
        List<OmFapiaoReport> list =  omFapiaoMasterService.fapiaoReport(omFapiaoReport);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('om:order:export')")
    @ApiOperation("导出开票明细报表")
    @GetMapping("/report/export")
    public AjaxResult  fapiaoReportExport(OmFapiaoReport omFapiaoReport){
        List<OmFapiaoReport> list =  omFapiaoMasterService.fapiaoReport(omFapiaoReport);
        ExcelUtil<OmFapiaoReport> util = new ExcelUtil<>(OmFapiaoReport.class);
        return util.exportExcel(list, "销售开票明细报表");
    }

}
