package com.njrsun.modules.om.domain;

import com.njrsun.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/9/7 17:35
 */
@Data
public class ExportOrder {
    @Excel(name ="合同号")
    private String contractNo;
    @Excel(name ="销售订单号")
    private String omCode;
    @Excel(name ="客户名称")
    private String customer;
    @Excel(name = "单据状态",readConverterExp = "0=开立,1=审核,2=退回")
    private  String   invoiceStatus;
    @Excel(name ="物料编码")
    private String invCode;
    @Excel(name ="物料名称")
    private String invName;
    @Excel(name ="数量")
    private BigDecimal quantity;
    @Excel(name ="单价")
    private BigDecimal price;
    @Excel(name ="金额")
    private BigDecimal amount;
    @Excel(name ="发货数量")
    private BigDecimal wiQuantityB;
    @Excel(name ="包装规格")
    private String pkgType;
    @Excel(name ="件数")
    private BigDecimal piece;
    @Excel(name ="销售类型")
    private String saleType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name ="单据日期",dateFormat = "yyyy-MM-dd")
    private Date invoiceDate;
}
