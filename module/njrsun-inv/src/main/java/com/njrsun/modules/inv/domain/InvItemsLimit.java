package com.njrsun.modules.inv.domain;

import com.njrsun.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author njrsun
 * @create 2021/7/22 10:03
 */
@Data
public class InvItemsLimit{

    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long uniqueId;

    /** 物料id */
    @Excel(name = "物料CODE")
    private String code;

    private Long sortId;
    /** 物料名称 */
    @Excel(name = "物料名称")
    private String name;

    /** 型号规格 */
    @Excel(name = "型号规格")
    private String attribute;

    /** 计量单位名称 */
    @Excel(name = "计量单位名称")
    private String unitName;


    @Excel(name ="现存量")
    private Float quantity;

    @Excel(name ="可用量")
    private Float available;

    @Excel(name ="下限数量")
    private BigDecimal lowerLimit;
    @Excel(name ="上限数量 ")
    private BigDecimal upperLimit;

    /** 版本号 */
    private Long version;
}
