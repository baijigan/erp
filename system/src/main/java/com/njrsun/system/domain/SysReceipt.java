package com.njrsun.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

/**
 * 单据列对象 sys_receipt
 * 
 * @author njrsun
 * @date 2021-07-30
 */
public class SysReceipt extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一序号 */
    private Long uniqueId;

    /** 所属模块 */
    @Excel(name = "所属模块")
    private String moduleId;

    /** 单据代码 */
    @Excel(name = "单据代码")
    private String code;

    /** 单据名称 */
    @Excel(name = "单据名称")
    private String name;

    /** 规格合并 */
    @Excel(name = "规格合并")
    private Long attrMerge;

    /** 规格别名 */
    @Excel(name = "规格别名")
    private String attrAlias;

    /** 规格特性 */
    @Excel(name = "规格特性")
    private String attrFeature;

    /** 数量小数位数 */
    @Excel(name = "数量小数位数")
    private Long quantityDigit;

    /** 单价小数位数 */
    @Excel(name = "单价小数位数")
    private Long priceDigit;

    /** 显示单价（0无 1有） */
    @Excel(name = "显示单价", readConverterExp = "0=无,1=有")
    private String nonePrice;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /** 版本号 */
    @Excel(name = "版本号")
    private Long version;

    /** 删除标志 */
    private Long delFlag;

    public void setUniqueId(Long uniqueId) 
    {
        this.uniqueId = uniqueId;
    }

    public Long getUniqueId() 
    {
        return uniqueId;
    }
    public void setModuleId(String moduleId) 
    {
        this.moduleId = moduleId;
    }

    public String getModuleId() 
    {
        return moduleId;
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
    public void setAttrMerge(Long attrMerge) 
    {
        this.attrMerge = attrMerge;
    }

    public Long getAttrMerge() 
    {
        return attrMerge;
    }
    public void setAttrAlias(String attrAlias) 
    {
        this.attrAlias = attrAlias;
    }

    public String getAttrAlias() 
    {
        return attrAlias;
    }
    public void setAttrFeature(String attrFeature) 
    {
        this.attrFeature = attrFeature;
    }

    public String getAttrFeature() 
    {
        return attrFeature;
    }
    public void setQuantityDigit(Long quantityDigit) 
    {
        this.quantityDigit = quantityDigit;
    }

    public Long getQuantityDigit() 
    {
        return quantityDigit;
    }
    public void setPriceDigit(Long priceDigit) 
    {
        this.priceDigit = priceDigit;
    }

    public Long getPriceDigit() 
    {
        return priceDigit;
    }
    public void setNonePrice(String nonePrice) 
    {
        this.nonePrice = nonePrice;
    }

    public String getNonePrice() 
    {
        return nonePrice;
    }
    public void setCreateDate(Date createDate) 
    {
        this.createDate = createDate;
    }

    public Date getCreateDate() 
    {
        return createDate;
    }
    public void setUpdateDate(Date updateDate) 
    {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() 
    {
        return updateDate;
    }
    public void setVersion(Long version) 
    {
        this.version = version;
    }

    public Long getVersion() 
    {
        return version;
    }
    public void setDelFlag(Long delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Long getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("uniqueId", getUniqueId())
            .append("moduleId", getModuleId())
            .append("code", getCode())
            .append("name", getName())
            .append("attrMerge", getAttrMerge())
            .append("attrAlias", getAttrAlias())
            .append("attrFeature", getAttrFeature())
            .append("quantityDigit", getQuantityDigit())
            .append("priceDigit", getPriceDigit())
            .append("nonePrice", getNonePrice())
            .append("createDate", getCreateDate())
            .append("createBy", getCreateBy())
            .append("updateDate", getUpdateDate())
            .append("updateBy", getUpdateBy())
            .append("version", getVersion())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
