package com.njrsun.common.dto;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/11/6 13:05
 */


public class MpOrderSalveDTO  extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long uniqueId;

    /** 主表ID */
    @Excel(name = "主表ID")
    private String mpCode;

    /** 销售员 */
    @Excel(name = "销售员")
    private String salesman;

    /** 销售订单号 */
    @Excel(name = "销售订单号")
    private String ppNumber;

    /** 订单交期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "订单交期", width = 30, dateFormat = "yyyy-MM-dd")
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

    private String customer;
    /** 密度 */
    @Excel(name = "密度")
    private Long density;

    /** 密度单位编码 */
    @Excel(name = "密度单位编码")
    private String densityCode;

    /** 质量要求 */
    @Excel(name = "质量要求")
    private String qualityRemarks;

    /** 文件清单（枚举勾选） */
    @Excel(name = "文件清单", readConverterExp = "枚=举勾选")
    private String documents;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal quantity;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal price;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 包装要求 */
    @Excel(name = "包装要求")
    private String pkgRemarks;

    /** 包装类型 */
    @Excel(name = "包装类型")
    private String pkgType;

    /** 包装件数 */
    @Excel(name = "包装件数")
    private BigDecimal piece;

    /** 包装件数单位 */
    @Excel(name = "包装件数单位")
    private String pieceType;

    /** 最小包装 */
    @Excel(name = "最小包装")
    private BigDecimal minNumber;

    /** 最小包装单位 */
    @Excel(name = "最小包装单位")
    private String minType;

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


    private Date startDate;

    private Date endDate;

    public Date getStartDate() {
        return startDate;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
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

    public Long getDensity() {
        return density;
    }

    public void setDensity(Long density) {
        this.density = density;
    }

    public String getDensityCode() {
        return densityCode;
    }

    public void setDensityCode(String densityCode) {
        this.densityCode = densityCode;
    }

    public String getQualityRemarks() {
        return qualityRemarks;
    }

    public void setQualityRemarks(String qualityRemarks) {
        this.qualityRemarks = qualityRemarks;
    }

    public String getDocuments() {
        return documents;
    }

    public void setDocuments(String documents) {
        this.documents = documents;
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

    public String getPkgRemarks() {
        return pkgRemarks;
    }

    public void setPkgRemarks(String pkgRemarks) {
        this.pkgRemarks = pkgRemarks;
    }

    public String getPkgType() {
        return pkgType;
    }

    public void setPkgType(String pkgType) {
        this.pkgType = pkgType;
    }

    public BigDecimal getPiece() {
        return piece;
    }

    public void setPiece(BigDecimal piece) {
        this.piece = piece;
    }

    public String getPieceType() {
        return pieceType;
    }

    public void setPieceType(String pieceType) {
        this.pieceType = pieceType;
    }

    public BigDecimal getMinNumber() {
        return minNumber;
    }

    public void setMinNumber(BigDecimal minNumber) {
        this.minNumber = minNumber;
    }

    public String getMinType() {
        return minType;
    }

    public void setMinType(String minType) {
        this.minType = minType;
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

    public Date getWoDate() {
        return woDate;
    }

    public void setWoDate(Date woDate) {
        this.woDate = woDate;
    }

    public String getWoCode() {
        return woCode;
    }

    public void setWoCode(String woCode) {
        this.woCode = woCode;
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

    @Override
    public String toString() {
        return "MpOrderSalveDTO{" +
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
                ", density=" + density +
                ", densityCode='" + densityCode + '\'' +
                ", qualityRemarks='" + qualityRemarks + '\'' +
                ", documents='" + documents + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", amount=" + amount +
                ", pkgRemarks='" + pkgRemarks + '\'' +
                ", pkgType='" + pkgType + '\'' +
                ", piece=" + piece +
                ", pieceType='" + pieceType + '\'' +
                ", minNumber=" + minNumber +
                ", minType='" + minType + '\'' +
                ", woConfig='" + woConfig + '\'' +
                ", woInvoice='" + woInvoice + '\'' +
                ", woInvoiceId='" + woInvoiceId + '\'' +
                ", woType='" + woType + '\'' +
                ", woTypeId='" + woTypeId + '\'' +
                ", woDate=" + woDate +
                ", woCode='" + woCode + '\'' +
                ", woUniqueId=" + woUniqueId +
                ", woQuantity=" + woQuantity +
                ", wiQuantity=" + wiQuantity +
                ", wiQuantityR=" + wiQuantityR +
                ", remarks='" + remarks + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
