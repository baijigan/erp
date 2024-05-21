package com.njrsun.common.core.domain.entity;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 物料单据对象 inv_form
 * 
 * @author njrsun
 * @date 2021-08-10
 */

public class SysForm extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分类id */
    private Long formId;

    /** 父类id */
    @Excel(name = "父类id")
    private Long parentId;

    /** 父级列表 */
    @Excel(name = "父级列表")
    private String ancestors;

    /** 单据名称 */
    @Excel(name = "单据名称")
    private String formName;

    /** 单据代码 */
    @Excel(name = "单据代码")
    private String formCode;

    @Excel(name = "完整路径")
    private String formPath;

    /** 单据状态（0正常 1停用） */
    @Excel(name = "单据状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0存在 1删除） */
    private String delFlag;

    /** 版本号 */
    @Excel(name = "版本号")
    private Long version;

   private List<SysForm> children = new ArrayList<>();

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(String ancestors) {
        this.ancestors = ancestors;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormCode() {
        return formCode;
    }

    public void setFormCode(String formCode) {
        this.formCode = formCode;
    }

    public String getFormPath() {
        return formPath;
    }

    public void setFormPath(String formPath) {
        this.formPath = formPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<SysForm> getChildren() {
        return children;
    }

    public void setChildren(List<SysForm> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "SysForm{" +
                "formId=" + formId +
                ", parentId=" + parentId +
                ", ancestors='" + ancestors + '\'' +
                ", formName='" + formName + '\'' +
                ", formCode='" + formCode + '\'' +
                ", formPath='" + formPath + '\'' +
                ", status='" + status + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", version=" + version +
                ", children=" + children +
                '}';
    }
}
