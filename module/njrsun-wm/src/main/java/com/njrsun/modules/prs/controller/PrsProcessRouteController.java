package com.njrsun.modules.prs.controller;

import java.util.List;

import com.njrsun.common.exception.CommonException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.modules.prs.domain.PrsProcessRoute;
import com.njrsun.modules.prs.service.IPrsProcessRouteService;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 工艺路线Controller
 * 
 * @author njrsun
 * @date 2022-01-10
 */
@RestController
@Api(tags = "工艺路线")
@RequestMapping("/prs/process")
public class PrsProcessRouteController extends BaseController
{
    @Autowired
    private IPrsProcessRouteService prsProcessRouteService;

    /**
     * 查询工艺路线列表
     */
    @PreAuthorize("@ss.hasPermi('prs:process:list')")
    @GetMapping("/list")
    public TableDataInfo list(PrsProcessRoute prsProcessRoute)
    {
            startPage();
            List<PrsProcessRoute> list = prsProcessRouteService.selectPrsProcessRouteList(prsProcessRoute);
            return getDataTable(list);
    }

    /**
     * 导出工艺路线列表
     */
    @PreAuthorize("@ss.hasPermi('prs:process:export')")
    @Log(title = "工艺路线", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PrsProcessRoute prsProcessRoute)
    {
            return util.exportExcel(list, "process");
    }

    /**
     * 获取工艺路线详细信息
     */
    @PreAuthorize("@ss.hasPermi('prs:process:query')")
    @GetMapping
    public AjaxResult getInfo(@RequestParam String processCode)
    {
            return AjaxResult.success(prsProcessRouteService.selectPrsProcessRouteById(processCode));
    }

    /**
     * 新增工艺路线
     */
    @PreAuthorize("@ss.hasPermi('prs:process:add')")
    @Log(title = "工艺路线", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsProcessRoute prsProcessRoute)
    {
        prsProcessRouteService.insertPrsProcessRoute(prsProcessRoute);
        return AjaxResult.success(prsProcessRouteService.selectPrsProcessRouteById(prsProcessRoute.getProcessCode()));
    }

    /**
     * 修改工艺路线
     */
    @PreAuthorize("@ss.hasPermi('prs:process:edit')")
    @Log(title = "工艺路线", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsProcessRoute prsProcessRoute)
    {
       prsProcessRouteService.updatePrsProcessRoute(prsProcessRoute);
        return AjaxResult.success(prsProcessRouteService.selectPrsProcessRouteById(prsProcessRoute.getProcessCode()));
    }

    /**
     * 删除工艺路线
     */
    @PreAuthorize("@ss.hasPermi('prs:process:remove')")
    @Log(title = "工艺路线", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<PrsProcessRoute> list)
    {
        return toAjax(prsProcessRouteService.deletePrsProcessRouteByIds(list));
    }

    @PreAuthorize("@ss.hasPermi('prs:resource:permit')")
    @Log(title = "审核订单", businessType = BusinessType.PERMIT)
    @ApiOperation("审核")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody List<PrsProcessRoute> list){
        PrsProcessRoute PrsProcessRoute = prsProcessRouteService.batchCheck(list);
        return AjaxResult.success(PrsProcessRoute);
    }


    @PreAuthorize("@ss.hasPermi('prs:resource:revoke')")
    @Log(title = "反审核订单", businessType = BusinessType.REVOKE)
    @ApiOperation("反审核")
    @PostMapping("/antiCheck")
    public AjaxResult antiCheck(@RequestBody  List<PrsProcessRoute> list){
        PrsProcessRoute PrsProcessRoute = prsProcessRouteService.batchAntiCheck(list);
        return AjaxResult.success(PrsProcessRoute);
    }

    @PreAuthorize("@ss.hasPermi('prs:resource:next')")
    @ApiOperation("下一条")
    @GetMapping("/nextOrLast")
    public AjaxResult nextOrLast(PrsProcessRoute prsProcessRoute){
        List<PrsProcessRoute> codes = prsProcessRouteService.selectPrsProcessRouteList(new PrsProcessRoute());
        int i = 0;
        String processCode = prsProcessRoute.getProcessCode();
        Boolean type = prsProcessRoute.getType();
        for (int i1 = 0; i1 < codes.size(); i1++) {
            if(codes.get(i1).getProcessCode().equals(processCode)){
                i = i1;
            }
        }
        if( (i == 0 && type) || ((i ==codes.size()-1) && !type)){
            throw  new CommonException("已经是最后一条单据了");
        }else if(type){
            return  AjaxResult.success( prsProcessRouteService.selectPrsProcessRouteById(codes.get(i-1).getProcessCode()));
        }
        else{
            return  AjaxResult.success( prsProcessRouteService.selectPrsProcessRouteById(codes.get(i+1).getProcessCode()));
        }
    }



}
