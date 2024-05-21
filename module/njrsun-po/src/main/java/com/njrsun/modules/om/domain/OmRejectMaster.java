package com.njrsun.modules.om.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

/**
 * 销售订单主对象 om_reject_master
 * 
 * @author njrsun
 * @date 2021-08-31
 */
public class OmRejectMaster extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 单据序号 */
    private Long uniqueId;

    /** 单据编码 */
    @Excel(name = "单据编码")
    private String omCode;

    /** 单据日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date invoiceDate;

    /** 单据状态 */
    @Excel(name = "单据状态")
    private String invoiceStatus;

    /** 单据类型 */
    @Excel(name = "单据类型")
    private String invoiceType;

    /** 业务状态 */
    @Excel(name = "业务状态")
    private String workStatus;

    /** 业务类型 */
    @Excel(name = "业务类型")
    private String workType;

    /** 销售部门 */
    @Excel(name = "销售部门")
    private String workDept;

    /** 销售人员 */
    @Excel(name = "销售人员")
    private String workStaff;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date checkDate;

    /** 操作员 */
    @Excel(name = "操作员")
    private String userOper;

    /** 审核员 */
    @Excel(name = "审核员")
    private String userCheck;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    /** 客户名称 */
    @Excel(name = "客户名称")
    private String customer;

    /** 客户编号 */
    @Excel(name = "客户编号")
    private String customerId;

    /** 交付日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "交付日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deliverDate;

    /** 退货仓库 */
    @Excel(name = "退货仓库")
    private String whCode;

    /** 退货原因 */
    @Excel(name = "退货原因")
    private String reason;

    private String reasonId;
    /** 删除标志 */
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

    private Date startDate;

    private Date endDate;

    private String pp;

    private Boolean type;

    private String warehouse;
    private String formConfig;

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    /** 销售退货从信息 */
    private List<OmRejectSalve> omRejectSalveList;

    public String getPp() {
        return pp;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setUniqueId(Long uniqueId)
    {
        this.uniqueId = uniqueId;
    }

    public Long getUniqueId() 
    {
        return uniqueId;
    }
    public void setOmCode(String omCode) 
    {
        this.omCode = omCode;
    }

    public String getOmCode() 
    {
        return omCode;
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
    public void setInvoiceType(String invoiceType) 
    {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceType() 
    {
        return invoiceType;
    }
    public void setWorkStatus(String workStatus) 
    {
        this.workStatus = workStatus;
    }

    public String getWorkStatus() 
    {
        return workStatus;
    }
    public void setWorkType(String workType) 
    {
        this.workType = workType;
    }

    public String getWorkType() 
    {
        return workType;
    }
    public void setWorkDept(String workDept) 
    {
        this.workDept = workDept;
    }

    public String getWorkDept() 
    {
        return workDept;
    }
    public void setWorkStaff(String workStaff) 
    {
        this.workStaff = workStaff;
    }

    public String getWorkStaff() 
    {
        return workStaff;
    }
    public void setCheckDate(Date checkDate) 
    {
        this.checkDate = checkDate;
    }

    public Date getCheckDate() 
    {
        return checkDate;
    }
    public void setUserOper(String userOper) 
    {
        this.userOper = userOper;
    }

    public String getUserOper() 
    {
        return userOper;
    }
    public void setUserCheck(String userCheck) 
    {
        this.userCheck = userCheck;
    }

    public String getUserCheck() 
    {
        return userCheck;
    }
    public void setRemarks(String remarks) 
    {
        this.remarks = remarks;
    }

    public String getRemarks() 
    {
        return remarks;
    }
    public void setCustomer(String customer) 
    {
        this.customer = customer;
    }

    public String getCustomer() 
    {
        return customer;
    }
    public void setCustomerId(String customerId) 
    {
        this.customerId = customerId;
    }

    public String getCustomerId() 
    {
        return customerId;
    }
    public void setDeliverDate(Date deliverDate) 
    {
        this.deliverDate = deliverDate;
    }

    public Date getDeliverDate() 
    {
        return deliverDate;
    }
    public void setWhCode(String whCode) 
    {
        this.whCode = whCode;
    }

    public String getWhCode() 
    {
        return whCode;
    }
    public void setReason(String reason) 
    {
        this.reason = reason;
    }

    public String getReason() 
    {
        return reason;
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

    public List<OmRejectSalve> getOmRejectSalveList()
    {
        return omRejectSalveList;
    }

    public void setOmRejectSalveList(List<OmRejectSalve> omRejectSalveList)
    {
        this.omRejectSalveList = omRejectSalveList;
    }

    public String getReasonId() {
        return reasonId;
    }

    public void setReasonId(String reasonId) {
        this.reasonId = reasonId;
    }

    public String getFormConfig() {
        return formConfig;
    }

    public void setFormConfig(String formConfig) {
        this.formConfig = formConfig;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("uniqueId", getUniqueId())
            .append("omCode", getOmCode())
            .append("invoiceDate", getInvoiceDate())
            .append("invoiceStatus", getInvoiceStatus())
            .append("invoiceType", getInvoiceType())
            .append("workStatus", getWorkStatus())
            .append("workType", getWorkType())
            .append("workDept", getWorkDept())
            .append("workStaff", getWorkStaff())
            .append("checkDate", getCheckDate())
            .append("userOper", getUserOper())
            .append("userCheck", getUserCheck())
            .append("remarks", getRemarks())
            .append("customer", getCustomer())
            .append("customerId", getCustomerId())
            .append("deliverDate", getDeliverDate())
            .append("whCode", getWhCode())
            .append("reason", getReason())
            .append("delFlag", getDelFlag())
            .append("createDate", getCreateDate())
            .append("createBy", getCreateBy())
            .append("updateDate", getUpdateDate())
            .append("updateBy", getUpdateBy())
            .append("version", getVersion())
            .append("omRejectSalveList", getOmRejectSalveList())
            .toString();
    }
}
