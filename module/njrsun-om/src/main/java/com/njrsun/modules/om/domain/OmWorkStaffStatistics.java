package com.njrsun.modules.om.domain;

import com.njrsun.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/9/13 10:00
 */
@Data
public class OmWorkStaffStatistics {
    /** 销售部门 */
    @Excel(name = "销售部门")
    private String workDept;

    /** 销售人员 */
    @Excel(name = "销售人员")
    private String workStaff;
    /** 客户名称 */
    @Excel(name = "客户名称")
    private String customer;
    /** 采购数量 */
    @Excel(name = "数量")
    private BigDecimal sumQuantity;
    /** 采购单价 */
    @Excel(name = "单价")
    private BigDecimal avgPrice;
    /** 采购金额 */
    @Excel(name = "金额")
    private BigDecimal sumAmount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}

