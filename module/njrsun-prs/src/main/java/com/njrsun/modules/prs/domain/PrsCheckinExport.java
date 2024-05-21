package com.njrsun.modules.prs.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/11/18 9:31
 */
@Data
public class PrsCheckinExport extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long uniqueId;
    @Excel(name = "销售订单号")
    private String ppNumber;

    @Excel(name = "生产单号")
    private String prsOrderCode;
    /** 主表ID */
    @Excel(name = "编码")
    private String prsCode;

    @Excel(name = "单据日期")
    private Date invoiceDate;

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

    /** 批号 */
    @Excel(name = "批号")
    private String batchNumber;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal quantity;

    @Excel(name ="入库数量")
    private BigDecimal wiQuantity;

    @Excel(name ="仓库")
    private String warehouse;

    private String workType;
    private String formConfig;

    private String invoiceType;

    private String invoiceStatus;

    private String workStatus;
    @Excel(name ="备注")
    private String remarks;
}
