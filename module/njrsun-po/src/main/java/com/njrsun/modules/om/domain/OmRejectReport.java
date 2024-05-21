package com.njrsun.modules.om.domain;

import com.njrsun.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/9/13 9:14
 */
@Data
public class OmRejectReport{
    private static final long serialVersionUID = 1L;

    /** 单据序号 */
    private Long uniqueId;
    /** 单据编码 */
    @Excel(name = "单据编码")
    private String omCode;
    /** 单据日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date invoiceDate;
    /** 单据状态 */
    @Excel(name = "单据状态")
    private String invoiceStatus;
    /** 单据类型 */
    @Excel(name = "单据类型")
    private String invoiceType;
    /** 业务状态 */
    @Excel(name = "业务状态")
    private String workStatus;
    /** 业务类型 */
    @Excel(name = "业务类型")
    private String workType;
    /** 销售部门 */
    @Excel(name = "销售部门")
    private String workDept;
    /** 销售人员 */
    @Excel(name = "销售人员")
    private String workStaff;
    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date checkDate;
    /** 操作员 */
    @Excel(name = "操作员")
    private String userOper;
    /** 审核员 */
    @Excel(name = "审核员")
    private String userCheck;
    /** 备注 */
    @Excel(name = "备注")
    private String remarks;
    /** 客户名称 */
    @Excel(name = "客户名称")
    private String customer;
    /** 客户编号 */
    @Excel(name = "客户编号")
    private String customerId;
    /** 交付日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "交付日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deliverDate;
    /** 退货仓库 */
    @Excel(name = "退货仓库")
    private String whCode;
    /** 退货原因 */
    @Excel(name = "退货原因")
    private String reason;
    private String reasonId;
    @Excel(name = "销售订单号")
    private String ppNumber;
    /** 料品大类 */
    @Excel(name = "料品大类")
    private String invSortRoot;
    /** 料品分类 */
    @Excel(name = "料品分类")
    private String invSortId;
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
    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal quantity;
    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal price;
    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;
    /** 税率 */
    @Excel(name = "税率")
    private Long tax;
    /** 开票名称 */
    @Excel(name = "开票名称")
    private String billName;
    /** 包装类型 */
    @Excel(name = "包装类型")
    private String pkgType;
    /** 上游单据 */
    @Excel(name = "上游单据")
    private String woCode;
    @Excel(name = "上游单据类型")
    private String woInvoice;
    @Excel(name= "上游单据编号")
    private String woInvoiceId;
    /** 上游物料 */
    @Excel(name = "上游物料")
    private Long woUniqueId;
    /** 上游数量 */
    @Excel(name = "上游数量")
    private BigDecimal woQuantity;
    /** 下游数量 */
    @Excel(name = "下游数量")
    private BigDecimal wiQuantity;
    /** 备注 */
    @Excel(name = "备注")
    private String remark ;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

}
