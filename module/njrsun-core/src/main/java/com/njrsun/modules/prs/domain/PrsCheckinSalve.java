package com.njrsun.modules.prs.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

/**
 * 生产入库单从对象 prs_checkin_salve
 * 
 * @author njrsun
 * @date 2022-01-18
 */
public class PrsCheckinSalve extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long uniqueId;

    /** 主表ID */
    @Excel(name = "主表ID")
    private String prsCode;

    /** 生产计划编码 */
    @Excel(name = "生产计划编码")
    private String mpOrderCode;

    /** 生产订单编码 */
    @Excel(name = "生产订单编码")
    private String prsOrderCode;

    /** 销售订单号 */
    @Excel(name = "销售订单号")
    private String ppNumber;

    /** 料品编码 */
    @Excel(name = "料品编码")
    private String invCode;

    /** 料品名称 */
    @Excel(name = "料品名称")
    private String invName;

    /** 型号规格 */
    @Excel(name = "型号规格")
    private String invAttribute;

    /** 计量单位编码 */
    @Excel(name = "计量单位编码")
    private String unitCode;

    /** 计量单位名称 */
    @Excel(name = "计量单位名称")
    private String unitName;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal quantity;

    /** 批次编号 */
    @Excel(name = "批次编号")
    private String batchNumber;

    /** 上游单据配置 */
    @Excel(name = "上游单据配置")
    private String woConfig;

    /** 上游单据类型 */
    @Excel(name = "上游单据类型")
    private String woInvoice;

    /** 上游单据代号 */
    @Excel(name = "上游单据代号")
    private String woInvoiceId;

    /** 上游业务类型 */
    @Excel(name = "上游业务类型")
    private String woType;

    /** 上游业务代号 */
    @Excel(name = "上游业务代号")
    private String woTypeId;

    /** 上游日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上游日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date woDate;

    /** 上游编码 */
    @Excel(name = "上游编码")
    private String woCode;

    /** 上游物料 */
    @Excel(name = "上游物料")
    private Long woUniqueId;

    /** 上游余量 */
    @Excel(name = "上游余量")
    private BigDecimal woQuantity;

    /** 下游蓝字数量 */
    @Excel(name = "下游蓝字数量")
    private BigDecimal wiQuantity;

    /** 下游红字数量 */
    @Excel(name = "下游红字数量")
    private BigDecimal wiQuantityR;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    /** 属性1 */
    @Excel(name = "属性1")
    private String f1;

    /** 属性2 */
    @Excel(name = "属性2")
    private String f2;

    /** 属性3 */
    @Excel(name = "属性3")
    private String f3;

    /** 属性4 */
    @Excel(name = "属性4")
    private String f4;

    /** 属性5 */
    @Excel(name = "属性5")
    private String f5;

    private String beltline;

    private String beltlineId;

    private String invSortRoot;
    private String invSortId;
    /** 删除标记 */
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
    public void setMpOrderCode(String mpOrderCode) 
    {
        this.mpOrderCode = mpOrderCode;
    }

    public String getMpOrderCode() 
    {
        return mpOrderCode;
    }
    public void setPrsOrderCode(String prsOrderCode) 
    {
        this.prsOrderCode = prsOrderCode;
    }

    public String getPrsOrderCode() 
    {
        return prsOrderCode;
    }
    public void setPpNumber(String ppNumber) 
    {
        this.ppNumber = ppNumber;
    }

    public String getPpNumber() 
    {
        return ppNumber;
    }
    public void setInvCode(String invCode) 
    {
        this.invCode = invCode;
    }

    public String getInvCode() 
    {
        return invCode;
    }
    public void setInvName(String invName) 
    {
        this.invName = invName;
    }

    public String getInvName() 
    {
        return invName;
    }
    public void setInvAttribute(String invAttribute) 
    {
        this.invAttribute = invAttribute;
    }

    public String getInvAttribute() 
    {
        return invAttribute;
    }
    public void setUnitCode(String unitCode) 
    {
        this.unitCode = unitCode;
    }

    public String getUnitCode() 
    {
        return unitCode;
    }
    public void setUnitName(String unitName) 
    {
        this.unitName = unitName;
    }

    public String getUnitName() 
    {
        return unitName;
    }
    public void setQuantity(BigDecimal quantity) 
    {
        this.quantity = quantity;
    }

    public BigDecimal getQuantity() 
    {
        return quantity;
    }
    public void setBatchNumber(String batchNumber) 
    {
        this.batchNumber = batchNumber;
    }

    public String getBatchNumber() 
    {
        return batchNumber;
    }
    public void setWoConfig(String woConfig) 
    {
        this.woConfig = woConfig;
    }

    public String getWoConfig() 
    {
        return woConfig;
    }
    public void setWoInvoice(String woInvoice) 
    {
        this.woInvoice = woInvoice;
    }

    public String getWoInvoice() 
    {
        return woInvoice;
    }
    public void setWoInvoiceId(String woInvoiceId) 
    {
        this.woInvoiceId = woInvoiceId;
    }

    public String getWoInvoiceId() 
    {
        return woInvoiceId;
    }
    public void setWoType(String woType) 
    {
        this.woType = woType;
    }

    public String getWoType() 
    {
        return woType;
    }
    public void setWoTypeId(String woTypeId) 
    {
        this.woTypeId = woTypeId;
    }

    public String getWoTypeId() 
    {
        return woTypeId;
    }
    public void setWoDate(Date woDate) 
    {
        this.woDate = woDate;
    }

    public Date getWoDate() 
    {
        return woDate;
    }
    public void setWoCode(String woCode) 
    {
        this.woCode = woCode;
    }

    public String getWoCode() 
    {
        return woCode;
    }
    public void setWoUniqueId(Long woUniqueId) 
    {
        this.woUniqueId = woUniqueId;
    }

    public Long getWoUniqueId() 
    {
        return woUniqueId;
    }
    public void setWoQuantity(BigDecimal woQuantity) 
    {
        this.woQuantity = woQuantity;
    }

    public BigDecimal getWoQuantity() 
    {
        return woQuantity;
    }
    public void setWiQuantity(BigDecimal wiQuantity) 
    {
        this.wiQuantity = wiQuantity;
    }

    public BigDecimal getWiQuantity() 
    {
        return wiQuantity;
    }
    public void setWiQuantityR(BigDecimal wiQuantityR) 
    {
        this.wiQuantityR = wiQuantityR;
    }

    public BigDecimal getWiQuantityR() 
    {
        return wiQuantityR;
    }
    public void setRemarks(String remarks) 
    {
        this.remarks = remarks;
    }

    public String getRemarks() 
    {
        return remarks;
    }
    public void setF1(String f1) 
    {
        this.f1 = f1;
    }

    public String getF1() 
    {
        return f1;
    }
    public void setF2(String f2) 
    {
        this.f2 = f2;
    }

    public String getF2() 
    {
        return f2;
    }
    public void setF3(String f3) 
    {
        this.f3 = f3;
    }

    public String getF3() 
    {
        return f3;
    }
    public void setF4(String f4) 
    {
        this.f4 = f4;
    }

    public String getF4() 
    {
        return f4;
    }
    public void setF5(String f5) 
    {
        this.f5 = f5;
    }

    public String getF5() 
    {
        return f5;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("uniqueId", getUniqueId())
            .append("prsCode", getPrsCode())
            .append("mpOrderCode", getMpOrderCode())
            .append("prsOrderCode", getPrsOrderCode())
            .append("ppNumber", getPpNumber())
            .append("invCode", getInvCode())
            .append("invName", getInvName())
            .append("invAttribute", getInvAttribute())
            .append("unitCode", getUnitCode())
            .append("unitName", getUnitName())
            .append("quantity", getQuantity())
            .append("batchNumber", getBatchNumber())
            .append("woConfig", getWoConfig())
            .append("woInvoice", getWoInvoice())
            .append("woInvoiceId", getWoInvoiceId())
            .append("woType", getWoType())
            .append("woTypeId", getWoTypeId())
            .append("woDate", getWoDate())
            .append("woCode", getWoCode())
            .append("woUniqueId", getWoUniqueId())
            .append("woQuantity", getWoQuantity())
            .append("wiQuantity", getWiQuantity())
            .append("wiQuantityR", getWiQuantityR())
            .append("remarks", getRemarks())
            .append("f1", getF1())
            .append("f2", getF2())
            .append("f3", getF3())
            .append("f4", getF4())
            .append("f5", getF5())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
