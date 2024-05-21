package com.njrsun.modules.om.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

/**
 * 销售退货从对象 om_reject_salve
 * 
 * @author njrsun
 * @date 2021-08-31
 */
public class OmRejectSalve extends BaseEntity
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

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal quantity;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal price;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 税率 */
    @Excel(name = "税率")
    private Long tax;

    /** 开票名称 */
    @Excel(name = "开票名称")
    private String billName;

    /** 包装类型 */
    @Excel(name = "包装类型")
    private String pkgType;

    /** 上游单据 */
    @Excel(name = "上游单据")
    private String woCode;



    private String woInvoice;

    private String woInvoiceId;


    private String f1;
    private String f2;
    private String f3;
    private String f4;
    private String f5;

    /** 上游物料 */
    @Excel(name = "上游物料")
    private Long woUniqueId;

    /** 上游数量 */
    @Excel(name = "上游数量")
    private BigDecimal woQuantity;

    /** 下游数量 */
    @Excel(name = "下游数量")
    private BigDecimal wiQuantityR;
    /** 下游数量 */
    @Excel(name = "下游数量")
    private BigDecimal wiQuantity;

    private String woConfig;

    private String batchNumber;
    /** 备注 */
    @Excel(name = "备注")
    private String remarks;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String woDate;
    private String woType;

    private String woTypeId;

    /** 删除标记 */
    private String delFlag;


    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public String getF2() {
        return f2;
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public String getF3() {
        return f3;
    }

    public void setF3(String f3) {
        this.f3 = f3;
    }

    public String getF4() {
        return f4;
    }

    public void setF4(String f4) {
        this.f4 = f4;
    }

    public String getF5() {
        return f5;
    }

    public void setF5(String f5) {
        this.f5 = f5;
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
    public void setQuantity(BigDecimal quantity) 
    {
        this.quantity = quantity;
    }

    public BigDecimal getQuantity() 
    {
        return quantity;
    }
    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setTax(Long tax) 
    {
        this.tax = tax;
    }

    public Long getTax() 
    {
        return tax;
    }
    public void setBillName(String billName) 
    {
        this.billName = billName;
    }

    public String getBillName() 
    {
        return billName;
    }
    public void setPkgType(String pkgType) 
    {
        this.pkgType = pkgType;
    }

    public String getPkgType() 
    {
        return pkgType;
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

    public BigDecimal getWiQuantity() {
        return wiQuantity;
    }

    public void setWiQuantity(BigDecimal wiQuantity) {
        this.wiQuantity = wiQuantity;
    }

    public String getWoConfig() {
        return woConfig;
    }

    public void setWoConfig(String woConfig) {
        this.woConfig = woConfig;
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

    public BigDecimal getWiQuantityR() {
        return wiQuantityR;
    }

    public void setWiQuantityR(BigDecimal wiQuantityR) {
        this.wiQuantityR = wiQuantityR;
    }


    public String getWoDate() {
        return woDate;
    }

    public void setWoDate(String woDate) {
        this.woDate = woDate;
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

    @Override
    public String toString() {
        return "OmRejectSalve{" +
                "uniqueId=" + uniqueId +
                ", omCode='" + omCode + '\'' +
                ", ppNumber='" + ppNumber + '\'' +
                ", invSortRoot=" + invSortRoot +
                ", invSortId=" + invSortId +
                ", invCode='" + invCode + '\'' +
                ", invName='" + invName + '\'' +
                ", invAttribute='" + invAttribute + '\'' +
                ", unitCode='" + unitCode + '\'' +
                ", unitName='" + unitName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", amount=" + amount +
                ", tax=" + tax +
                ", billName='" + billName + '\'' +
                ", pkgType='" + pkgType + '\'' +
                ", woCode='" + woCode + '\'' +
                ", woInvoice='" + woInvoice + '\'' +
                ", woInvoiceId='" + woInvoiceId + '\'' +
                ", woUniqueId=" + woUniqueId +
                ", woQuantity=" + woQuantity +
                ", wiQuantityR=" + wiQuantityR +
                ", wiQuantity=" + wiQuantity +
                ", woConfig='" + woConfig + '\'' +
                ", remarks='" + remarks + '\'' +
                ", woDate='" + woDate + '\'' +
                ", woType='" + woType + '\'' +
                ", woTypeId='" + woTypeId + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
