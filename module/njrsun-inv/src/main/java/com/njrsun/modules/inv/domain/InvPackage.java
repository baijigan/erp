package com.njrsun.modules.inv.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

/**
 * 包装单位对象 inv_package
 * 
 * @author njrsun
 * @date 2021-04-06
 */
public class InvPackage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long uniqueId;

    /** 包装单位符号 */
    @Excel(name = "包装单位符号")
    private String code;

    /** 包装单位中文 */
    @Excel(name = "包装单位中文")
    private String name;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    private String beChip;

    /** 版本号 */
    @Excel(name = "版本号")
    private Long version;

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
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }
    public void setVersion(Long version) 
    {
        this.version = version;
    }

    public Long getVersion() 
    {
        return version;
    }

    public String getBeChip() {
        return beChip;
    }

    public void setBeChip(String beChip) {
        this.beChip = beChip;
    }

    @Override
    public String toString() {
        return "InvPackage{" +
                "uniqueId=" + uniqueId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", beChip='" + beChip + '\'' +
                ", version=" + version +
                '}';
    }
}
