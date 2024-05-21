package com.njrsun.modules.prs.controller;

import java.util.List;
import java.util.Map;

import com.njrsun.common.annotation.DataScope;
import com.njrsun.common.exception.CommonException;
import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.domain.PrsCheckinExport;
import com.njrsun.modules.prs.domain.PrsCheckinMaster;
import com.njrsun.modules.prs.domain.PrsCheckinReport;
import com.njrsun.modules.prs.service.IPrsProductMasterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.modules.prs.service.IPrsCheckinMasterService;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 生产入库单主Controller
 * 
 * @author njrsun
 * @date 2022-01-18
 */
@RestController
@Api(tags = "生产入库单")
@RequestMapping("/prs/checkin")
public class PrsCheckinMasterController extends BaseController
{
        @Autowired
        private IPrsCheckinMasterService prsCheckinMasterService;

        @Autowired
        private IPrsProductMasterService prsProductMasterService;

    /**
     * 查询生产入库单主列表
     */
    @ApiOperation("列表")
    @PreAuthorize("@ss.hasPermi('prs:checkin:list')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_checkin_type")
    @GetMapping("/list")
    public TableDataInfo list(PrsCheckinMaster prsCheckinMaster)
    {
        startPage();
        List<PrsCheckinMaster> list = prsCheckinMasterService.selectPrsCheckinMasterList(prsCheckinMaster);
        return getDataTable(list);
    }

    /**
     * 导出生产入库单主列表
     */
    @PreAuthorize("@ss.hasPermi('prs:checkin:export')")
    @Log(title = "生产入库单主", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PrsCheckinMaster prsCheckinMaster)
    {
        List<PrsCheckinMaster> list = prsCheckinMasterService.selectPrsCheckinMasterList(prsCheckinMaster);
        ExcelUtil<PrsCheckinMaster> util = new ExcelUtil<PrsCheckinMaster>(PrsCheckinMaster.class);
        return util.exportExcel(list, "checkin");
    }

    /**
     * 获取生产入库单主详细信息
     */
    @ApiOperation("详情")
    @PreAuthorize("@ss.hasPermi('prs:checkin:entity:view')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_checkin_type")
    @GetMapping
    public AjaxResult getInfo(PrsCheckinMaster prsCheckinMaster)
    {
        return AjaxResult.success(prsCheckinMasterService.selectPrsCheckinMasterById(prsCheckinMaster));
    }

    /**
     * 新增生产入库单主
     */
    @ApiOperation("新增")
    @PreAuthorize("@ss.hasPermi('prs:checkin:entity:add')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_checkin_type")
    @Log(title = "生产入库单主", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsCheckinMaster prsCheckinMaster)
    {
        prsCheckinMasterService.insertPrsCheckinMaster(prsCheckinMaster);
        return AjaxResult.success(prsCheckinMasterService.selectPrsCheckinMasterById(prsCheckinMaster));


    }

    /**
     * 修改生产入库单主
     */
    @ApiOperation("修改")
    @PreAuthorize("@ss.hasPermi('prs:checkin:entity:edit')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_checkin_type")
    @Log(title = "生产入库单主", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsCheckinMaster prsCheckinMaster)
    {
        prsCheckinMasterService.updatePrsCheckinMaster(prsCheckinMaster);

        return AjaxResult.success(prsCheckinMasterService.selectPrsCheckinMasterById(prsCheckinMaster));
    }

    /**
     * 删除生产入库单主
     */
    @ApiOperation("删除")
    @PreAuthorize("@ss.hasPermi('prs:checkin:remove')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_checkin_type")
    @Log(title = "生产入库单主", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody   List<PrsCheckinMaster> list)
    {
        return toAjax(prsCheckinMasterService.deletePrsCheckinMasterByIds(list));
    }


    @PreAuthorize("@ss.hasPermi('prs:checkin:permit')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_checkin_type")
    @Log(title = "审核订单", businessType = BusinessType.PERMIT)
    @ApiOperation("审核")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody List<PrsCheckinMaster> list){
        PrsCheckinMaster prsCheckinMaster = prsCheckinMasterService.batchCheck(list);
        return AjaxResult.success(prsCheckinMaster);
    }

    @PreAuthorize("@ss.hasPermi('prs:checkin:revoke')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_checkin_type")
    @Log(title = "反审核订单", businessType = BusinessType.REVOKE)
    @ApiOperation("反审核")
    @PostMapping("/antiCheck")
    public AjaxResult antiCheck(@RequestBody  List<PrsCheckinMaster> list){
        PrsCheckinMaster prsCheckinMaster = prsCheckinMasterService.batchAntiCheck(list);
        return AjaxResult.success(prsCheckinMaster);
    }

    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:pick:next')")
    @ApiOperation("下一条")
    @GetMapping("/nextOrLast")
    public AjaxResult nextOrLast(PrsCheckinMaster prsCheckinMaster){
        List<String> codes = prsCheckinMasterService.selectPrsCode(prsCheckinMaster);
        int i = 0;
        String prsCode = prsCheckinMaster.getPrsCode();
        Boolean type = prsCheckinMaster.getType();
        for (int i1 = 0; i1 < codes.size(); i1++) {
            if(codes.get(i1).equals(prsCode)){
                i = i1;
            }
        }
        if( (i == 0 && type) || ((i ==codes.size()-1) && !type)){
            throw  new CommonException("已经是最后一条单据了");
        }else if(type){
            PrsCheckinMaster prsCheckinMaster1 = new  PrsCheckinMaster();
            prsCheckinMaster1.setPrsCode(codes.get(i-1));
            return  AjaxResult.success( prsCheckinMasterService.selectPrsCheckinMasterById(prsCheckinMaster1));
        }
        else{
            PrsCheckinMaster prsCheckinMaster1 = new  PrsCheckinMaster();
            prsCheckinMaster1.setPrsCode(codes.get(i+1));
            return AjaxResult.success(prsCheckinMasterService.selectPrsCheckinMasterById(prsCheckinMaster1));
        }
    }

    @PreAuthorize("@ss.hasPermi('prs:checkin:import')")
    @ApiOperation("引入")
    @GetMapping("/lead")
    public TableDataInfo lead(@RequestParam Map<String,String> map){
        startPage();
        List<Map<String,Object>> list =  prsProductMasterService.lead(map);
        //List<Map<String,Object>> list =  prsCheckinMasterService.lead(map);
        return getDataTable(list);
    }

    @DataScope(roleAlias = "prs_oper",workAlias = "prs_checkin_type")
    @PreAuthorize("@ss.hasPermi('prs:checkin:detail')")
    @ApiOperation("获取明细")
    @GetMapping("/getDetail")
    public TableDataInfo getDetail(PrsCheckinExport prsCheckinExport){
        startPage();
        List<PrsCheckinExport> detail = prsCheckinMasterService.getDetail(prsCheckinExport);
        return getDataTable(detail);
    }

    @PreAuthorize("@ss.hasPermi('prs:checkin:chain')")
    @ApiOperation("关联单据查询")
    @GetMapping("/chain")
    public TableDataInfo linkDetail(PrsCheckinExport prsCheckinExport){
        List<Map<String,String>> list =  prsCheckinMasterService.chain(prsCheckinExport);
        return getDataTable(list);
    }

    @ApiOperation("更新业务状态")
    @Log(title = "挂起完工订单", businessType = BusinessType.HANG)
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_checkin_type")
    @PreAuthorize("@ss.hasPermi('prs:checkin:operate')")
    @PostMapping("/operate")
    public AjaxResult changeWorkStatus(@RequestBody PrsCheckinMaster prsCheckinMaster){
        prsCheckinMasterService.changeWorkStatus(prsCheckinMaster);
        return AjaxResult.success(prsCheckinMasterService.selectPrsCheckinMasterById(prsCheckinMaster));
    }

    @ApiOperation("生产入库明细表")
    @GetMapping("/report")
    public TableDataInfo report(PrsCheckinReport prsCheckinReport){
        startPage();
        List<PrsCheckinReport> report = prsCheckinMasterService.report(prsCheckinReport);
        return getDataTable(report);
    }

    @GetMapping("/report/export")
    @ApiOperation("生产入库明细表导出")
    public AjaxResult reportExport(PrsCheckinReport prsCheckinReport){
        List<PrsCheckinReport> report = prsCheckinMasterService.report(prsCheckinReport);
        ExcelUtil<PrsCheckinReport> excelUtil = new ExcelUtil<PrsCheckinReport>(PrsCheckinReport.class);
        return  excelUtil.exportExcel(report,"生产入库明细表");
    }
}
