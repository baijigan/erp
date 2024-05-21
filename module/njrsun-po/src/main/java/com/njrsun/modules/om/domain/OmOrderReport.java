package com.njrsun.modules.om.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/9/10 14:13
 */
@Data
public class OmOrderReport extends BaseEntity {
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
    @Excel(name = "销售类型")
    private String saleType;
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
    @Excel(name = "销售合同号")
    private String contractNo;
    /** 客户名称 */
    @Excel(name = "客户名称")
    private String customer;
    /** 客户编号 */
    @Excel(name = "客户编号")
    private String customerId;
    /** 客户单号 */
    @Excel(name = "客户单号")
    private String custCode;
    @Excel(name = "付款条件")
    private String paymentTeam;
    /** 交付日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "交付日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deliverDate;
    /** 交期状态（0预计  1确认） */
    @Excel(name = "交期状态", readConverterExp = "0=预计,1=确认")
    private String deliverStatus;
    /** 税率 */
    @Excel(name = "税率")
    private String tax;
    /** 币种 */
    @Excel(name = "币种")
    private String currency;
    @Excel(name = "国家")
    private String country;
    /** 收货人 */
    @Excel(name = "收货人")
    private String contact;
    /** 电话 */
    @Excel(name = "电话")
    private String mobile;
    /** 地址 */
    @Excel(name = "地址")
    private String addr;
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
    /** 密度 */
    @Excel(name = "密度")
    private Long density;
    /** 密度单位编码 */
    @Excel(name = "密度单位编码")
    private String densityCode;
    /** 质量备注 */
    @Excel(name = "质量备注")
    private String qualityRemarks;
    /** 文件清单（枚举勾选） */
    @Excel(name = "文件清单")
    private String documents;
    /** 采购数量 */
    @Excel(name = "采购数量")
    private BigDecimal quantity;
    /** 采购单价 */
    @Excel(name = "采购单价")
    private BigDecimal price;
    /** 采购金额 */
    @Excel(name = "采购金额")
    private BigDecimal amount;
    @Excel(name = "包装要求")
    private String pkgRemarks;
    @Excel(name = "包装类型")
    private String pkgType;
    @Excel(name = "包装件数")
    private BigDecimal piece;
    @Excel(name = "包装件数单位")
    private String pieceType;
    @Excel(name = "最小包装")
    private BigDecimal minNumber;
    @Excel(name = "最小包装单位")
    private String minType;
    @Excel(name = "上游单据类型")
    private String woInvoice;
    @Excel(name = "上游单据类型代号")
    private String woInvoiceId;
    /** 上游单据 */
    @Excel(name = "上游单据编码")
    private String woCode;
    /** 上游物料 */
    @Excel(name = "上游物料")
    private Long woUniqueId;
    /** 上游数量 */
    @Excel(name = "上游余量")
    private BigDecimal woQuantity;
    /** 下游数量 */
    @Excel(name = "发货数量")
    private BigDecimal wiQuantity;
    /** 下游数量 */
    @Excel(name = "未数量")
    private BigDecimal notWiQuantity;
    /** 备注 */
    @Excel(name = "备注")
    private String remark;
    @Excel(name = "出库数量")
    private BigDecimal outQuantity;
    @Excel(name = "未出库数量")
    private BigDecimal notOutQuantity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

}
