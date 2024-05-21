package com.njrsun.modules.prs.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

/**
 * 生产线对象 prs_beltline
 * 
 * @author njrsun
 * @date 2021-12-24
 */
public class PrsBeltline extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一序号 */
    private Long uniqueId;

    /** 业务类型 */
    @Excel(name = "业务类型")
    private String workType;

    /** 产线代码 */
    @Excel(name = "产线代码")
    private String code;

    /** 产线名称 */
    @Excel(name = "产线名称")
    private String name;

    /** 工艺路线 */
    @Excel(name = "工艺路线")
    private String processRoute;

    /** 排序 */
    @Excel(name = "排序")
    private Long orderId;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

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
    public void setProcessRoute(String processRoute) 
    {
        this.processRoute = processRoute;
    }

    public String getProcessRoute() 
    {
        return processRoute;
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
            .append("workType", getWorkType())
            .append("code", getCode())
            .append("name", getName())
            .append("processRoute", getProcessRoute())
            .append("orderId", getOrderId())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("updateDate", getUpdateDate())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
