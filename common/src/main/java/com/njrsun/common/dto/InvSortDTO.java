package com.njrsun.common.dto;

import com.njrsun.common.annotation.Excel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author njrsun
 * @create 2021/9/26 14:33
 */
public class InvSortDTO {
    private static final long serialVersionUID = 1L;

    /** 分类id */
    @Excel(name = "分类id")
    private Long sortId;

    /** 父类id */
    @Excel(name = "父类id")
    private Long parentId;

    /** 父级列表 */
    @Excel(name = "父级列表")
    private String ancestors;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String sortName;

    /** 分类代码 */
    @Excel(name = "分类代码")
    private String sortCode;

    /** 最后流水号 */
    @Excel(name = "最后流水号")
    private Long serialNumber;

    @Excel(name="计量单位编码")
    private String unitCode;
    @Excel(name = "计量单位名称")
    private String unitName;


    /** 流水号宽度 */
    @Excel(name = "流水号宽度")
    private Long serialLength;

    /** 个性物料 */
    @Excel(name ="物料特性")
    private String featureCode;

    private List<InvIndividualAttrDTO> invIndividuals;

    /** 分类状态（0正常 1停用） */
    @Excel(name = "分类状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 版本号 */
    private Long version;
    private List<InvSortDTO> children = new ArrayList<>();
    @Excel(name ="规格合并")
    private Integer attrMerge;
    @Excel(name ="规格别名")
    private String attrAlias;
    @Excel(name = "数量小数位数")
    private Integer quantityDigit;
    @Excel(name = "单价小数位数")
    private Integer priceDigit;
    @Excel(name = "显示单价")
    private String nonePrice;
    @Excel(name ="启用批号")
    private String batchNumber;

    public Long getSortId() {
        return sortId;
    }

    public void setSortId(Long sortId) {
        this.sortId = sortId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(String ancestors) {
        this.ancestors = ancestors;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
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

    public Long getSerialLength() {
        return serialLength;
    }

    public void setSerialLength(Long serialLength) {
        this.serialLength = serialLength;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

    public List<InvIndividualAttrDTO> getInvIndividuals() {
        return invIndividuals;
    }

    public void setInvIndividuals(List<InvIndividualAttrDTO> invIndividuals) {
        this.invIndividuals = invIndividuals;
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

    public List<InvSortDTO> getChildren() {
        return children;
    }

    public void setChildren(List<InvSortDTO> children) {
        this.children = children;
    }

    public Integer getAttrMerge() {
        return attrMerge;
    }

    public void setAttrMerge(Integer attrMerge) {
        this.attrMerge = attrMerge;
    }

    public String getAttrAlias() {
        return attrAlias;
    }

    public void setAttrAlias(String attrAlias) {
        this.attrAlias = attrAlias;
    }

    public Integer getQuantityDigit() {
        return quantityDigit;
    }

    public void setQuantityDigit(Integer quantityDigit) {
        this.quantityDigit = quantityDigit;
    }

    public Integer getPriceDigit() {
        return priceDigit;
    }

    public void setPriceDigit(Integer priceDigit) {
        this.priceDigit = priceDigit;
    }

    public String getNonePrice() {
        return nonePrice;
    }

    public void setNonePrice(String nonePrice) {
        this.nonePrice = nonePrice;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    @Override
    public String toString() {
        return "InvSortDto{" +
                "sortId=" + sortId +
                ", parentId=" + parentId +
                ", ancestors='" + ancestors + '\'' +
                ", sortName='" + sortName + '\'' +
                ", sortCode='" + sortCode + '\'' +
                ", serialNumber=" + serialNumber +
                ", unitCode='" + unitCode + '\'' +
                ", unitName='" + unitName + '\'' +
                ", serialLength=" + serialLength +
                ", featureCode='" + featureCode + '\'' +
                ", invIndividuals=" + invIndividuals +
                ", status='" + status + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", version=" + version +
                ", children=" + children +
                ", attrMerge=" + attrMerge +
                ", attrAlias='" + attrAlias + '\'' +
                ", quantityDigit=" + quantityDigit +
                ", priceDigit=" + priceDigit +
                ", nonePrice='" + nonePrice + '\'' +
                ", batchNumber='" + batchNumber + '\'' +
                '}';
    }
}
