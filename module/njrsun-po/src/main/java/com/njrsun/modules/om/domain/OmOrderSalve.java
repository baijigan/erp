package com.njrsun.modules.om.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

/**
 * 销售订单从对象 om_order_salve
 * 
 * @author njrsun
 * @date 2021-08-28
 */
public class OmOrderSalve extends BaseEntity
{

    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long uniqueId;

    /** 主表ID */
    @Excel(name = "主表ID")
    private String omCode;

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


    /** 质量备注 */
    @Excel(name = "质量备注")
    private String qualityRemarks;

    /** 文件清单（枚举勾选） */
    @Excel(name = "文件清单", readConverterExp = "枚=举勾选")
    private String documents;

    private String  drawingNo;
    /** 采购数量 */
    @Excel(name = "采购数量")
    private BigDecimal quantity;

    /** 采购单价 */
    @Excel(name = "采购单价")
    private BigDecimal price;

    /** 采购金额 */
    @Excel(name = "采购金额")
    private BigDecimal amount;


    private String pkgRemarks;

    private String pkgType;


    private BigDecimal piece;

    private String pieceType;


    private BigDecimal minNumber;

    private String minType;

    /** 上游单据 */
    @Excel(name = "上游单据")
    private String woCode;

    private String woInvoice;

    private String woInvoiceId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String woDate;
    private String woType;

    private String woTypeId;
    /** 上游物料 */
    @Excel(name = "上游物料")
    private Long woUniqueId;

    /** 上游数量 */
    @Excel(name = "上游数量")
    private BigDecimal woQuantity;

    /** 下游数量 */
    @Excel(name = "下游蓝字数量")
    private BigDecimal wiQuantity;

    /** 下游数量 */
    @Excel(name = "下游红字数量")
    private BigDecimal wiQuantityR;

    /** 下游金额 */
    @Excel(name = "下游蓝字金额")
    private BigDecimal wiAmount;

    private String woConfig;


    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    /** 删除标记 */
    private String delFlag;

    private String tax;

    private BigDecimal mpQuantity;

    private BigDecimal prsQuantity;

    public BigDecimal getMpQuantity() {
        return mpQuantity;
    }
    public void setMpQuantity(BigDecimal mpQuantity) {
        this.mpQuantity = mpQuantity;
    }

    public BigDecimal getPrsQuantity() {
        return prsQuantity;
    }
    public void setPrsQuantity(BigDecimal prsQuantity) {
        this.prsQuantity = prsQuantity;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
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

    public BigDecimal getMinNumber() {
        return minNumber;
    }

    public void setMinNumber(BigDecimal minNumber) {
        this.minNumber = minNumber;
    }

    public String getPieceType() {
        return pieceType;
    }

    public void setPieceType(String pieceType) {
        this.pieceType = pieceType;
    }

    public String getDrawingNo() {
        return drawingNo;
    }

    public void setDrawingNo(String drawingNo) {
        this.drawingNo = drawingNo;
    }

    public String getMinType() {
        return minType;
    }

    public void setMinType(String minType) {
        this.minType = minType;
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
    public void setQualityRemarks(String qualityRemarks) 
    {
        this.qualityRemarks = qualityRemarks;
    }

    public String getQualityRemarks() 
    {
        return qualityRemarks;
    }
    public void setDocuments(String documents) 
    {
        this.documents = documents;
    }

    public String getDocuments() 
    {
        return documents;
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

    @Override
    public String toString() {
        return "OmOrderSalve{" +
                "uniqueId=" + uniqueId +
                ", omCode='" + omCode + '\'' +
                ", invSortRoot=" + invSortRoot +
                ", invSortId=" + invSortId +
                ", invCode='" + invCode + '\'' +
                ", invName='" + invName + '\'' +
                ", invAttribute='" + invAttribute + '\'' +
                ", unitCode='" + unitCode + '\'' +
                ", unitName='" + unitName + '\'' +
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
                ", woCode='" + woCode + '\'' +
                ", woInvoice='" + woInvoice + '\'' +
                ", woInvoiceId='" + woInvoiceId + '\'' +
                ", woDate='" + woDate + '\'' +
                ", woType='" + woType + '\'' +
                ", woTypeId='" + woTypeId + '\'' +
                ", woUniqueId=" + woUniqueId +
                ", woQuantity=" + woQuantity +
                ", wiQuantity=" + wiQuantity +
                ", wiQuantityR=" + wiQuantityR +
                ", woConfig='" + woConfig + '\'' +
                ", remarks='" + remarks + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", tax='" + tax + '\'' +
                '}';
    }

    public BigDecimal getWiAmount() {
        return wiAmount;
    }

    public void setWiAmount(BigDecimal wiAmount) {
        this.wiAmount = wiAmount;
    }
}
