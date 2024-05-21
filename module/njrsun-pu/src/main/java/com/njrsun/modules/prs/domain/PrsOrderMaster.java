package com.njrsun.modules.prs.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产订单主对象 prs_order_master
 * 
 * @author njrsun
 * @date 2021-11-12
 */
public class PrsOrderMaster extends BaseEntity
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

    private String salesman;
    /** 单据状态 */
    @Excel(name = "单据状态")
    private String invoiceStatus;

    /** 单据类型 */
    @Excel(name = "单据类型")
    private String invoiceType;

    private String ppNumber;
    private Date  ppDate;

    private String mpOrderCode;
    /** 业务状态 */
    @Excel(name = "业务状态")
    private String workStatus;

    /** 业务类型 */
    @Excel(name = "业务类型")
    private String workType;

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    private String process;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    private String processId;

    /** 业务部门 */
    @Excel(name = "业务部门")
    private String workDept;

    /** 业务人员 */
    @Excel(name = "业务人员")
    private String workStaff;

    /** 表单配置 */
    @Excel(name = "表单配置")
    private String formConfig;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date checkDate;

    private String invCode;

    private String invSortRoot;
    private String invSortId;
    private String invName;

    private String invAttribute;

    private String unitCode;

    private String unitName;

    private BigDecimal invQuantity;

    private Date needDate;

    private Long duration;

    /** 操作员 */
    @Excel(name = "操作员")
    private String userOper;

    /** 审核员 */
    @Excel(name = "审核员")
    private String userCheck;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    /** 删除标志 */
    private String delFlag;

    public String getInvSortRoot() {
        return invSortRoot;
    }

    public void setInvSortRoot(String invSortRoot) {
        this.invSortRoot = invSortRoot;
    }

    public String getInvSortId() {
        return invSortId;
    }

    public void setInvSortId(String invSortId) {
        this.invSortId = invSortId;
    }

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    private String beltline;
    private String beltlineId;
    private BigDecimal wiQuantity;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /** 版本号 */
    @Excel(name = "版本号")
    private Long version;

    private String woCode;
    private BigDecimal  woQuantity;
    private String woType;
    private String woTypeId;
    private String woInvoice;
    private String woInvoiceId;
    private String woUniqueId;
      private Date woDate;
    private String woConfig;

    private String drawingNo;
    private Date arrangeDate;


    public String getDrawingNo() {
        return drawingNo;
    }

    public void setDrawingNo(String drawingNo) {
        this.drawingNo = drawingNo;
    }

    public BigDecimal getWoQuantity() {
        return woQuantity;
    }

    public void setWoQuantity(BigDecimal woQuantity) {
        this.woQuantity = woQuantity;
    }

    public String getWoType() {
        return woType;
    }

    public void setWoType(String woType) {
        this.woType = woType;
    }

    public String getWoTypeId() {
        return woTypeId;
    }

    public void setWoTypeId(String woTypeId) {
        this.woTypeId = woTypeId;
    }

    public String getWoInvoice() {
        return woInvoice;
    }

    public void setWoInvoice(String woInvoice) {
        this.woInvoice = woInvoice;
    }

    public String getWoInvoiceId() {
        return woInvoiceId;
    }

    public void setWoInvoiceId(String woInvoiceId) {
        this.woInvoiceId = woInvoiceId;
    }

    public Date getWoDate() {
        return woDate;
    }

    public void setWoDate(Date woDate) {
        this.woDate = woDate;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getSalesman() {
        return salesman;
    }

    public String getWoCode() {
        return woCode;
    }

    public void setWoCode(String woCode) {
        this.woCode = woCode;
    }

    public String getWoUniqueId() {
        return woUniqueId;
    }

    public void setWoUniqueId(String woUniqueId) {
        this.woUniqueId = woUniqueId;
    }

    public String getWoConfig() {
        return woConfig;
    }

    public void setWoConfig(String woConfig) {
        this.woConfig = woConfig;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getInvCode() {
        return invCode;
    }

    public void setInvCode(String invCode) {
        this.invCode = invCode;
    }

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public String getInvAttribute() {
        return invAttribute;
    }

    public void setInvAttribute(String invAttribute) {
        this.invAttribute = invAttribute;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public BigDecimal getInvQuantity() {
        return invQuantity;
    }

    public void setInvQuantity(BigDecimal invQuantity) {
        this.invQuantity = invQuantity;
    }

    public Date getNeedDate() {
        return needDate;
    }

    public void setNeedDate(Date needDate) {
        this.needDate = needDate;
    }

    public Date getArrangeDate() {
        return arrangeDate;
    }

    public void setArrangeDate(Date arrangeDate) {
        this.arrangeDate = arrangeDate;
    }

    public BigDecimal getWiQuantity() {
        return wiQuantity;
    }

    public void setWiQuantity(BigDecimal wiQuantity) {
        this.wiQuantity = wiQuantity;
    }

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
    public void setFormConfig(String formConfig) 
    {
        this.formConfig = formConfig;
    }

    public String getFormConfig() 
    {
        return formConfig;
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

    public String getBeltline() {
        return beltline;
    }

    public void setBeltline(String beltline) {
        this.beltline = beltline;
    }

    public String getBeltlineId() {
        return beltlineId;
    }

    public void setBeltlineId(String beltlineId) {
        this.beltlineId = beltlineId;
    }

    public String getPpNumber() {
        return ppNumber;
    }

    public void setPpNumber(String ppNumber) {
        this.ppNumber = ppNumber;
    }

    public Date getPpDate() {
        return ppDate;
    }

    public void setPpDate(Date ppDate) {
        this.ppDate = ppDate;
    }

    public String getMpOrderCode() {
        return mpOrderCode;
    }

    public void setMpOrderCode(String mpOrderCode) {
        this.mpOrderCode = mpOrderCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("uniqueId", getUniqueId())
            .append("prsCode", getPrsCode())
            .append("invoiceDate", getInvoiceDate())
            .append("invoiceStatus", getInvoiceStatus())
            .append("invoiceType", getInvoiceType())
            .append("workStatus", getWorkStatus())
            .append("workType", getWorkType())
            .append("workDept", getWorkDept())
            .append("workStaff", getWorkStaff())
            .append("formConfig", getFormConfig())
            .append("checkDate", getCheckDate())
            .append("userOper", getUserOper())
            .append("userCheck", getUserCheck())
            .append("remarks", getRemarks())
            .append("delFlag", getDelFlag())
            .append("createDate", getCreateDate())
            .append("createBy", getCreateBy())
            .append("updateDate", getUpdateDate())
            .append("updateBy", getUpdateBy())
            .append("version", getVersion())
            .toString();
    }

    public String getPrsProcessCode() {
        return this.processId;
    }
}
