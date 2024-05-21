package com.njrsun.modules.prs.controller;

import java.util.List;
import java.util.Map;

import com.njrsun.common.annotation.DataScope;
import com.njrsun.common.exception.CommonException;
import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.service.IPrsOrderMasterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.modules.prs.service.IPrsProductMasterService;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 生产完工单主Controller
 * 
 * @author njrsun
 * @date 2021-11-15
 */
@RestController
@Api(tags = "完工单")
@RequestMapping("/prs/product")
public class PrsProductMasterController extends BaseController
{
    @Autowired
    private IPrsProductMasterService prsProductMasterService;

    @Autowired
    private IPrsOrderMasterService prsOrderMasterService;
    /**
     * 查询生产完工单主列表
     */
    @PreAuthorize("@ss.hasPermi('prs:product:list')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_product_type")
    @GetMapping("/list")
    public TableDataInfo list(PrsProductMaster prsProductMaster)
    {
        startPage();
        List<PrsProductMaster> list = prsProductMasterService.selectPrsProductMasterList(prsProductMaster);
        return getDataTable(list);
    }

    /**
     * 导出生产完工单主列表
     */
    @PreAuthorize("@ss.hasPermi('prs:product:export')")
    @Log(title = "生产完工单主", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PrsProductMaster prsProductMaster)
    {
        List<PrsProductMaster> list = prsProductMasterService.selectPrsProductMasterList(prsProductMaster);
        ExcelUtil<PrsProductMaster> util = new ExcelUtil<PrsProductMaster>(PrsProductMaster.class);
        return util.exportExcel(list, "product");
    }

    /**
     * 获取生产完工单主详细信息
     */
    @PreAuthorize("@ss.hasPermi('prs:product:query')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_product_type")
    @GetMapping
    public AjaxResult getInfo(PrsProductMaster prsProductMaster)
    {
        return AjaxResult.success(prsProductMasterService.selectPrsProductMasterById(prsProductMaster));
    }

    /**
     * 新增生产完工单主
     */
    @PreAuthorize("@ss.hasPermi('prs:product:entity:add')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_product_type")
    @Log(title = "生产完工单主", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsProductMaster prsProductMaster)
    {
        return AjaxResult.success(prsProductMasterService.selectPrsProductMasterById(prsProductMaster));

    }

    /**
     * 修改生产完工单主
     */
    @PreAuthorize("@ss.hasPermi('prs:product:entity:edit')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_product_type")
    @Log(title = "生产完工单主", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsProductMaster prsProductMaster)
    {
        int i = prsProductMasterService.updatePrsProductMaster(prsProductMaster);
        if(i == 0){
            return AjaxResult.error();
        }
        return AjaxResult.success(prsProductMasterService.selectPrsProductMasterById(prsProductMaster));
    }

    /**
     * 删除生产完工单主
     */
    @PreAuthorize("@ss.hasPermi('prs:product:remove')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_product_type")
    @Log(title = "生产完工单主", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<PrsProductMaster> list)
    {
        return toAjax(prsProductMasterService.deletePrsProductMasterByIds(list));
    }

    @ApiOperation("更新业务状态")
    @Log(title = "挂起完工订单", businessType = BusinessType.HANG)
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_product_type")
    @PreAuthorize("@ss.hasPermi('prs:product:operate')")
    @PostMapping("/operate")
    public AjaxResult changeWorkStatus(@RequestBody PrsProductMaster prsProductMaster){
        prsProductMasterService.changeWorkStatus(prsProductMaster);
        return AjaxResult.success(prsProductMasterService.selectPrsProductMasterById(prsProductMaster));
    }

    @DataScope(roleAlias = "prs_oper",workAlias = "prs_product_type")
    @PreAuthorize("@ss.hasPermi('prs:product:detail')")
    @ApiOperation("获取明细")
    @GetMapping("/getDetail")
    public TableDataInfo getDetail(PrsProductExport prsProductSalve){
        startPage();
        List<PrsProductExport> detail = prsProductMasterService.getDetail(prsProductSalve);
        return getDataTable(detail);
    }


    @PreAuthorize("@ss.hasPermi('prs:product:export')")
    @ApiOperation("明细导出")
    @GetMapping("/detailExport")
    public AjaxResult detailExport(PrsProductExport prsProductSalve){
        List<PrsProductExport> detail = prsProductMasterService.getDetail(prsProductSalve);
        ExcelUtil<PrsProductExport> util = new ExcelUtil<PrsProductExport>(PrsProductExport.class);
        return util.exportExcel(detail, "生产完工单明细");
    }

    @PreAuthorize("@ss.hasPermi('prs:order:import')")
    @ApiOperation("引入明细")
    @GetMapping("/leadInto")
    public TableDataInfo leadInto(PrsOrderMaster prsOrderMaster){
        startPage();
        List<Map<String,String>> list =  prsProductMasterService.leadInto(prsOrderMaster);
        return getDataTable(list);
    }


    @DataScope(roleAlias = "prs_oper",workAlias = "prs_product_type")
    @PreAuthorize("@ss.hasPermi('prs:order:import')")
    @ApiOperation("引入")
    @GetMapping("/lead")
    public TableDataInfo lead(PrsOrderMaster prsOrderMaster){
        startPage();
        List<Map<String,String>> list = prsOrderMasterService.lead(prsOrderMaster);
        return getDataTable(list);
    }


    @PreAuthorize("@ss.hasPermi('prs:product:permit')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_product_type")
    @Log(title = "审核订单", businessType = BusinessType.PERMIT)
    @ApiOperation("审核")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody List<PrsProductMaster> list){
        PrsProductMaster prsProductMaster = prsProductMasterService.batchCheck(list);
        return AjaxResult.success(prsProductMaster);
    }


    @PreAuthorize("@ss.hasPermi('prs:product:revoke')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_product_type")
    @Log(title = "反审核订单", businessType = BusinessType.REVOKE)
    @ApiOperation("反审核")
    @PostMapping("/antiCheck")
    public AjaxResult antiCheck(@RequestBody  List<PrsProductMaster> list){
        PrsProductMaster prsProductMaster = prsProductMasterService.batchAntiCheck(list);
        return AjaxResult.success(prsProductMaster);
    }

    @DataScope(roleAlias = "prs_oper",workAlias = "prs_product_type")
    @PreAuthorize("@ss.hasPermi('prs:product:next')")
    @ApiOperation("下一条")
    @GetMapping("/nextOrLast")
    public AjaxResult nextOrLast(PrsProductMaster prsProductMaster){
        List<String> codes = prsProductMasterService.selectPrsCodeList(prsProductMaster);
        int i = 0;
        String prsCode = prsProductMaster.getPrsCode();
        Boolean type = prsProductMaster.getType();
        for (int i1 = 0; i1 < codes.size(); i1++) {
            if(codes.get(i1).equals(prsCode)){
                i = i1;
            }
        }
        if( (i == 0 && type) || ((i ==codes.size()-1) && !type)){
            throw  new CommonException("已经是最后一条单据了");
        }else if(type){
            PrsProductMaster prsOrderMaster1 = new  PrsProductMaster();
            prsOrderMaster1.setPrsCode(codes.get(i-1));
            return  AjaxResult.success( prsProductMasterService.selectPrsProductMasterById(prsOrderMaster1));
        }
        else{
            PrsProductMaster prsOrderMaster1 = new PrsProductMaster();
            prsOrderMaster1.setPrsCode(codes.get(i+1));
            return AjaxResult.success(prsProductMasterService.selectPrsProductMasterById(prsOrderMaster1));
        }
    }



    @PreAuthorize("@ss.hasPermi('prs:product:chain')")
    @ApiOperation("关联单据查询")
    @GetMapping("/chain")
    public TableDataInfo linkDetail(PrsProductExport prsProductExport){
        startPage();
        List<Map<String,String>> list =  prsProductMasterService.chain(prsProductExport);
        return getDataTable(list);
    }


    @ApiOperation("生产执行明细表")
    @GetMapping("/report")
    public TableDataInfo report(PrsOrderProductExport prsProductExport){
        startPage();
        List<PrsOrderProductExport> list =  prsProductMasterService.report(prsProductExport);
        return getDataTable(list);
    }

    @ApiOperation("生产执行明细表导出")
    @GetMapping("/report/execl")
    public AjaxResult execl(PrsOrderProductExport prsProductExport){
        List<PrsOrderProductExport> list =  prsProductMasterService.report(prsProductExport);
        ExcelUtil<PrsOrderProductExport> util = new ExcelUtil<PrsOrderProductExport>(PrsOrderProductExport.class);
        return util.exportExcel(list, "生产执行明细表");
    }

    @ApiOperation("产量统计报表")
    @GetMapping("/produce/report")
    public TableDataInfo ProduceReport(PrsOrderProductExport prsProductExport){
        startPage();
        List<PrsProduceReport> list =  prsProductMasterService.produceReport(prsProductExport);
        return getDataTable(list);
    }

    @ApiOperation("产量统计报表")
    @GetMapping("/produce/report/export")
    public AjaxResult ProduceReportExport(PrsOrderProductExport prsProductExport){
        List<PrsProduceReport> list =  prsProductMasterService.produceReport(prsProductExport);
        ExcelUtil<PrsProduceReport> util = new ExcelUtil<PrsProduceReport>(PrsProduceReport.class);
        return util.exportExcel(list, "产量统计报表");
    }


    @ApiOperation("生产产量明细表开发")
    @GetMapping("/output")
    public TableDataInfo output(PrsProductMaster prsProductMaster){
        startPage();
        List<PrsOutputReport> list =  prsProductMasterService.output(prsProductMaster);
        return getDataTable(list);
    }

    @ApiOperation("生产产量明细表开发导出")
    @GetMapping("/output/export")
    public AjaxResult outputExport(PrsProductMaster prsProductMaster){
        List<PrsOutputReport> list =  prsProductMasterService.output(prsProductMaster);
        ExcelUtil<PrsOutputReport> util = new ExcelUtil<PrsOutputReport>(PrsOutputReport.class);
        return util.exportExcel(list, "生产产量明细表");
    }


}
