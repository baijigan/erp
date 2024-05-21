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
import com.njrsun.modules.prs.domain.PrsBeltline;
import com.njrsun.modules.prs.service.IPrsBeltlineService;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;

/**
 * 生产线Controller
 * 
 * @author njrsun
 * @date 2021-12-24
 */
@RestController
@Api(tags = "生产线")
@RequestMapping("/prs/beltline")
public class PrsBeltlineController extends BaseController
{
    @Autowired
    private IPrsBeltlineService prsBeltlineService;

    /**
     * 查询生产线列表
     */
    @PreAuthorize("@ss.hasPermi('prs:beltline:list')")
    @GetMapping("/list")
    public TableDataInfo list(PrsBeltline prsBeltline)
    {
        startPage();
        List<PrsBeltline> list = prsBeltlineService.selectPrsBeltlineList(prsBeltline);
        return getDataTable(list);
    }

    /**
     * 导出生产线列表
     */
    @PreAuthorize("@ss.hasPermi('prs:beltline:export')")
    @Log(title = "生产线", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PrsBeltline prsBeltline)
    {
        List<PrsBeltline> list = prsBeltlineService.selectPrsBeltlineList(prsBeltline);
        ExcelUtil<PrsBeltline> util = new ExcelUtil<>(PrsBeltline.class);
        return util.exportExcel(list, "beltline");
    }

    /**
     * 获取生产线详细信息
     */
    @PreAuthorize("@ss.hasPermi('prs:beltline:entity:view')")
    @GetMapping(value = "/{uniqueId}")
    public AjaxResult getInfo(@PathVariable("uniqueId") Long uniqueId)
    {
        return AjaxResult.success(prsBeltlineService.selectPrsBeltlineById(uniqueId));
    }

    /**
     * 新增生产线
     */
    @PreAuthorize("@ss.hasPermi('prs:beltline:entity:add')")
    @Log(title = "生产线", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsBeltline prsBeltline)
    {
        return toAjax(prsBeltlineService.insertPrsBeltline(prsBeltline));
    }

    /**
     * 修改生产线
     */
    @PreAuthorize("@ss.hasPermi('prs:beltline:entity:edit')")
    @Log(title = "生产线", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsBeltline prsBeltline)
    {
        return toAjax(prsBeltlineService.updatePrsBeltline(prsBeltline));
    }

    /**
     * 删除生产线
     */
    @PreAuthorize("@ss.hasPermi('prs:beltline:remove')")
    @Log(title = "生产线", businessType = BusinessType.DELETE)
	@DeleteMapping("/{uniqueIds}")
    public AjaxResult remove(@PathVariable Long[] uniqueIds)
    {
        return toAjax(prsBeltlineService.deletePrsBeltlineByIds(uniqueIds));
    }
}
