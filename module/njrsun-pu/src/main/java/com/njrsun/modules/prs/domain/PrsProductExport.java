package com.njrsun.modules.prs.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/11/15 15:15
 */
@Data
public class PrsProductExport  extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long uniqueId;
    @Excel(name = "销售订单号")
    private String ppNumber;

    /** 主表ID */
    @Excel(name = "编码")
    private String prsCode;

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

    /** 用料方式（0通用料 1专用料） */
    @Excel(name = "用料方式", readConverterExp = "0=通用料,1=专用料")
    private String invInType;

    /** 批号 */
    @Excel(name = "批号")
    private String batchNumber;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal quantity;

    @Excel(name ="入库数量")
    private BigDecimal wiQuantity;
    @Excel(name ="单据日期")
    private Date invoiceDate;
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

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    private String workType;


}
