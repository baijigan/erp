package com.njrsun.modules.prs.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

/**
 * 标准工序段对象 prs_operate_section
 * 
 * @author njrsun
 * @date 2022-01-10
 */
public class PrsOperateSection extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一序号 */
    private Long uniqueId;

    /** 工序代码 */
    @Excel(name = "工序代码")
    private String code;

    /** 工序名称 */
    @Excel(name = "工序名称")
    private String name;

    /** 标准工时 */
    @Excel(name = "标准工时")
    private Long duration;

    /** 是否质检（0否 1是） */
    @Excel(name = "是否质检", readConverterExp = "0=否,1=是")
    private String isQuality;

    /** 计件方式（0班组 1个人） */
    @Excel(name = "计件方式", readConverterExp = "0=班组,1=个人")
    private String countType;

    /** 排序 */
    @Excel(name = "排序")
    private Long orderId;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    public void setUniqueId(Long uniqueId) 
    {
        this.uniqueId = uniqueId;
    }

    public Long getUniqueId() 
    {
        return uniqueId;
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
    public void setDuration(Long duration) 
    {
        this.duration = duration;
    }

    public Long getDuration() 
    {
        return duration;
    }
    public void setIsQuality(String isQuality) 
    {
        this.isQuality = isQuality;
    }

    public String getIsQuality() 
    {
        return isQuality;
    }
    public void setCountType(String countType) 
    {
        this.countType = countType;
    }

    public String getCountType() 
    {
        return countType;
    }
    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setUpdateDate(Date updateDate) 
    {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() 
    {
        return updateDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("uniqueId", getUniqueId())
            .append("code", getCode())
            .append("name", getName())
            .append("duration", getDuration())
            .append("isQuality", getIsQuality())
            .append("countType", getCountType())
            .append("orderId", getOrderId())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("updateDate", getUpdateDate())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
