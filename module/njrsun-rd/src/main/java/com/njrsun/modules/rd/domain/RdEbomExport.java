package com.njrsun.modules.rd.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author njrsun
 * @create 2021/11/23 14:10
 */
@Data
public class RdEbomExport  extends BaseEntity {


    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long uniqueId;
    /** 主表ID */
    @Excel(name = "主表ID")
    private String rdCode;

    @Excel(name = "单据日期")
    private Date invoiceDate;
    /** 料品大类 */
    @Excel(name = "料品大类")
    private Long invSortRoot;

    private String workType;
    /** 料品分类 */
    @Excel(name = "料品分类")
    private Long invSortId;

    /** 料品编码 */
    @Excel(name = "料品编码")
    private String invCode;

    /** 料品名称 */
    @Excel(name = "料品名称")
    private String invName;

    /** 型号规格 */
    @Excel(name = "型号规格")
    private String invAttribute;

    /** 计量单位编码 */
    @Excel(name = "计量单位编码")
    private String unitCode;

    /** 计量单位名称 */
    @Excel(name = "计量单位名称")
    private String unitName;

    /** 补足（0否 1是） */
    @Excel(name = "补足", readConverterExp = "0=否,1=是")
    private String fill;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

}
