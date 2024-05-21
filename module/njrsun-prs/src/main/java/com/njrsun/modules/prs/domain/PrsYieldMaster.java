package com.njrsun.modules.prs.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;

/**
 * 生产报工单主对象 prs_yield_master
 * 
 * @author njrsun
 * @date 2022-01-13
 */
public class PrsYieldMaster extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 单据序号 */
    private Long uniqueId;

    /** 单据编码 */
    @Excel(name = "单据编码")
    private String prsCode;

    /** 单据日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date invoiceDate;

    /** 单据状态 */
    @Excel(name = "单据状态")
    private String invoiceStatus;

    /** 生产订单号 */
    @Excel(name = "生产订单号")
    private String prsOrderCode;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    /** 工艺路线编码 */
    @Excel(name = "工艺路线编码")
    private String processId;

    /** 工艺路线名称 */
    @Excel(name = "工艺路线名称")
    private String process;

    /** 删除标记 */
    private String delFlag;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /** 版本号 */
    @Excel(name = "版本号")
    private Long version;

    /** 生产报工单子信息 */
    private List<PrsYieldSalve> prsYieldSalveList;

    public void setUniqueId(Long uniqueId) 
    {
        this.uniqueId = uniqueId;
    }

    public Long getUniqueId() 
    {
        return uniqueId;
    }
    public void setPrsCode(String prsCode) 
    {
        this.prsCode = prsCode;
    }

    public String getPrsCode() 
    {
        return prsCode;
    }
    public void setInvoiceDate(Date invoiceDate) 
    {
        this.invoiceDate = invoiceDate;
    }

    public Date getInvoiceDate() 
    {
        return invoiceDate;
    }
    public void setInvoiceStatus(String invoiceStatus) 
    {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceStatus() 
    {
        return invoiceStatus;
    }
    public void setPrsOrderCode(String prsOrderCode) 
    {
        this.prsOrderCode = prsOrderCode;
    }

    public String getPrsOrderCode() 
    {
        return prsOrderCode;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }
    public void setCreateDate(Date createDate) 
    {
        this.createDate = createDate;
    }

    public Date getCreateDate() 
    {
        return createDate;
    }
    public void setUpdateDate(Date updateDate) 
    {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() 
    {
        return updateDate;
    }
    public void setVersion(Long version) 
    {
        this.version = version;
    }

    public Long getVersion() 
    {
        return version;
    }

    public List<PrsYieldSalve> getPrsYieldSalveList()
    {
        return prsYieldSalveList;
    }

    public void setPrsYieldSalveList(List<PrsYieldSalve> prsYieldSalveList)
    {
        this.prsYieldSalveList = prsYieldSalveList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("uniqueId", getUniqueId())
            .append("prsCode", getPrsCode())
            .append("invoiceDate", getInvoiceDate())
            .append("invoiceStatus", getInvoiceStatus())
            .append("prsOrderCode", getPrsOrderCode())
            .append("delFlag", getDelFlag())
            .append("createDate", getCreateDate())
            .append("createBy", getCreateBy())
            .append("updateDate", getUpdateDate())
            .append("updateBy", getUpdateBy())
            .append("version", getVersion())
            .append("prsYieldSalveList", getPrsYieldSalveList())
            .toString();
    }
}
