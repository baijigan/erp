package com.njrsun.modules.om.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OmFapiaoReport extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 单据编码 */
    @Excel(name = "单据编码")
    private String omCode;

    /** 销售订单号 */
    private String omOrderCode;

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

    /** 客户名称 */
    @Excel(name = "客户名称")
    private String customer;

    /** 客户编号 */
    @Excel(name = "客户编号")
    private String customerId;

    /** 客户开票名称 */
    @Excel(name = "客户开票名称")
    private String customerFp;

    /** 开户行 */
    @Excel(name = "开户行")
    private String bank;

    /** 账号 */
    @Excel(name = "账号")
    private String account;

    /** 税号 */
    @Excel(name = "税号")
    private String tax;

    /** 地址电话 */
    @Excel(name = "地址电话")
    private String addr;

    /** 税率 */
    @Excel(name = "税率")
    private String vatRate;

    /** 发票状态（0录入  1开具  2驳回） */
    @Excel(name = "发票状态", readConverterExp = "0=录入,1=开具,2=驳回")
    private String vatStatus;

    /** 开票数量 */
    @Excel(name = "开票数量")
    private BigDecimal sumQuantity;

    /** 开票金额 */
    @Excel(name = "开票金额")
    private BigDecimal sumAmount;
}
