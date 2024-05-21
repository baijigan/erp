package com.njrsun.modules.prs.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

/**
 * 班组对象 prs_team
 * 
 * @author njrsun
 * @date 2021-12-23
 */
public class PrsTeam extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一序号 */
    private Long uniqueId;

    /** 业务类型 */
    @Excel(name = "业务类型")
    private String workType;


    @Excel(name = "创建者")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
    @Excel(name = "更新者")
    private String updateBy;
    /** 班组代码 */
    @Excel(name = "班组代码")
    private String code;

    /** 班组名称 */
    @Excel(name = "班组名称")
    private String name;
    @Excel(name = "状态",readConverterExp = "0=正常,1=停用")
    private String status;
    /** 排序 */
    @Excel(name = "排序")
    private Long orderId;

    public void setUniqueId(Long uniqueId)
    {
        this.uniqueId = uniqueId;
    }

    public Long getUniqueId() 
    {
        return uniqueId;
    }
    public void setWorkType(String workType) 
    {
        this.workType = workType;
    }

    public String getWorkType() 
    {
        return workType;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String getUpdateBy() {
        return updateBy;
    }

    @Override
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("uniqueId", getUniqueId())
            .append("workType", getWorkType())
            .append("code", getCode())
            .append("name", getName())
            .append("orderId", getOrderId())
            .toString();
    }
}
