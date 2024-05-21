package com.njrsun.common.dto;

import com.njrsun.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author njrsun
 * @create 2021/9/26 14:43
 */
public class InvIndividualAttrDTO {
    /** 记录id */
    private Long uniqueId;

    /** 物料代码 */
    @Excel(name = "物料代码")
    private String code;

    /** 物料名称 */
    @Excel(name = "物料名称")
    private String name;

    /** 分类状态（0正常 1停用） */
    @Excel(name = "分类状态", readConverterExp = "0=正常,1=停用")
    private String status;

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
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("uniqueId", getUniqueId())
                .append("code", getCode())
                .append("name", getName())
                .append("status", getStatus())
                .toString();
    }
}
