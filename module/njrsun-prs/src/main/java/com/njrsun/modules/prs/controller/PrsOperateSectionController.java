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
import com.njrsun.modules.prs.domain.PrsOperateSection;
import com.njrsun.modules.prs.service.IPrsOperateSectionService;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 标准工序段Controller
 * 
 * @author njrsun
 * @date 2022-01-10
 */

@RestController
@RequestMapping("/prs/operate")
@Api(tags =  "标准工序段")
public class PrsOperateSectionController extends BaseController
{
    @Autowired
    private IPrsOperateSectionService prsOperateSectionService;

    /**
     * 查询标准工序段列表
     */
    @PreAuthorize("@ss.hasPermi('prs:operate:list')")
    @GetMapping("/list")
    public TableDataInfo list(PrsOperateSection prsOperateSection)
    {
            startPage();
            List<PrsOperateSection> list = prsOperateSectionService.selectPrsOperateSectionList(prsOperateSection);
            return getDataTable(list);
    }

    /**
     * 导出标准工序段列表
     */
    @PreAuthorize("@ss.hasPermi('prs:operate:export')")
    @Log(title = "标准工序段", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PrsOperateSection prsOperateSection)
    {
        List<PrsOperateSection> list = prsOperateSectionService.selectPrsOperateSectionList(prsOperateSection);
        ExcelUtil<PrsOperateSection> util = new ExcelUtil<PrsOperateSection>(PrsOperateSection.class);
        return util.exportExcel(list, "标准工序段");
    }

    /**
     * 获取标准工序段详细信息
     */
    @PreAuthorize("@ss.hasPermi('prs:operate:query')")
    @GetMapping(value = "/{uniqueId}")
    public AjaxResult getInfo(@PathVariable("uniqueId") Long uniqueId)
    {
        return AjaxResult.success(prsOperateSectionService.selectPrsOperateSectionById(uniqueId));
    }

    /**
     * 新增标准工序段
     */
    @PreAuthorize("@ss.hasPermi('prs:operate:add')")
    @Log(title = "标准工序段", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsOperateSection prsOperateSection)
    {
        return toAjax(prsOperateSectionService.insertPrsOperateSection(prsOperateSection));
    }

    /**
     * 修改标准工序段
     */
    @PreAuthorize("@ss.hasPermi('prs:operate:edit')")
    @Log(title = "标准工序段", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsOperateSection prsOperateSection)
    {
        return toAjax(prsOperateSectionService.updatePrsOperateSection(prsOperateSection));
    }

    /**
     * 删除标准工序段
     */
    @PreAuthorize("@ss.hasPermi('prs:operate:remove')")
    @Log(title = "标准工序段", businessType = BusinessType.DELETE)
	@DeleteMapping("/{uniqueIds}")
    public AjaxResult remove(@PathVariable Long[] uniqueIds)
    {
        return toAjax(prsOperateSectionService.deletePrsOperateSectionByIds(uniqueIds));
    }
}
