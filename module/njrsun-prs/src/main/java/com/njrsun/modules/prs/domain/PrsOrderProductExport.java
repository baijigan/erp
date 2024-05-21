package com.njrsun.modules.prs.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2022/1/4 9:20
 */
@Data
public class PrsOrderProductExport extends BaseEntity {

    @Excel(name = "销售单号")
    private String ppNumber;
    @Excel(name = "生产订单号")
    private String prsCode;
    @Excel(name = "订单交期")
    private String ppDate;
    @Excel(name = "物料编码")
    private String invCode;
    @Excel(name = "物料名称")
    private String invName;
    @Excel(name = "主计量")
    private String unitName;
    @Excel(name = "生产线")
    private String beltline;
    @Excel(name = "生产日期")
    private Date arrangeDate;
    @Excel(name = "生产数量")
    private BigDecimal invQuantity;
    @Excel(name = "完工数量")
    private BigDecimal productQuantity;
    @Excel(name = "未完工数量")
    private BigDecimal notProductQuantity;
    @Excel(name = "入库数量")
    private BigDecimal wmQuantity;
    @Excel(name = "未入库数量")
    private BigDecimal notWmQuantity;

    private BigDecimal wiQuantity;

    private BigDecimal notWiQuantity;

    private String beltlineId;

    private String workType;

}
