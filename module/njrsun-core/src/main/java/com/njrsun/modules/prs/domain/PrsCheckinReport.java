package com.njrsun.modules.prs.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产入库单从对象 prs_checkin_salve
 * 
 * @author njrsun
 * @date 2022-01-18
 */
@Data
public class PrsCheckinReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 销售订单号 */
    @Excel(name = "销售订单号")
    private String ppNumber;
    @Excel(name = "完工单号")
    private String woCode;
    @Excel(name = "生产线")
    private String beltline;
    /** 料品编码 */
    @Excel(name = "料品编码")
    private String invCode;
    /** 料品名称 */
    @Excel(name = "料品名称")
    private String invName;
    /** 型号规格 */
    @Excel(name = "型号规格")
    private String invAttribute;
    /** 计量单位名称 */
    @Excel(name = "计量单位名称")
    private String unitName;
    /** 批次编号 */
    @Excel(name = "批次编号")
    private String batchNumber;
    /** 属性1 */
    @Excel(name = "属性1")
    private String f1;

    /** 属性2 */
    @Excel(name = "属性2")
    private String f2;

    /** 属性3 */
    @Excel(name = "属性3")
    private String f3;

    /** 属性4 */
    @Excel(name = "属性4")
    private String f4;

    /** 属性5 */
    @Excel(name = "属性5")
    private String f5;

    @Excel(name = "入库时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date invoiceDate;

    @Excel(name = "入库数量")
    private BigDecimal quantity;


}
