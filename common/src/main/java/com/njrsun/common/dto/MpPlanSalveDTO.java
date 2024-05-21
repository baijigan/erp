package com.njrsun.common.dto;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/10/14 15:16
 */

public class MpPlanSalveDTO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long uniqueId;

    /** 主表ID */
    @Excel(name = "主表ID")
    private String mpCode;

    private String poCode;
    private String salesman;

    private String ppNumber;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ppDate;

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

    /** 采购数量 */
    @Excel(name = "采购数量")
    private BigDecimal quantity;

    private BigDecimal amount;

    private BigDecimal price;

    /** 库存数量 */
    @Excel(name = "库存数量")
    private BigDecimal wmQuantity;

    private String woInvoice;
    private String woInvoiceId;
    private String woCode;
    private  String woType;
    private String woConfig;
    private Date woDate;
    private String woTypeId;
    private Long woUniqueId;
    private BigDecimal woQuantity;
    /** 已入库数量 */
    @Excel(name = "已入库数量")
    private BigDecimal wiQuantity;

    private BigDecimal wiQuantityR;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    /** 删除标记 */
    private String delFlag;

    private BigDecimal surplus;

    public Long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getMpCode() {
        return mpCode;
    }

    public void setMpCode(String mpCode) {
        this.mpCode = mpCode;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
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

    public Long getInvSortRoot() {
        return invSortRoot;
    }

    public void setInvSortRoot(Long invSortRoot) {
        this.invSortRoot = invSortRoot;
    }

    public Long getInvSortId() {
        return invSortId;
    }

    public void setInvSortId(Long invSortId) {
        this.invSortId = invSortId;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getWmQuantity() {
        return wmQuantity;
    }

    public void setWmQuantity(BigDecimal wmQuantity) {
        this.wmQuantity = wmQuantity;
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

    public String getWoCode() {
        return woCode;
    }

    public void setWoCode(String woCode) {
        this.woCode = woCode;
    }

    public String getWoType() {
        return woType;
    }

    public void setWoType(String woType) {
        this.woType = woType;
    }

    public String getWoConfig() {
        return woConfig;
    }

    public void setWoConfig(String woConfig) {
        this.woConfig = woConfig;
    }

    public Date getWoDate() {
        return woDate;
    }

    public void setWoDate(Date woDate) {
        this.woDate = woDate;
    }

    public String getWoTypeId() {
        return woTypeId;
    }

    public void setWoTypeId(String woTypeId) {
        this.woTypeId = woTypeId;
    }

    public Long getWoUniqueId() {
        return woUniqueId;
    }

    public void setWoUniqueId(Long woUniqueId) {
        this.woUniqueId = woUniqueId;
    }

    public BigDecimal getWoQuantity() {
        return woQuantity;
    }

    public void setWoQuantity(BigDecimal woQuantity) {
        this.woQuantity = woQuantity;
    }

    public BigDecimal getWiQuantity() {
        return wiQuantity;
    }

    public void setWiQuantity(BigDecimal wiQuantity) {
        this.wiQuantity = wiQuantity;
    }

    public BigDecimal getWiQuantityR() {
        return wiQuantityR;
    }

    public void setWiQuantityR(BigDecimal wiQuantityR) {
        this.wiQuantityR = wiQuantityR;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public BigDecimal getSurplus() {
        return surplus;
    }

    public void setSurplus(BigDecimal surplus) {
        this.surplus = surplus;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    @Override
    public String toString() {
        return "MpPlanSalveDTO{" +
                "uniqueId=" + uniqueId +
                ", mpCode='" + mpCode + '\'' +
                ", salesman='" + salesman + '\'' +
                ", ppNumber='" + ppNumber + '\'' +
                ", ppDate=" + ppDate +
                ", invSortRoot=" + invSortRoot +
                ", invSortId=" + invSortId +
                ", invCode='" + invCode + '\'' +
                ", invName='" + invName + '\'' +
                ", invAttribute='" + invAttribute + '\'' +
                ", unitCode='" + unitCode + '\'' +
                ", unitName='" + unitName + '\'' +
                ", quantity=" + quantity +
                ", amount=" + amount +
                ", price=" + price +
                ", wmQuantity=" + wmQuantity +
                ", woInvoice='" + woInvoice + '\'' +
                ", woInvoiceId='" + woInvoiceId + '\'' +
                ", woCode='" + woCode + '\'' +
                ", woType='" + woType + '\'' +
                ", woConfig='" + woConfig + '\'' +
                ", woDate=" + woDate +
                ", woTypeId='" + woTypeId + '\'' +
                ", woUniqueId=" + woUniqueId +
                ", woQuantity=" + woQuantity +
                ", wiQuantity=" + wiQuantity +
                ", wiQuantityR=" + wiQuantityR +
                ", remarks='" + remarks + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", surplus=" + surplus +
                '}';
    }
}
