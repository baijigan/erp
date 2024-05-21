package com.njrsun.modules.prs.controller;

import com.njrsun.common.annotation.DataScope;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.core.page.TableDataInfo;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.modules.prs.domain.PrsOrderMaster;
import com.njrsun.modules.prs.domain.PrsUnPickExport;
import com.njrsun.modules.prs.domain.PrsUnPickMaster;
import com.njrsun.modules.prs.service.IPrsPickMasterService;
import com.njrsun.modules.prs.service.IPrsUnPickMasterService;
import com.njrsun.modules.prs.service.impl.PrsOrderMasterServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 生产领料单Controller
 * 
 * @author njrsun
 * @date 2021-11-18
 */
@Api(tags = "生产领料单")
@RestController
@RequestMapping("/prs/unpick")
public class PrsUnPickMasterController extends BaseController
{
    @Autowired
    private IPrsUnPickMasterService prsUnPickMasterService;

    @Autowired
    private IPrsPickMasterService prsPickMasterService;

    @Autowired
    private PrsOrderMasterServiceImpl prsOrderMasterService;

    /**
     * 查询生产领料单列表
     */
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:unpick:list')")
    @GetMapping("/list")
    public TableDataInfo list(PrsUnPickMaster prsUnPickMaster)
    {
        startPage();
        List<PrsUnPickMaster> list = prsUnPickMasterService.selectPrsUnPickMasterList(prsUnPickMaster);
        return getDataTable(list);
    }

    /**
     * 导出生产领料单列表
     */
    @PreAuthorize("@ss.hasPermi('prs:unpick:export')")
    @Log(title = "生产领料单", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PrsUnPickMaster prsUnPickMaster)
    {
        List<PrsUnPickMaster> list = prsUnPickMasterService.selectPrsUnPickMasterList(prsUnPickMaster);
        ExcelUtil<PrsUnPickMaster> util = new ExcelUtil<PrsUnPickMaster>(PrsUnPickMaster.class);
        return util.exportExcel(list, "pick");
    }

    /**
     * 获取生产领料单详细信息
     */
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:unpick:entity:view')")
    @GetMapping
    public AjaxResult getInfo(PrsUnPickMaster prsUnPickMaster)
    {
        return AjaxResult.success(prsUnPickMasterService.selectPrsUnPickMasterById(prsUnPickMaster));
    }

    /**
     * 新增生产领料单
     */
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:unpick:entity:add')")
    @Log(title = "生产领料单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsUnPickMaster prsUnPickMaster)
    {
        return AjaxResult.success(prsUnPickMasterService.selectPrsUnPickMasterById(prsUnPickMaster));
    }

    /**
     * 修改生产领料单
     */
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:unpick:entity:edit')")
    @Log(title = "生产领料单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsUnPickMaster prsUnPickMaster)
    {
        int i = prsUnPickMasterService.updatePrsUnPickMaster(prsUnPickMaster);
        if(i == 0){
            return   AjaxResult.error();
        }else{
            return AjaxResult.success(prsUnPickMasterService.selectPrsUnPickMasterById(prsUnPickMaster));
        }
    }

    /**
     * 删除生产领料单
     */
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:unpick:remove')")
    @Log(title = "生产领料单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<PrsUnPickMaster> list)
    {
        return toAjax(prsUnPickMasterService.deletePrsUnPickMasterByIds(list));
    }


    @ApiOperation("更新业务状态")
    @Log(title = "挂起完工订单", businessType = BusinessType.HANG)
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:unpick:operate')")
    @PostMapping("/operate")
    public AjaxResult changeWorkStatus(@RequestBody PrsUnPickMaster prsUnPickMaster){
        prsUnPickMasterService.changeWorkStatus(prsUnPickMaster);
        return AjaxResult.success(prsUnPickMasterService.selectPrsUnPickMasterById(prsUnPickMaster));
    }


    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:unpick:detail')")
    @ApiOperation("获取明细")
    @GetMapping("/getDetail")
    public TableDataInfo getDetail(PrsUnPickExport prsUnPickExport){
        startPage();
        List<PrsUnPickExport> detail = prsUnPickMasterService.getDetail(prsUnPickExport);
        return getDataTable(detail);
    }


    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:unpick:export')")
    @ApiOperation("明细导出")
    @GetMapping("/detailExport")
    public AjaxResult detailExport(PrsUnPickExport prsUnPickExport){
        List<PrsUnPickExport> detail = prsUnPickMasterService.getDetail(prsUnPickExport);
        ExcelUtil<PrsUnPickExport> util = new ExcelUtil<>(PrsUnPickExport.class);
        return util.exportExcel(detail, "生产完工单明细");
    }

    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:unpick:import')")
    @ApiOperation("选择生产订单")
    @GetMapping("/getPrsOrder")
    public TableDataInfo getprsorder(PrsOrderMaster prsOrderMaster){
        startPage();
        List<Map<String,String>> list = prsOrderMasterService.lead(prsOrderMaster);
        return getDataTable(list);
    }

    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:unpick:import')")
    @ApiOperation("选择领料主表")
    @GetMapping("/lead")
    public TableDataInfo lead(@RequestParam Map<String, String> query){
        startPage();
        query.put("bredVouch", "true");
        List<Map<String,String>> list = prsPickMasterService.lead(query);
        return getDataTable(list);
    }

//  @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:unpick:import')")
    @ApiOperation("引入领料明细")
    @GetMapping("/leadInto")
    public TableDataInfo leadInto(@RequestParam Map<String, String> query){
        startPage();
        query.put("bredVouch", "true");
        List<Map<String,String>> list = prsPickMasterService.leadInto(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('prs:unpick:permit')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @Log(title = "审核订单", businessType = BusinessType.PERMIT)
    @ApiOperation("审核")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody List<PrsUnPickMaster> list){
        PrsUnPickMaster prsUnPickMaster = prsUnPickMasterService.batchCheck(list);
        return AjaxResult.success(prsUnPickMaster);
    }

    @PreAuthorize("@ss.hasPermi('prs:unpick:revoke')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @Log(title = "反审核订单", businessType = BusinessType.REVOKE)
    @ApiOperation("反审核")
    @PostMapping("/antiCheck")
    public AjaxResult antiCheck(@RequestBody  List<PrsUnPickMaster> list){
        PrsUnPickMaster prsUnPickMaster = prsUnPickMasterService.batchAntiCheck(list);
        return AjaxResult.success(prsUnPickMaster);
    }

    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:unpick:next')")
    @ApiOperation("下一条")
    @GetMapping("/nextOrLast")
    public AjaxResult nextOrLast(PrsUnPickMaster prsUnPickMaster){
        List<String> codes = prsUnPickMasterService.selectPrsCode(prsUnPickMaster);
        int i = 0;
        String prsCode = prsUnPickMaster.getPrsCode();
        Boolean type = prsUnPickMaster.getType();
        for (int i1 = 0; i1 < codes.size(); i1++) {
            if(codes.get(i1).equals(prsCode)){
                i = i1;
            }
        }
        if( (i == 0 && type) || ((i ==codes.size()-1) && !type)){
            throw  new CommonException("已经是最后一条单据了");
        }else if(type){
            PrsUnPickMaster prsOrderMaster1 = new  PrsUnPickMaster();
            prsOrderMaster1.setPrsCode(codes.get(i-1));
            return  AjaxResult.success( prsUnPickMasterService.selectPrsUnPickMasterById(prsOrderMaster1));
        }
        else{
            PrsUnPickMaster prsOrderMaster1 = new PrsUnPickMaster();
            prsOrderMaster1.setPrsCode(codes.get(i+1));
            return AjaxResult.success(prsUnPickMasterService.selectPrsUnPickMasterById(prsOrderMaster1));
        }
    }


    @PreAuthorize("@ss.hasPermi('prs:unpick:chain')")
    @ApiOperation("关联单据查询")
    @GetMapping("/chain")
    public TableDataInfo linkDetail(PrsUnPickMaster prsProductExport){
        List<Map<String,String>> list =  prsUnPickMasterService.chain(prsProductExport);
        return getDataTable(list);
    }

}
