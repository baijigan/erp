package com.njrsun.modules.prs.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

/**
 * 生产入库单主对象 prs_checkin_master
 * 
 * @author njrsun
 * @date 2022-01-18
 */
public class PrsCheckinMaster extends BaseEntity
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

    /** 入库仓库 */
    @Excel(name = "入库仓库")
    private String warehouse;

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

    /** 生产入库单从信息 */
    private List<PrsCheckinSalve> prsCheckinSalveList;

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
    public void setWarehouse(String warehouse) 
    {
        this.warehouse = warehouse;
    }

    public String getWarehouse() 
    {
        return warehouse;
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

    public List<PrsCheckinSalve> getPrsCheckinSalveList()
    {
        return prsCheckinSalveList;
    }

    public void setPrsCheckinSalveList(List<PrsCheckinSalve> prsCheckinSalveList)
    {
        this.prsCheckinSalveList = prsCheckinSalveList;
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
            .append("warehouse", getWarehouse())
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
            .append("prsCheckinSalveList", getPrsCheckinSalveList())
            .toString();
    }
}
