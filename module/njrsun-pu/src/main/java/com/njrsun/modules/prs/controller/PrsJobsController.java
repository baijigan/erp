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
import com.njrsun.modules.prs.domain.PrsJobs;
import com.njrsun.modules.prs.service.IPrsJobsService;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 工种Controller
 * 
 * @author njrsun
 * @date 2022-01-11
 */
@RestController
@Api(tags = "工种")
@RequestMapping("/prs/jobs")
public class PrsJobsController extends BaseController
{
    @Autowired
    private IPrsJobsService prsJobsService;

    /**
     * 查询工种列表
     */
    @PreAuthorize("@ss.hasPermi('prs:jobs:list')")
    @GetMapping("/list")
    public TableDataInfo list(PrsJobs prsJobs)
    {
        startPage();
        List<PrsJobs> list = prsJobsService.selectPrsJobsList(prsJobs);
        return getDataTable(list);
    }

    /**
     * 导出工种列表
     */
    @PreAuthorize("@ss.hasPermi('prs:jobs:export')")
    @Log(title = "工种", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PrsJobs prsJobs)
    {
        return util.exportExcel(list, "jobs");
    }

    /**
     * 获取工种详细信息
     */
    @PreAuthorize("@ss.hasPermi('prs:jobs:query')")
    @GetMapping(value = "/{uniqueId}")
    public AjaxResult getInfo(@PathVariable("uniqueId") Long uniqueId)
    {
        return AjaxResult.success(prsJobsService.selectPrsJobsById(uniqueId));
    }

    /**
     * 新增工种
     */
    @PreAuthorize("@ss.hasPermi('prs:jobs:add')")
    @Log(title = "工种", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsJobs prsJobs)
    {
        return toAjax(prsJobsService.insertPrsJobs(prsJobs));
    }

    /**
     * 修改工种
     */
    @PreAuthorize("@ss.hasPermi('prs:jobs:edit')")
    @Log(title = "工种", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsJobs prsJobs)
    {
        return toAjax(prsJobsService.updatePrsJobs(prsJobs));
    }

    /**
     * 删除工种
     */
    @PreAuthorize("@ss.hasPermi('prs:jobs:remove')")
    @Log(title = "工种", businessType = BusinessType.DELETE)
	@DeleteMapping("/{uniqueIds}")
    public AjaxResult remove(@PathVariable Long[] uniqueIds)
    {
        return toAjax(prsJobsService.deletePrsJobsByIds(uniqueIds));
    }
}
