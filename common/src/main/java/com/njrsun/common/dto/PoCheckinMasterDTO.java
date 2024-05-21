package com.njrsun.common.dto;

import com.njrsun.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * @author njrsun
 * @create 2021/10/26 17:06
 */

public class PoCheckinMasterDTO {
    private static final long serialVersionUID = 1L;

    /** 单据序号 */
    private Long uniqueId;

    /** 单据编码 */
    @Excel(name = "单据编码")
    private String poCode;

    /** 单据日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

    private String formConfig;

    /** 业务类型 */
    @Excel(name = "业务类型")
    private String workType;

    /** 采购部门 */
    @Excel(name = "采购部门")
    private String workDept;

    /** 采购人员 */
    @Excel(name = "采购人员")
    private String workStaff;

    private String supplyType;
    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

    /** 供方厂家 */
    @Excel(name = "供方厂家")
    private String supplier;

    /** 供方厂家 */
    @Excel(name = "供方厂家")
    private String supplierId;

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

    private String reason;

    private String reasonId;

    /** 采购到货单子信息 */
    private List<PoCheckinSalveDTO> poCheckinSalveList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private  Date createTime;

    private String createBy;

    private Boolean type;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getFormConfig() {
        return formConfig;
    }

    public void setFormConfig(String formConfig) {
        this.formConfig = formConfig;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getWorkDept() {
        return workDept;
    }

    public void setWorkDept(String workDept) {
        this.workDept = workDept;
    }

    public String getWorkStaff() {
        return workStaff;
    }

    public void setWorkStaff(String workStaff) {
        this.workStaff = workStaff;
    }

    public String getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(String supplyType) {
        this.supplyType = supplyType;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public String getUserOper() {
        return userOper;
    }

    public void setUserOper(String userOper) {
        this.userOper = userOper;
    }

    public String getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(String userCheck) {
        this.userCheck = userCheck;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReasonId() {
        return reasonId;
    }

    public void setReasonId(String reasonId) {
        this.reasonId = reasonId;
    }

    public List<PoCheckinSalveDTO> getPoCheckinSalveList() {
        return poCheckinSalveList;
    }

    public void setPoCheckinSalveList(List<PoCheckinSalveDTO> poCheckinSalveList) {
        this.poCheckinSalveList = poCheckinSalveList;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "PoCheckinMasterDTO{" +
                "uniqueId=" + uniqueId +
                ", poCode='" + poCode + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", invoiceStatus='" + invoiceStatus + '\'' +
                ", invoiceType='" + invoiceType + '\'' +
                ", workStatus='" + workStatus + '\'' +
                ", formConfig='" + formConfig + '\'' +
                ", workType='" + workType + '\'' +
                ", workDept='" + workDept + '\'' +
                ", workStaff='" + workStaff + '\'' +
                ", supplyType='" + supplyType + '\'' +
                ", checkDate=" + checkDate +
                ", userOper='" + userOper + '\'' +
                ", userCheck='" + userCheck + '\'' +
                ", remarks='" + remarks + '\'' +
                ", supplier='" + supplier + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", version=" + version +
                ", reason='" + reason + '\'' +
                ", reasonId='" + reasonId + '\'' +
                ", poCheckinSalveList=" + poCheckinSalveList +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", createTime=" + createTime +
                ", createBy='" + createBy + '\'' +
                ", type=" + type +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
