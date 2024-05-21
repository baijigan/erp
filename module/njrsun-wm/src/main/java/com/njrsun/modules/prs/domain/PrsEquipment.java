package com.njrsun.modules.prs.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 设备列对象 prs_equipment
 * 
 * @author njrsun
 * @date 2021-06-25
 */
public class PrsEquipment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 客户序号 */
    private Long uniqueId;
    @Excel(name = "业务类型")
    private String workType;
    @Excel(name = "部门名称")
    private String deptName;
    /** 设备名称 */
    @Excel(name = "设备名称")
    private String name;
    @Excel(name = "状态",readConverterExp = "0=正常,1=停用")
    private String status;
    @Excel(name = "部门编码")
    private String code;
    @Excel(name = "排序")
    private String orderId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private Date updateDate;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public void setUniqueId(Long uniqueId)
    {
        this.uniqueId = uniqueId;
    }

    public Long getUniqueId() 
    {
        return uniqueId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "PrsEquipment{" +
                "uniqueId=" + uniqueId +
                ", deptName='" + deptName + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
