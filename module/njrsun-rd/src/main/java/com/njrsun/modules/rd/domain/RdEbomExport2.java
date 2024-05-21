package com.njrsun.modules.rd.domain;

import com.njrsun.common.annotation.Excel;

import java.math.BigDecimal;

/**
 * @author njrsun
 * @create 2021/12/7 17:18
 */
public class RdEbomExport2 {
    /** 料品编码 */
    @Excel(name = "料品编码")
    private String invCode;

    /** 料品名称 */
    @Excel(name = "料品名称")
    private String invName;


    private String supplyType;

    /** 计量单位名称 */
    @Excel(name = "计量单位名称")
    private String unitName;

    /** 用料方式（0通用料 1专用料） */
    @Excel(name = "领料方式", readConverterExp = "0=按单领用,1=虚拟件,2=入库倒冲,3=其它")
    private String invOUtType;

    /** 比例 */
    @Excel(name = "比例")
    private BigDecimal ratio;

    /** 补足（0否 1是） */
    @Excel(name = "补足", readConverterExp = "0=否,1=是")
    private String fill;

}
