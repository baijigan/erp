package com.njrsun.modules.inv.domain;

import com.njrsun.common.annotation.Excel;
import lombok.Data;

/**
 * @author njrsun
 * @create 2021/5/31 16:24
 */
@Data
public class InvRelated {

    private static final long serialVersionUID = 1L;

    /** 记录id */
    private Long uniqueId;

    /** 料品大类 */
    @Excel(name = "料品大类")
    private Long invSortRoot;

    /** 物料分类 */
    @Excel(name = "物料分类")
    private Long invSortId;

    /** 物料编码 */
    @Excel(name = "物料编码")
    private String invCode;

    /** 物料名称 */
    @Excel(name = "物料名称")
    private String invName;

    /** 型号规格 */
    @Excel(name = "型号规格")
    private String invAttribute;

    /** 料品大类 */
    @Excel(name = "料品大类")
    private Long relSortRoot;

    /** 物料分类 */
    @Excel(name = "物料分类")
    private Long relSortId;

    /** 物料编码 */
    @Excel(name = "物料编码")
    private String relCode;

    /** 物料名称 */
    @Excel(name = "物料名称")
    private String relName;

    /** 型号规格 */
    @Excel(name = "型号规格")
    private String relAttribute;

    /** 配方含量 */
    @Excel(name = "配方含量")
    private String relParam;
}
