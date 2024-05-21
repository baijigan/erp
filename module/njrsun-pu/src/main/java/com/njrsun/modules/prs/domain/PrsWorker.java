package com.njrsun.modules.prs.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 工人对象 prs_worker
 * 
 * @author njrsun
 * @date 2021-12-23
 */
public class PrsWorker extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一序号 */
    private Long uniqueId;

    /** 班组代码 */
    @Excel(name = "班组代码")
    private String teamCode;

    private String workType;
    @Excel(name = "工人名称")
     private String name;
    /** 工人代码 */
    @Excel(name = "工人代码")
    private String code;
    @Excel(name = "工种")
    private String jobs;
    @Excel(name = "手机号")
    private String mobile;
    /** 工人名称 */

    /** 排序 */
    @Excel(name = "排序")
    private Long orderId;
    @Excel(name = "状态",readConverterExp = "0=正常,1=停用")
    private String status;
    @Excel(name = "备注")
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(dateFormat = "yyyy-MM-dd HH:mm:ss",name = "更新时间")
    private Date updateDate;
    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getJobs() {
        return jobs;
    }

    public void setJobs(String jobs) {
        this.jobs = jobs;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUniqueId(Long uniqueId)
    {
        this.uniqueId = uniqueId;
    }

    public Long getUniqueId() 
    {
        return uniqueId;
    }
    public void setTeamCode(String teamCode) 
    {
        this.teamCode = teamCode;
    }

    public String getTeamCode() 
    {
        return teamCode;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("uniqueId", getUniqueId())
            .append("teamCode", getTeamCode())
            .append("code", getCode())
            .append("name", getName())
            .append("orderId", getOrderId())
            .toString();
    }
}
