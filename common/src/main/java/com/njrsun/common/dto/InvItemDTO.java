package com.njrsun.common.dto;

import com.njrsun.common.annotation.Excel;

import java.math.BigDecimal;

/**
 * @author njrsun
 * @create 2021/12/1 18:05
 */

public class InvItemDTO {
    /** 主键id */
    @Excel(name="序号")
    private Long uniqueId;

    // @Excel(name="顶级目录")
    private String sortRoot;

    /** 分类ID */
    @Excel(name ="分类ID")
    private Long sortId;

    /** 物料id */
    @Excel(name = "物料CODE")
    private String code;

    /** 物料名称 */
    @Excel(name = "物料名称")
    private String name;

    /** 型号规格 */
    @Excel(name = "型号规格")
    private String attribute;
    @Excel(name ="规格描述")
    private String property;

    /** 计量单位编码 */
    @Excel(name = "计量单位编码")
    private String unitCode;
    /** 计量单位名称 */
    @Excel(name = "计量单位名称")
    private String unitName;
    /** 是否成品（0否 1是） */
    private String isProduct;
    /** 是否原料（0否 1是） */
    private String isMaterial;
    /** 是否半成品(0否 1是) */
    private String isComponent;

    @Excel(name = "发料有无零头",readConverterExp = "0=无,1=有")
    private String enableChip;
    @Excel(name ="是否允许负库存",readConverterExp = "0=不启用,1=启用")
    private String enableDebt;
    @Excel(name ="是否启用批次号",readConverterExp = "0=不启用,1=启用")
    private String enableBatch;
    /** 状态（0正常 1停用） */
    private String status;
    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;
    /** 版本号 */
    private Long version;
    @Excel(name ="当前库存数量")
    private Float quantity;
    @Excel(name ="计划锁定数量")
    /** 计划锁定数量 */
    private Float mp;
    @Excel(name = "销售锁定数量")
    /** 销售锁定数量 */
    private Float om;
    @Excel(name ="采购在途数量")
    /** 采购在途数量 */
    private Float po;
    @Excel(name ="生产在途数量")
    /** 生产在途数量 */
    private Float prs;
    /** 委外在途数量 */
    @Excel(name ="委外在途数量")
    private Float pu;
    /** 实际可用数量 */
    @Excel(name ="实际可用数量")
    private Float available;
    @Excel(name = "下限数量")
    private BigDecimal lowerLimit;
    @Excel(name = "上限数量")
    private BigDecimal upperLimit;

    private String supplyType;

    public String getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(String supplyType) {
        this.supplyType = supplyType;
    }

    public Long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getSortRoot() {
        return sortRoot;
    }

    public void setSortRoot(String sortRoot) {
        this.sortRoot = sortRoot;
    }

    public Long getSortId() {
        return sortId;
    }

    public void setSortId(Long sortId) {
        this.sortId = sortId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
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

    public String getIsProduct() {
        return isProduct;
    }

    public void setIsProduct(String isProduct) {
        this.isProduct = isProduct;
    }

    public String getIsMaterial() {
        return isMaterial;
    }

    public void setIsMaterial(String isMaterial) {
        this.isMaterial = isMaterial;
    }

    public String getIsComponent() {
        return isComponent;
    }

    public void setIsComponent(String isComponent) {
        this.isComponent = isComponent;
    }

    public String getEnableChip() {
        return enableChip;
    }

    public void setEnableChip(String enableChip) {
        this.enableChip = enableChip;
    }

    public String getEnableDebt() {
        return enableDebt;
    }

    public void setEnableDebt(String enableDebt) {
        this.enableDebt = enableDebt;
    }

    public String getEnableBatch() {
        return enableBatch;
    }

    public void setEnableBatch(String enableBatch) {
        this.enableBatch = enableBatch;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Float getMp() {
        return mp;
    }

    public void setMp(Float mp) {
        this.mp = mp;
    }

    public Float getOm() {
        return om;
    }

    public void setOm(Float om) {
        this.om = om;
    }

    public Float getPo() {
        return po;
    }

    public void setPo(Float po) {
        this.po = po;
    }

    public Float getPrs() {
        return prs;
    }

    public void setPrs(Float prs) {
        this.prs = prs;
    }

    public Float getPu() {
        return pu;
    }

    public void setPu(Float pu) {
        this.pu = pu;
    }

    public Float getAvailable() {
        return available;
    }

    public void setAvailable(Float available) {
        this.available = available;
    }

    public BigDecimal getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(BigDecimal lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public BigDecimal getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(BigDecimal upperLimit) {
        this.upperLimit = upperLimit;
    }
}
