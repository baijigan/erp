package com.njrsun.modules.rd.controller;

import com.njrsun.common.annotation.DataScope;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.core.page.TableDataInfo;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.modules.rd.domain.RdEbomExport;
import com.njrsun.modules.rd.domain.RdEbomMaster;
import com.njrsun.modules.rd.domain.RdEbomSalve;
import com.njrsun.modules.rd.domain.RdTree;
import com.njrsun.modules.rd.service.IRdEbomMasterService;
import com.njrsun.api.inv.service.IInvItemsApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 配方Controller
 * @author njrsun
 * @date 2021-11-23
 */

@Api(tags = "物料清单")
@RestController
@RequestMapping("/rd/ebom")
public class RdEbomMasterController extends BaseController
{
    @Autowired
    private IRdEbomMasterService rdRecipeMasterService;

    @Autowired
    private IInvItemsApiService invItemsApiService;

    /**
     * 查询配方列表
     */
    @ApiOperation("列表")
    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @PreAuthorize("@ss.hasPermi('rd:ebom:list')")
    @GetMapping("/list")
    public TableDataInfo list(RdEbomMaster rdEbomMaster)
    {
        startPage();
        List<RdEbomMaster> list = rdRecipeMasterService.selectRdRecipeMasterList(rdEbomMaster);
        return getDataTable(list);
    }

    @ApiOperation("多阶")
    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @PreAuthorize("@ss.hasPermi('rd:ebom:list')")
    @GetMapping("/associate")
    public AjaxResult associate(RdEbomMaster rdEbomMaster)
    {
        List<RdEbomMaster> list = rdRecipeMasterService.associate(rdEbomMaster);
        return AjaxResult.success(list);
    }

    /**
     * 导出配方列表
     */
    @PreAuthorize("@ss.hasPermi('rd:ebom:export')")
    @Log(title = "配方", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(RdEbomExport rdRecipeExport)
    {
        List<RdEbomExport> list =   rdRecipeMasterService.detail(rdRecipeExport);
        ExcelUtil<RdEbomExport> util = new ExcelUtil<>(RdEbomExport.class);
        return util.exportExcel(list, "配方单");
    }

    /**
     * 获取配方详细信息
     */
    @ApiOperation("详情")
    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @PreAuthorize("@ss.hasPermi('rd:ebom:query')")
    @GetMapping
    public AjaxResult getInfo(RdEbomMaster rdEbomMaster)
    {
        return AjaxResult.success(rdRecipeMasterService.selectRdRecipeMasterById(rdEbomMaster));
    }

    /**
     * 新增配方
     */
    @ApiOperation("新增")
    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @PreAuthorize("@ss.hasPermi('rd:ebom:entity:add')")
    @Log(title = "配方", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RdEbomMaster rdEbomMaster)
    {
        rdRecipeMasterService.insertRdRecipeMaster(rdEbomMaster);
        return AjaxResult.success(rdRecipeMasterService.selectRdRecipeMasterById(rdEbomMaster));
    }

    /**
     * 修改配方
     */
    @ApiOperation("修改")
    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @PreAuthorize("@ss.hasPermi('rd:ebom:entity:edit')")
    @Log(title = "配方", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RdEbomMaster rdEbomMaster)
    {
        rdRecipeMasterService.updateRdRecipeMaster(rdEbomMaster);
        return AjaxResult.success(rdRecipeMasterService.selectRdRecipeMasterById(rdEbomMaster));
    }

    /**
     * 删除配方
     */
    @ApiOperation("删除")
    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @PreAuthorize("@ss.hasPermi('rd:ebom:remove')")
    @Log(title = "配方", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<RdEbomMaster> list)
    {
        return toAjax(rdRecipeMasterService.deleteRdRecipeMasterByIds(list));
    }

    @ApiOperation("下一条")
    @PreAuthorize("@ss.hasPermi('rd:ebom:next')")
    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @GetMapping("/nextOrLast")
    public AjaxResult getNextOrLast(RdEbomMaster rdEbomMaster){
        RdEbomMaster nextOrLast = rdRecipeMasterService.getNextOrLast(rdEbomMaster);
        return AjaxResult.success(nextOrLast);
    }

    @PreAuthorize("@ss.hasPermi('rd:ebom:permit')")
    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @Log(title = "审核订单", businessType = BusinessType.PERMIT)
    @ApiOperation("审核")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody List<RdEbomMaster> list){
        RdEbomMaster rdEbomMaster = rdRecipeMasterService.batchCheck(list);
        return AjaxResult.success(rdEbomMaster);
    }

    @PreAuthorize("@ss.hasPermi('rd:ebom:revoke')")
    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @Log(title = "反审核订单", businessType = BusinessType.REVOKE)
    @ApiOperation("反审核")
    @PostMapping("/antiCheck")
    public AjaxResult antiCheck(@RequestBody  List<RdEbomMaster> list){
        RdEbomMaster rdEbomMaster = rdRecipeMasterService.batchAntiCheck(list);
        return AjaxResult.success(rdEbomMaster);
    }

    @ApiOperation("更新业务状态")
    @Log(title = "挂起完工订单", businessType = BusinessType.HANG)
    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @PreAuthorize("@ss.hasPermi('rd:ebom:operate')")
    @PostMapping("/operate")
    public AjaxResult changeWorkStatus(@RequestBody RdEbomMaster rdEbomMaster){
        rdRecipeMasterService.changeWorkStatus(rdEbomMaster);
        return AjaxResult.success(rdRecipeMasterService.selectRdRecipeMasterById(rdEbomMaster));
    }

    @ApiOperation("明细")
    @Log(title = "明细", businessType = BusinessType.OTHER)
    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @PreAuthorize("@ss.hasPermi('rd:ebom:detail')")
    @GetMapping("/getDetail")
    public TableDataInfo detail(RdEbomExport rdRecipeExport){
       List<RdEbomExport> list =   rdRecipeMasterService.detail(rdRecipeExport);
       return getDataTable(list);
    }

    @ApiOperation("导入物料表")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file) throws Exception
    {
         rdRecipeMasterService.importExcelEbom(file.getOriginalFilename(), file.getInputStream());
        return  AjaxResult.success();
    }

    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @ApiOperation("检验")
    @GetMapping("/check")
    public AjaxResult check(String rdCode){
        ArrayList<String> strings = new ArrayList<>();
        rdRecipeMasterService.check(rdCode,strings);
        return AjaxResult.success();
    }

    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @ApiOperation("导出关联")
    @GetMapping("/export/link")
    public AjaxResult exportLink(@RequestParam("rdCode") String rdCode, @RequestParam("fileName") String fileName){
        ExcelUtil<RdEbomSalve> rdEbomSalveExcelUtil = new ExcelUtil<>(RdEbomSalve.class);
        ArrayList<RdEbomSalve> list = new ArrayList<>();
        rdRecipeMasterService.exportLink(rdCode,list);
        return  rdEbomSalveExcelUtil.exportExcel(list,fileName);
    }

    @DataScope(roleAlias = "rd_oper",workAlias = "rd_ebom_type")
    @ApiOperation("多阶树")
    @GetMapping("/tree")
    public AjaxResult tree(String invCode) throws UnsupportedEncodingException {
        RdTree rdTree = new RdTree();
        RdTree list =   rdRecipeMasterService.tree(invCode,rdTree);
        return AjaxResult.success(list);
    }


    @ApiOperation("物料查询")
    @GetMapping("/invList")
    public TableDataInfo invList(@RequestParam Map<String,String> query){
        startPage();
        List<Map<String,String>> list  =   invItemsApiService.selectInv(query);
        return  getDataTable(list);
    }


    @ApiOperation("清空")
    @GetMapping("/clear")
    public AjaxResult clear(@RequestParam String workType){
        rdRecipeMasterService.clear(workType);
        return  AjaxResult.success();
    }




}
