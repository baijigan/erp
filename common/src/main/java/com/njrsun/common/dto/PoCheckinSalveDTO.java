package com.njrsun.common.dto;

import com.njrsun.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/10/26 17:07
 */
public class PoCheckinSalveDTO {
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long uniqueId;

    /** 主表ID */
    @Excel(name = "主表ID")
    private String poCode;

    private String salesman;

    /** PP号 */
    @Excel(name = "PP号")
    private String ppNumber;
    /** PP交期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "PP交期", width = 30, dateFormat = "yyyy-MM-dd")
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

    /** 采购订单号 */
    @Excel(name = "采购订单号")
    private String woCode;

    private String woConfig;
    private String woInvoice;
    private String woInvoiceId;
    private String woType;
    private String woTypeId;

    private String supplyType;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date woDate;

    /** 采购订单唯一号 */
    @Excel(name = "采购订单唯一号")
    private Long woUniqueId;

    /** 到货数量 */
    @Excel(name = "到货数量")
    private BigDecimal quantity;

    /** 采购单价 */
    @Excel(name = "采购单价")
    private BigDecimal price;

    /** 采购金额 */
    @Excel(name = "采购金额")
    private BigDecimal amount;

    private BigDecimal wiQuantity;

    private BigDecimal wiQuantityR;

    private BigDecimal woQuantity;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    /** 删除标记 */
    private String delFlag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private  Date createTime;
    private String createBy;
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

    public String getWoCode() {
        return woCode;
    }

    public void setWoCode(String woCode) {
        this.woCode = woCode;
    }

    public String getWoConfig() {
        return woConfig;
    }

    public void setWoConfig(String woConfig) {
        this.woConfig = woConfig;
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

    public String getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(String supplyType) {
        this.supplyType = supplyType;
    }

    public Date getWoDate() {
        return woDate;
    }

    public void setWoDate(Date woDate) {
        this.woDate = woDate;
    }

    public Long getWoUniqueId() {
        return woUniqueId;
    }

    public void setWoUniqueId(Long woUniqueId) {
        this.woUniqueId = woUniqueId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public BigDecimal getWoQuantity() {
        return woQuantity;
    }

    public void setWoQuantity(BigDecimal woQuantity) {
        this.woQuantity = woQuantity;
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
        return "PoCheckinSalveDTO{" +
                "uniqueId=" + uniqueId +
                ", poCode='" + poCode + '\'' +
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
                ", woCode='" + woCode + '\'' +
                ", woConfig='" + woConfig + '\'' +
                ", woInvoice='" + woInvoice + '\'' +
                ", woInvoiceId='" + woInvoiceId + '\'' +
                ", woType='" + woType + '\'' +
                ", woTypeId='" + woTypeId + '\'' +
                ", supplyType='" + supplyType + '\'' +
                ", woDate=" + woDate +
                ", woUniqueId=" + woUniqueId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", amount=" + amount +
                ", wiQuantity=" + wiQuantity +
                ", wiQuantityR=" + wiQuantityR +
                ", woQuantity=" + woQuantity +
                ", remarks='" + remarks + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", createTime=" + createTime +
                ", createBy='" + createBy + '\'' +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
