package com.njrsun.modules.om.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

/**
 * 销售开票从对象 om_fapiao_salve
 * 
 * @author njrsun
 * @date 2022-04-04
 */
public class OmFapiaoSalve extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long uniqueId;

    /** 主表ID */
    @Excel(name = "主表ID")
    private String omCode;

    /** 销售订单号 */
    @Excel(name = "销售订单号")
    private String ppNumber;

    /** 料品大类 */
    @Excel(name = "料品大类")
    private Long invSortRoot;

    /** 料品分类 */
    @Excel(name = "料品分类")
    private Long invSortId;

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

    /** 开票数量 */
    @Excel(name = "开票数量")
    private BigDecimal vatQuantity;

    /** 开票名称 */
    @Excel(name = "开票名称")
    private String vatName;

    /** 开票单价 */
    @Excel(name = "开票单价")
    private BigDecimal vatPrice;

    /** 开票金额 */
    @Excel(name = "开票金额")
    private BigDecimal vatAmount;

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

    /** 删除标记 */
    private String delFlag;

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
    public void setPpNumber(String ppNumber) 
    {
        this.ppNumber = ppNumber;
    }

    public String getPpNumber() 
    {
        return ppNumber;
    }
    public void setInvSortRoot(Long invSortRoot) 
    {
        this.invSortRoot = invSortRoot;
    }

    public Long getInvSortRoot() 
    {
        return invSortRoot;
    }
    public void setInvSortId(Long invSortId) 
    {
        this.invSortId = invSortId;
    }

    public Long getInvSortId() 
    {
        return invSortId;
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
    public void setVatQuantity(BigDecimal vatQuantity) 
    {
        this.vatQuantity = vatQuantity;
    }

    public BigDecimal getVatQuantity() 
    {
        return vatQuantity;
    }
    public void setVatName(String vatName) 
    {
        this.vatName = vatName;
    }

    public String getVatName() 
    {
        return vatName;
    }
    public void setVatPrice(BigDecimal vatPrice) 
    {
        this.vatPrice = vatPrice;
    }

    public BigDecimal getVatPrice() 
    {
        return vatPrice;
    }
    public void setVatAmount(BigDecimal vatAmount) 
    {
        this.vatAmount = vatAmount;
    }

    public BigDecimal getVatAmount() 
    {
        return vatAmount;
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
            .append("omCode", getOmCode())
            .append("ppNumber", getPpNumber())
            .append("invSortRoot", getInvSortRoot())
            .append("invSortId", getInvSortId())
            .append("invCode", getInvCode())
            .append("invName", getInvName())
            .append("invAttribute", getInvAttribute())
            .append("unitCode", getUnitCode())
            .append("unitName", getUnitName())
            .append("vatQuantity", getVatQuantity())
            .append("vatName", getVatName())
            .append("vatPrice", getVatPrice())
            .append("vatAmount", getVatAmount())
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
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
