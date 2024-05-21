package com.njrsun.common.dto;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.page.TableDataInfo;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/9/26 14:33
 */
public class WmWarehouseDTO {
    /** 仓库序号 */
    private Long uniqueId;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String code;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String name;

    private Integer period;

    /** 料品大类 */
    @Excel(name = "料品大类")
    private String sortId;

    private ArrayList<InvSortDTO> invSorts;

    @Excel(name ="料品大类")
    private String sortName;

    /** 仓库管理员 */
    @Excel(name = "仓库管理员")
    private String warehouseMgr;

    /** 仓库操作员 */
    @Excel(name = "仓库操作员")
    private String warehouseOper;

    /** 数量位数 */
    @Excel(name = "入库类型")
    private String inType;

    @Excel(name= "入库类型")
    private String inTypeName;
    @Excel(name ="出库类型")
    private String outTypeName;
    @Excel(name ="货位",readConverterExp = "0=停用,1=启用")
    private Integer enableLocation;

    private Integer enableChip;

    private TableDataInfo wmInitialsList;

    /** 单价位数 */
    @Excel(name = "出库类型")
    private String outType;

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

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public ArrayList<InvSortDTO> getInvSorts() {
        return invSorts;
    }

    public void setInvSorts(ArrayList<InvSortDTO> invSorts) {
        this.invSorts = invSorts;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getWarehouseMgr() {
        return warehouseMgr;
    }

    public void setWarehouseMgr(String warehouseMgr) {
        this.warehouseMgr = warehouseMgr;
    }

    public String getWarehouseOper() {
        return warehouseOper;
    }

    public void setWarehouseOper(String warehouseOper) {
        this.warehouseOper = warehouseOper;
    }

    public String getInType() {
        return inType;
    }

    public void setInType(String inType) {
        this.inType = inType;
    }

    public String getInTypeName() {
        return inTypeName;
    }

    public void setInTypeName(String inTypeName) {
        this.inTypeName = inTypeName;
    }

    public String getOutTypeName() {
        return outTypeName;
    }

    public void setOutTypeName(String outTypeName) {
        this.outTypeName = outTypeName;
    }

    public Integer getEnableLocation() {
        return enableLocation;
    }

    public void setEnableLocation(Integer enableLocation) {
        this.enableLocation = enableLocation;
    }

    public Integer getEnableChip() {
        return enableChip;
    }

    public void setEnableChip(Integer enableChip) {
        this.enableChip = enableChip;
    }

    public TableDataInfo getWmInitialsList() {
        return wmInitialsList;
    }

    public void setWmInitialsList(TableDataInfo wmInitialsList) {
        this.wmInitialsList = wmInitialsList;
    }

    public String getOutType() {
        return outType;
    }

    public void setOutType(String outType) {
        this.outType = outType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
