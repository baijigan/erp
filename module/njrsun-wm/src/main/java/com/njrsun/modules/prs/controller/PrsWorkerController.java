package com.njrsun.modules.prs.controller;

import java.util.List;

import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.modules.prs.domain.PrsWorker;
import com.njrsun.modules.prs.service.IPrsWorkerService;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 工人Controller
 * 
 * @author njrsun
 * @date 2021-12-23
 */
@RestController
@Api(tags = "工人")
@RequestMapping("/prs/worker")
public class PrsWorkerController extends BaseController
{
        @Autowired
        private IPrsWorkerService prsWorkerService;

    /**
     * 查询工人列表
     */
    @PreAuthorize("@ss.hasPermi('prs:worker:list')")
    @GetMapping("/list")
    public TableDataInfo list(PrsWorker prsWorker)
    {
        startPage();
        List<PrsWorker> list = prsWorkerService.selectPrsWorkerList(prsWorker);
        return getDataTable(list);
    }

    /**
     * 导出工人列表
     */
    @PreAuthorize("@ss.hasPermi('prs:worker:export')")
    @Log(title = "工人", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PrsWorker prsWorker)
    {
        List<PrsWorker> list = prsWorkerService.selectPrsWorkerList(prsWorker);
        ExcelUtil<PrsWorker> util = new ExcelUtil<PrsWorker>(PrsWorker.class);
        return util.exportExcel(list, "worker");
    }

    /**
     * 获取工人详细信息
     */
    @PreAuthorize("@ss.hasPermi('prs:worker:query')")
    @GetMapping(value = "/{uniqueId}")
    public AjaxResult getInfo(@PathVariable("uniqueId") Long uniqueId)
    {
        return AjaxResult.success(prsWorkerService.selectPrsWorkerById(uniqueId));
    }

    /**
     * 新增工人
     */
    @PreAuthorize("@ss.hasPermi('prs:worker:add')")
    @Log(title = "工人", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsWorker prsWorker)
    {
        return toAjax(prsWorkerService.insertPrsWorker(prsWorker));
    }

    /**
     * 修改工人
     */
    @PreAuthorize("@ss.hasPermi('prs:worker:edit')")
    @Log(title = "工人", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsWorker prsWorker)
    {
        return toAjax(prsWorkerService.updatePrsWorker(prsWorker));
    }

    /**
     * 删除工人
     */
    @PreAuthorize("@ss.hasPermi('prs:worker:remove')")
    @Log(title = "工人", businessType = BusinessType.DELETE)
	@DeleteMapping("/{code}")
    public AjaxResult remove(@PathVariable String[] code)
    {
        return toAjax(prsWorkerService.deletePrsWorkerByIds(code));
    }
}
