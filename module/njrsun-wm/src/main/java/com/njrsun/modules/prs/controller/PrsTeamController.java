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
import com.njrsun.modules.prs.domain.PrsTeam;
import com.njrsun.modules.prs.service.IPrsTeamService;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 班组Controller
 * 
 * @author njrsun
 * @date 2021-12-23
 */
@RestController
@Api(tags = "班组")
@RequestMapping("/prs/team")
public class PrsTeamController extends BaseController
{
        @Autowired
        private IPrsTeamService prsTeamService;

    /**
     * 查询班组列表
     */
    @PreAuthorize("@ss.hasPermi('prs:team:list')")
    @GetMapping("/list")
    public TableDataInfo list(PrsTeam prsTeam)
    {
        startPage();
        List<PrsTeam> list = prsTeamService.selectPrsTeamList(prsTeam);
        return getDataTable(list);
    }

    /**
     * 导出班组列表
     */
    @PreAuthorize("@ss.hasPermi('prs:team:export')")
    @Log(title = "班组", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PrsTeam prsTeam)
    {
        List<PrsTeam> list = prsTeamService.selectPrsTeamList(prsTeam);
        ExcelUtil<PrsTeam> util = new ExcelUtil<PrsTeam>(PrsTeam.class);
        return util.exportExcel(list, "prs");
    }

    /**
     * 获取班组详细信息
     */
    @PreAuthorize("@ss.hasPermi('prs:team:query')")
    @GetMapping(value = "/{uniqueId}")
    public AjaxResult getInfo(@PathVariable("uniqueId") Long uniqueId)
    {
        return AjaxResult.success(prsTeamService.selectPrsTeamById(uniqueId));
    }

    /**
     * 新增班组
     */
    @PreAuthorize("@ss.hasPermi('prs:team:add')")
    @Log(title = "班组", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsTeam prsTeam)
    {
        return toAjax(prsTeamService.insertPrsTeam(prsTeam));
    }

    /**
     * 修改班组
     */
    @PreAuthorize("@ss.hasPermi('prs:team:edit')")
    @Log(title = "班组", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsTeam prsTeam)
    {
        return toAjax(prsTeamService.updatePrsTeam(prsTeam));
    }

    /**
     * 删除班组
     */
    @PreAuthorize("@ss.hasPermi('prs:team:remove')")
    @Log(title = "班组", businessType = BusinessType.DELETE)
	@DeleteMapping("/{uniqueIds}")
    public AjaxResult remove(@PathVariable Long[] uniqueIds)
    {
        return toAjax(prsTeamService.deletePrsTeamByIds(uniqueIds));
    }
}
