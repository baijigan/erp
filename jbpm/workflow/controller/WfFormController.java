package com.njrsun.workflow.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.PageQuery;
import com.njrsun.common.core.domain.R;
import com.njrsun.common.core.page.TableDataInfo;
import com.njrsun.common.core.validate.QueryGroup;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.workflow.domain.WfDeployForm;
import com.njrsun.workflow.domain.bo.WfFormBo;
import com.njrsun.workflow.domain.vo.WfFormVo;
import com.njrsun.workflow.service.IWfDeployFormService;
import com.njrsun.workflow.service.IWfFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 流程表单Controller
 *
 * @author njrsun
 * @createTime 2022/3/7 22:07
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/workflow/form")
public class WfFormController extends BaseController {

    private final IWfFormService formService;
    private final IWfDeployFormService deployFormService;

    /**
     * 查询流程表单列表
     */
    @SaCheckPermission("workflow:form:list")
    @GetMapping("/list")
    public TableDataInfo<WfFormVo> list(@Validated(QueryGroup.class) WfFormBo bo, PageQuery pageQuery) {
        return formService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取流程表单详细信息
     * @param formId 主键
     */
    @SaCheckPermission("workflow:form:query")
    @GetMapping(value = "/{formId}")
    public R<WfFormVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("formId") Long formId) {
        return R.ok(formService.queryById(formId));
    }

    /**
     * 新增流程表单
     */
    @SaCheckPermission("workflow:form:add")
    @Log(title = "流程表单", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@RequestBody WfFormBo bo) {
        return toAjax(formService.insertForm(bo));
    }

    /**
     * 修改流程表单
     */
    @SaCheckPermission("workflow:form:edit")
    @Log(title = "流程表单", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@RequestBody WfFormBo bo) {
        return toAjax(formService.updateForm(bo));
    }

    /**
     * 删除流程表单
     * @param formIds 主键串
     */
    @SaCheckPermission("workflow:form:remove")
    @Log(title = "流程表单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{formIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] formIds) {
        return toAjax(formService.deleteWithValidByIds(Arrays.asList(formIds)) ? 1 : 0);
    }


    /**
     * 挂载流程表单
     */
    @Log(title = "流程表单", businessType = BusinessType.INSERT)
    @PostMapping("/addDeployForm")
    public R<Void> addDeployForm(@RequestBody WfDeployForm deployForm) {
        return toAjax(deployFormService.insertWfDeployForm(deployForm));
    }
}
