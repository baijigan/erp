package com.njrsun.system.domain;

import java.util.Date;

/**
 * @author njrsun
 * @create 2021/9/26 15:24
 */

public class SysCoder {
    private Long coderId;
    private String workTypeId;
    private String workType;
    private String prefix;
    private Long incNumber;
    private Long incLen;
    private String exactDate;
    private String orderNum;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String remark;
    private String workTypeName;
    private String typeShare;

    public String getTypeShare() {
        return typeShare;
    }

    public void setTypeShare(String typeShare) {
        this.typeShare = typeShare;
    }

    public Long getCoderId() {
        return coderId;
    }

    public void setCodeId(Long coderId) {
        this.coderId = coderId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setCoderId(Long coderId) {
        this.coderId = coderId;
    }

    public Long getIncNumber() {
        return incNumber;
    }

    public void setIncNumber(Long incNumber) {
        this.incNumber = incNumber;
    }

    public Long getIncLen() {
        return incLen;
    }

    public void setIncLen(Long incLen) {
        this.incLen = incLen;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getWorkTypeId() {
        return workTypeId;
    }

    public void setWorkTypeId(String workTypeId) {
        this.workTypeId = workTypeId;
    }

    public String getExactDate() {
        return exactDate;
    }

    public void setExactDate(String exactDate) {
        this.exactDate = exactDate;
    }

    public String getWorkTypeName() {
        return workTypeName;
    }

    public void setWorkTypeName(String workTypeName) {
        this.workTypeName = workTypeName;
    }

    @Override
    public String toString() {
        return "SysCode{" +
                "coderId=" + coderId +
                ", workTypeId='" + workTypeId + '\'' +
                ", workType='" + workType + '\'' +
                ", prefix='" + prefix + '\'' +
                ", incNumber='" + incNumber + '\'' +
                ", incLen='" + incLen + '\'' +
                ", exactDate='" + exactDate + '\'' +
                ", orderNum='" + orderNum + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                ", remark='" + remark + '\'' +
                ", workTypeName='" + workTypeName + '\'' +
                '}';
    }
}
