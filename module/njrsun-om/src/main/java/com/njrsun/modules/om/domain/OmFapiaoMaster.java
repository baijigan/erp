package com.njrsun.modules.om.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

/**
 * 销售开票主对象 om_fapiao_master
 * 
 * @author njrsun
 * @date 2022-04-04
 */
public class OmFapiaoMaster extends BaseEntity
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

    /** 业务部门 */
    @Excel(name = "业务部门")
    private String workDept;

    /** 业务人员 */
    @Excel(name = "业务人员")
    private String workStaff;

    /** 表单配置 */
    @Excel(name = "表单配置")
    private String formConfig;

    /** 客户名称 */
    @Excel(name = "客户名称")
    private String customer;

    /** 客户编号 */
    @Excel(name = "客户编号")
    private String customerId;

    /** 客户开票名称 */
    @Excel(name = "客户开票名称")
    private String customerFp;

    /** 发票号码 */
    @Excel(name = "发票号码")
    private String vatNo;

    /** 开户行 */
    @Excel(name = "开户行")
    private String bank;

    /** 账号 */
    @Excel(name = "账号")
    private String account;

    /** 税号 */
    @Excel(name = "税号")
    private String tax;

    /** 地址电话 */
    @Excel(name = "地址电话")
    private String addr;

    /** 发票类型 */
    @Excel(name = "发票类型", readConverterExp = "0=专票,1=普票")
    private String vatType;

    /** 蓝字红字 */
    @Excel(name = "蓝字红字", readConverterExp = "0=蓝字,1=红字")
    private Integer bredVouch;

    /** 原发票号码 */
    @Excel(name = "原发票号码")
    private String bredNo;

    /** 发票金额 */
    @Excel(name = "发票金额")
    private BigDecimal vatAmount;

    /** 税额 */
    @Excel(name = "税额")
    private BigDecimal vatTax;

    /** 价税合计 */
    @Excel(name = "价税合计")
    private BigDecimal vatTotal;

    /** 税率 */
    @Excel(name = "税率")
    private String vatRate;

    /** 发票状态（0录入  1开具  2驳回） */
    @Excel(name = "发票状态", readConverterExp = "0=录入,1=开具,2=驳回")
    private String vatStatus;

    /** 会计期间 */
    @Excel(name = "会计期间")
    private Integer fdPeriod;

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

    /** 销售开票从信息 */
    private List<OmFapiaoSalve> omFapiaoSalveList;

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
    public void setFormConfig(String formConfig) 
    {
        this.formConfig = formConfig;
    }

    public String getFormConfig() 
    {
        return formConfig;
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
    public void setCustomerFp(String customerFp) 
    {
        this.customerFp = customerFp;
    }

    public String getCustomerFp() 
    {
        return customerFp;
    }
    public void setBank(String bank) 
    {
        this.bank = bank;
    }

    public String getBank() 
    {
        return bank;
    }
    public void setAccount(String account) 
    {
        this.account = account;
    }

    public String getAccount() 
    {
        return account;
    }
    public void setTax(String tax) 
    {
        this.tax = tax;
    }

    public String getTax() 
    {
        return tax;
    }

    public void setAddr(String addr) 
    {
        this.addr = addr;
    }

    public String getAddr() 
    {
        return addr;
    }
    public void setVatRate(String vatRate) 
    {
        this.vatRate = vatRate;
    }

    public String getVatRate() 
    {
        return vatRate;
    }
    public void setVatStatus(String vatStatus) 
    {
        this.vatStatus = vatStatus;
    }

    public String getVatStatus() 
    {
        return vatStatus;
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

    public List<OmFapiaoSalve> getOmFapiaoSalveList()
    {
        return omFapiaoSalveList;
    }

    public void setOmFapiaoSalveList(List<OmFapiaoSalve> omFapiaoSalveList)
    {
        this.omFapiaoSalveList = omFapiaoSalveList;
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
            .append("formConfig", getFormConfig())
            .append("customer", getCustomer())
            .append("customerId", getCustomerId())
            .append("customerFp", getCustomerFp())
            .append("bank", getBank())
            .append("account", getAccount())
            .append("tax", getTax())
            .append("addr", getAddr())
            .append("vatRate", getVatRate())
            .append("vatStatus", getVatStatus())
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
            .append("omFapiaoSalveList", getOmFapiaoSalveList())
            .toString();
    }

    public Integer getFdPeriod() {
        return fdPeriod;
    }

    public void setFdPeriod(Integer fdPeriod) {
        this.fdPeriod = fdPeriod;
    }

    public String getVatType() {
        return vatType;
    }

    public void setVatType(String vatType) {
        this.vatType = vatType;
    }

    public Integer getBredVouch() {
        return bredVouch;
    }

    public void setBredVouch(Integer bredVouch) {
        this.bredVouch = bredVouch;
    }

    public String getBredNo() {
        return bredNo;
    }

    public void setBredNo(String bredNo) {
        this.bredNo = bredNo;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    public BigDecimal getVatTax() {
        return vatTax;
    }

    public void setVatTax(BigDecimal vatTax) {
        this.vatTax = vatTax;
    }

    public BigDecimal getVatTotal() {
        return vatTotal;
    }

    public void setVatTotal(BigDecimal vatTotal) {
        this.vatTotal = vatTotal;
    }

    public String getVatNo() {
        return vatNo;
    }

    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }
}
