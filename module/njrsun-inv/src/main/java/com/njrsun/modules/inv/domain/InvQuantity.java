package com.njrsun.modules.inv.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

/**
 * 物料数量对象 inv_quantity
 * 
 * @author njrsun
 * @date 2021-04-07
 */
public class InvQuantity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录id */
    private Long uniqueId;

    /** 物料id */
    @Excel(name = "物料id")
    private String code;

    /** 物料名称 */
    @Excel(name = "物料名称")
    private String name;

    /** 型号规格 */
    @Excel(name = "型号规格")
    private String attribute;

    /** 计量单位编码 */
    @Excel(name = "计量单位编码")
    private String unitCode;

    /** 计量单位名称 */
    @Excel(name = "计量单位名称")
    private String unitName;

    /** 数量 */
    @Excel(name = "库存数量")
    private Float quantity;

    /** 计划锁定数量 */
    @Excel(name = "计划锁定数量")
    private Float mp;

    /** 销售锁定数量 */
    @Excel(name = "销售锁定数量")
    private Float om;

    /** 采购在途数量 */
    @Excel(name = "采购在途数量")
    private Float po;

    /** 生产在途数量 */
    @Excel(name = "生产在途数量")
    private Float prs;

    /** 委外在途数量 */
    @Excel(name = "委外在途数量")
    private Float pu;

    /** 实际可用数量 */
    @Excel(name = "实际可用数量")
    private Float available;

    @Excel(name = "期初数量")
    private Float initial;

    /** 版本号 */
    @Excel(name = "版本号")
    private Long version;

    @Excel(name = "删除标志")
    private String delFlag;

    public void setUniqueId(Long uniqueId) 
    {
        this.uniqueId = uniqueId;
    }

    public Long getUniqueId() 
    {
        return uniqueId;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setAttribute(String attribute) 
    {
        this.attribute = attribute;
    }

    public String getAttribute() 
    {
        return attribute;
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

    public Float getInitial() {
        return initial;
    }

    public void setInitial(Float initial) {
        this.initial = initial;
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

    public void setVersion(Long version)
    {
        this.version = version;
    }

    public Long getVersion() 
    {
        return version;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return "InvQuantity{" +
                "uniqueId=" + uniqueId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", attribute='" + attribute + '\'' +
                ", unitCode='" + unitCode + '\'' +
                ", unitName='" + unitName + '\'' +
                ", quantity=" + quantity +
                ", mp=" + mp +
                ", om=" + om +
                ", po=" + po +
                ", prs=" + prs +
                ", pu=" + pu +
                ", available=" + available +
                ", initial=" + initial +
                ", version=" + version +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
