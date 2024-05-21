package com.njrsun.modules.prs.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/11/13 11:02
 */
@Data
public class PrsOrderExport  extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long uniqueId;

    /** 主表ID */
    @Excel(name = "单据编码")
    private String prsCode;
    @Excel(name ="单据日期",dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date invoiceDate;
    /** 销售订单号 */
    @Excel(name = "销售订单号")
    private String ppNumber;
    @Excel(name = "用料方式")
    private String invInType;
    @Excel(name = "计划生产日期")
    private Date needDate;
    @Excel(name = "执行生产日期")
    private Date arrangeDate;

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

    /** 包装件数 */
    @Excel(name = "包装件数")
    private BigDecimal piece;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal quantity;

    @Excel(name = "完工数量")
    private BigDecimal wiQuantity;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

}
