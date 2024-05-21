package com.njrsun.modules.om.domain;

import com.njrsun.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/10/22 14:58
 */
@Data
public class OmOrderDetail {
    @Excel(name ="销售员")
    private  String   workStaff;
    @Excel(name ="销售订单号")
    private String omCode;
    @Excel(name ="业务类型")
    private String workType;
    @Excel(name ="销售类型")
    private String saleType;
    @Excel(name ="订单交期",dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliverDate;
    @Excel(name ="交期状态")
    private String deliverStatus;
    @Excel(name ="物料编码")
    private String invCode;
    @Excel(name ="物料名称")
    private String invName;
    @Excel(name = "规格型号")
    private String invAttribute;
    @Excel(name ="数量")
    private BigDecimal quantity;
    @Excel(name ="质量要求")
    private String qualityRemarks;
    @Excel(name ="包装要求")
    private String pkgRemarks;
    @Excel(name ="件数")
    private Float  piece;
    @Excel(name ="目的国家")
    private  String country;
}
