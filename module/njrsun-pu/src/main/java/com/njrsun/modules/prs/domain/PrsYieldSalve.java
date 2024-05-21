package com.njrsun.modules.prs.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 生产报工单子对象 prs_yield_salve
 * 
 * @author njrsun
 * @date 2022-01-13
 */
public class PrsYieldSalve extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 单据序号 */
    private Long uniqueId;

    /** 单据编码 */
    @Excel(name = "单据编码")
    private String prsCode;

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    /** 工序代码 */
    @Excel(name = "工序代码")
    private String sectionId;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    /** 工序名称 */
    @Excel(name = "工序名称")
    private String section;

    /** 标准工时 */
    @Excel(name = "标准工时")
    private Long duration;

    /** 实际工时 */
    @Excel(name = "实际工时")
    private Long useDuration;

    /** 报损数量 */
    @Excel(name = "报损数量")
    private BigDecimal badQuantity;

    /** 完工数量 */
    @Excel(name = "完工数量")
    private BigDecimal quantity;

    /** 是否质检（0否 1是） */
    @Excel(name = "是否质检", readConverterExp = "0=否,1=是")
    private String isQuality;

    /** 计件方式（0班组 1个人） */
    @Excel(name = "计件方式", readConverterExp = "0=班组,1=个人")
    private String countType;

    /** 工人编码 */
    @Excel(name = "工人编码")
    private String workersIds;

    /** 工人姓名 */
    @Excel(name = "工人姓名")
    private String workersNames;

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
    public void setPrsCode(String prsCode) 
    {
        this.prsCode = prsCode;
    }

    public String getPrsCode() 
    {
        return prsCode;
    }
    public void setDuration(Long duration)
    {
        this.duration = duration;
    }

    public Long getDuration() 
    {
        return duration;
    }
    public void setUseDuration(Long useDuration) 
    {
        this.useDuration = useDuration;
    }

    public Long getUseDuration() 
    {
        return useDuration;
    }
    public void setBadQuantity(BigDecimal badQuantity) 
    {
        this.badQuantity = badQuantity;
    }

    public BigDecimal getBadQuantity() 
    {
        return badQuantity;
    }
    public void setQuantity(BigDecimal quantity) 
    {
        this.quantity = quantity;
    }

    public BigDecimal getQuantity() 
    {
        return quantity;
    }
    public void setIsQuality(String isQuality) 
    {
        this.isQuality = isQuality;
    }

    public String getIsQuality() 
    {
        return isQuality;
    }
    public void setCountType(String countType) 
    {
        this.countType = countType;
    }

    public String getCountType() 
    {
        return countType;
    }
    public void setWorkersIds(String workersIds) 
    {
        this.workersIds = workersIds;
    }

    public String getWorkersIds() 
    {
        return workersIds;
    }
    public void setWorkersNames(String workersNames) 
    {
        this.workersNames = workersNames;
    }

    public String getWorkersNames() 
    {
        return workersNames;
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
            .append("prsCode", getPrsCode())
            .append("duration", getDuration())
            .append("useDuration", getUseDuration())
            .append("badQuantity", getBadQuantity())
            .append("quantity", getQuantity())
            .append("isQuality", getIsQuality())
            .append("countType", getCountType())
            .append("workersIds", getWorkersIds())
            .append("workersNames", getWorkersNames())
            .append("remarks", getRemarks())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
