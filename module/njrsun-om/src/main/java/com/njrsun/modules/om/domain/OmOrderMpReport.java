package com.njrsun.modules.om.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/11/10 8:53
 */
@Data
public class OmOrderMpReport extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /** 单据序号 */
    private Long uniqueId;

    @Excel(name = "销售合同号")
    private String contractNo;
    /** 单据编码 */
    @Excel(name = "单据编码")
    private String omCode;
    /** 单据日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date invoiceDate;
    /** 单据类型 */
    @Excel(name = "订单类型")
    private String supplyType;

    @Excel(name = "销售类型")
    private String saleType;
    /** 销售人员 */
    @Excel(name = "销售人员")
    private String workStaff;
    /** 客户名称 */
    @Excel(name = "客户名称")
    private String customer;
    /** 交付日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "交付日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deliverDate;
    /** 料品编码 */
    @Excel(name = "料品编码")
    private String invCode;
    /** 料品名称 */
    @Excel(name = "料品名称")
    private String invName;
    /** 型号规格 */
    @Excel(name = "型号规格")
    private String invAttribute;
    /** 计量单位编码 */
    @Excel(name = "计量单位编码")
    private String unitCode;
    /** 计量单位名称 */
    @Excel(name = "计量单位名称")
    private String unitName;
    /** 下游数量 */
    @Excel(name = "计划数量")
    private BigDecimal mpQuantity;
    /** 下游数量 */
    @Excel(name = "未执行数量")
    private BigDecimal notMpQuantity;
    @Excel(name = "数量")
    private BigDecimal quantity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
}
