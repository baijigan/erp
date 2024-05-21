package com.njrsun.modules.prs.controller;

import com.njrsun.common.annotation.DataScope;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.core.page.TableDataInfo;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.modules.mp.contact.MpContacts;
import com.njrsun.modules.mp.domain.MpMbomMaster;
import com.njrsun.modules.mp.service.IMpMbomMasterService;
import com.njrsun.modules.om.contact.OmContacts;
import com.njrsun.modules.om.domain.OmOrderReport;
import com.njrsun.modules.om.service.IOmOrderMasterService;
import com.njrsun.modules.prs.domain.ProductionProgress;
import com.njrsun.modules.prs.domain.PrsOrderExport;
import com.njrsun.modules.prs.domain.PrsOrderMaster;
import com.njrsun.modules.prs.domain.PrsOverdueExport;
import com.njrsun.modules.prs.service.IPrsOrderMasterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 生产订单主Controller
 *
 * @author njrsun
 * @date 2021-11-12
 */
@Api(tags = "生产订单")
@RestController
@RequestMapping("/prs/order")
public class PrsOrderMasterController extends BaseController {
    @Autowired
    private IPrsOrderMasterService prsOrderMasterService;

    @Autowired
    private IMpMbomMasterService mpMbomMasterService;

    @Autowired
    private IOmOrderMasterService omOrderMasterService;

    /**
     * 查询生产订单主列表
     */
    @ApiOperation("列表")
    @DataScope(roleAlias = "prs_oper", workAlias = "prs_order_type")
    @PreAuthorize("@ss.hasPermi('prs:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(PrsOrderMaster prsOrderMaster) {
        startPage();
        List<PrsOrderMaster> list = prsOrderMasterService.selectPrsOrderMasterList(prsOrderMaster);
        return getDataTable(list);
    }

    /**
     * 导出生产订单主列表
     */
    @ApiOperation(value = "导出", hidden = true)
    @PreAuthorize("@ss.hasPermi('prs:order:export')")
    @Log(title = "生产订单主", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PrsOrderMaster prsOrderMaster) {
        List<PrsOrderMaster> list = prsOrderMasterService.selectPrsOrderMasterList(prsOrderMaster);
        ExcelUtil<PrsOrderMaster> util = new ExcelUtil<PrsOrderMaster>(PrsOrderMaster.class);
        return util.exportExcel(list, "prs");
    }

    /**
     * 获取生产订单主详细信息
     */
    @ApiOperation("查看")
    @DataScope(roleAlias = "prs_oper", workAlias = "prs_order_type")
    @PreAuthorize("@ss.hasPermi('prs:order:entity:view')")
    @GetMapping
    public AjaxResult getInfo(PrsOrderMaster prsOrderMaster) {
        return AjaxResult.success(prsOrderMasterService.selectPrsOrderMasterById(prsOrderMaster));
    }

    /**
     * 新增生产订单主
     */
    @ApiOperation("新增")
    @DataScope(roleAlias = "prs_oper", workAlias = "prs_order_type")
    @PreAuthorize("@ss.hasPermi('prs:order:entity:add')")
    @Log(title = "生产订单主", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsOrderMaster prsOrderMaster) {
        return AjaxResult.success(prsOrderMasterService.selectPrsOrderMasterById(prsOrderMaster));
    }

    /**
     * 修改生产订单主
     */
    @ApiOperation("修改")
    @DataScope(roleAlias = "prs_oper", workAlias = "prs_order_type")
    @PreAuthorize("@ss.hasPermi('prs:order:entity:edit')")
    @Log(title = "生产订单主", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsOrderMaster prsOrderMaster) {
        prsOrderMasterService.updatePrsOrderMaster(prsOrderMaster);

        return AjaxResult.success(prsOrderMasterService.selectPrsOrderMasterById(prsOrderMaster));
    }

    /**
     * 删除生产订单主
     */
    @ApiOperation("删除")
    @DataScope(roleAlias = "prs_oper", workAlias = "prs_order_type")
    @PreAuthorize("@ss.hasPermi('prs:order:remove')")
    @Log(title = "生产订单主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<PrsOrderMaster> list) {
        return toAjax(prsOrderMasterService.deletePrsOrderMasterByIds(list));
    }


    @PreAuthorize("@ss.hasPermi('prs:order:permit')")
    @DataScope(roleAlias = "prs_oper", workAlias = "prs_order_type")
    @Log(title = "审核订单", businessType = BusinessType.PERMIT)
    @ApiOperation("审核")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody List<PrsOrderMaster> list) {
        PrsOrderMaster prsOrderMaster = prsOrderMasterService.batchCheck(list);
        return AjaxResult.success(prsOrderMaster);
    }


    @PreAuthorize("@ss.hasPermi('prs:order:revoke')")
    @DataScope(roleAlias = "prs_oper", workAlias = "prs_order_type")
    @Log(title = "反审核订单", businessType = BusinessType.REVOKE)
    @ApiOperation("反审核")
    @PostMapping("/antiCheck")
    public AjaxResult antiCheck(@RequestBody List<PrsOrderMaster> list) {
        PrsOrderMaster prsOrderMaster = prsOrderMasterService.batchAntiCheck(list);
        return AjaxResult.success(prsOrderMaster);
    }

    @DataScope(roleAlias = "prs_oper", workAlias = "prs_order_type")
    @PreAuthorize("@ss.hasPermi('prs:order:import')")
    @ApiOperation("引入计划明细")
    @GetMapping("/leadInto/plan")
    public TableDataInfo leadIntoPlan(MpMbomMaster mpMbomMaster) {
        startPage();
        List<Map<String, String>> list = mpMbomMasterService.leadIntoPrs(mpMbomMaster);
        return getDataTable(list);
    }

    @DataScope(roleAlias = "prs_oper", workAlias = "prs_order_type")
    @PreAuthorize("@ss.hasPermi('prs:order:import')")
    @ApiOperation("引入销售明细")
    @GetMapping("/leadInto/sales")
    public TableDataInfo leadIntoSales(OmOrderReport omOrderReport) {
        startPage();
        List<Map<String, String>> list = omOrderMasterService.leadIntoPrs(omOrderReport);
         return getDataTable(list);
    }

    @DataScope(roleAlias = "prs_oper", workAlias = "prs_order_type")
    @PreAuthorize("@ss.hasPermi('prs:order:next')")
    @ApiOperation("下一条")
    @GetMapping("/nextOrLast")
    public AjaxResult nextOrLast(PrsOrderMaster prsOrderMaster) {
        List<String> codes = prsOrderMasterService.selectPrsCodeList(prsOrderMaster);
        int i = 0;
        String prsCode = prsOrderMaster.getPrsCode();
        Boolean type = prsOrderMaster.getType();
        for (int i1 = 0; i1 < codes.size(); i1++) {
            if (codes.get(i1).equals(prsCode)) {
                i = i1;
            }
        }
        if ((i == 0 && type) || ((i == codes.size() - 1) && !type)) {
            throw new CommonException("已经是最后一条单据了");
        } else if (type) {
            PrsOrderMaster prsOrderMaster1 = new PrsOrderMaster();
            prsOrderMaster1.setPrsCode(codes.get(i - 1));
            return AjaxResult.success(prsOrderMasterService.selectPrsOrderMasterById(prsOrderMaster1));
        } else {
            PrsOrderMaster prsOrderMaster1 = new PrsOrderMaster();
            prsOrderMaster1.setPrsCode(codes.get(i + 1));
            return AjaxResult.success(prsOrderMasterService.selectPrsOrderMasterById(prsOrderMaster1));
        }
    }


//    @DataScope(roleAlias = "prs_oper",workAlias = "prs_order_type")
//    @PreAuthorize("@ss.hasPermi('prs:order:export')")
//    @ApiOperation("明细导出")
//    @GetMapping("/detailExport")
//    public AjaxResult detailExport(PrsOrderSalve prsOrderSalve){
//        List<PrsOrderExport> detail = prsOrderMasterService.getDetail(prsOrderSalve);
//        ExcelUtil<PrsOrderExport> util = new ExcelUtil<PrsOrderExport>(PrsOrderExport.class);
//        return util.exportExcel(detail, "生产订单明细");
//    }


    @PreAuthorize("@ss.hasPermi('prs:order:chain')")
    @ApiOperation("关联单据查询")
    @GetMapping("/chain")
    public TableDataInfo linkDetail(PrsOrderExport prsOrderExport) {
        startPage();
        List<Map<String, String>> list = prsOrderMasterService.linkDetail(prsOrderExport);
        return getDataTable(list);
    }


    @ApiOperation("更新业务状态")
    @Log(title = "挂起计划订单", businessType = BusinessType.HANG)
    @DataScope(roleAlias = "prs_oper", workAlias = "prs_order_type")
    @PreAuthorize("@ss.hasPermi('prs:order:operate')")
    @PostMapping("/operate")
    public AjaxResult changeWorkStatus(@RequestBody PrsOrderMaster prsOrderMaster) {
        prsOrderMasterService.changeWorkStatus(prsOrderMaster);
        return AjaxResult.success(prsOrderMasterService.selectPrsOrderMasterById(prsOrderMaster));
    }


    @ApiOperation("生产逾期明细表")
    @GetMapping("/overdue/report")
    public TableDataInfo report(PrsOverdueExport prsOverdueExport) {
        startPage();
        List<PrsOverdueExport> list = prsOrderMasterService.export(prsOverdueExport);
        return getDataTable(list);
    }

    @ApiOperation("生产逾期明细表导出")
    @GetMapping("/overdue/execl")
    public AjaxResult execl(PrsOverdueExport prsOverdueExport) {
        List<PrsOverdueExport> list = prsOrderMasterService.export(prsOverdueExport);
        ExcelUtil<PrsOverdueExport> util = new ExcelUtil<PrsOverdueExport>(PrsOverdueExport.class);
        return util.exportExcel(list, "生产逾期明细表");
    }

    @ApiOperation(value = "生产进度", notes = "0 = 3天,1 = 7天 ，2 = 一个月，其它等于所有")
    @PreAuthorize("@ss.hasPermi('prs:progress:list')")
    @GetMapping("/productionProgress")
    public AjaxResult productionProgress(@RequestParam String workType, @RequestParam String flag) {
        ProductionProgress list = prsOrderMasterService.production(workType, flag);
        return AjaxResult.success(list);
    }


    /**
     * 修改生产订单主
     */
    @ApiOperation("审核修改")
    @DataScope(roleAlias = "prs_oper", workAlias = "prs_order_type")
    @PreAuthorize("@ss.hasPermi('prs:order:edit')")
    @Log(title = "生产订单主", businessType = BusinessType.UPDATE)
    @PutMapping("/check")
    public AjaxResult edit_check(@RequestBody PrsOrderMaster prsOrderMaster) {
        prsOrderMasterService.updatePrsOrderMasterCheck(prsOrderMaster);
        return AjaxResult.success(prsOrderMasterService.selectPrsOrderMasterById(prsOrderMaster));
    }


}
