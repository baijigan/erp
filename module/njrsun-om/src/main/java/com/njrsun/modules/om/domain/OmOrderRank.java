package com.njrsun.modules.om.domain;

import com.njrsun.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/9/13 9:37
 */
@Data
public class OmOrderRank {
    @Excel(name = "料品大类")
    private String invSortRoot;
    /** 料品分类 */
    @Excel(name = "料品分类")
    private String invSortId;
    /** 料品编码 */
    @Excel(name = "料品编码")
    private String invCode;
    /** 料品名称 */
    @Excel(name = "料品名称")
    private String invName;
    /** 型号规格 */
    @Excel(name = "型号规格")
    private String invAttribute;
    /** 采购数量 */
    @Excel(name = "数量")
    private BigDecimal sumQuantity;
    /** 采购单价 */
    @Excel(name = "单价")
    private BigDecimal avgPrice;
    /** 采购金额 */
    @Excel(name = "金额")
    private BigDecimal sumAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}
