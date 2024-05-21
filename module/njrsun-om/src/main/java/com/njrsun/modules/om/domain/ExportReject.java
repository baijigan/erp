package com.njrsun.modules.om.domain;

import com.njrsun.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author njrsun
 * @create 2021/9/7 17:35
 */
@Data
public class ExportReject {

    @Excel(name ="销售订单号")
    private String ppNumber;
    @Excel(name = "客户名称")
    private String customer;
    @Excel(name = "单据状态",readConverterExp = "0=开立,1=审核,2=退回")
    private  String   invoiceStatus;
    @Excel(name = "物料编码")
    private String invCode;
    @Excel(name = "物料名称")
    private String invName;
    @Excel(name = "退货数量")
    private String quantity;
    @Excel(name = "入库数量")
    private String wiQuantityB;
    @Excel(name = "包装规格")
    private String pkgType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name ="单据日期",dateFormat = "yyyy-MM-dd")
    private Date invoiceDate;
    @Excel(name = "批次编号")
    private String batchNumber;

}
