package com.njrsun.modules.inv.domain;

import lombok.Data;
import com.njrsun.common.annotation.Excel;

import java.util.List;

/**
 * 物料关联对象 inv_related
 * 
 * @author njrsun
 * @date 2021-05-31
 */
@Data
public class InvRelatedMaster
{
    private static final long serialVersionUID = 1L;

    /** 记录id */
    private Long uniqueId;

    /** 料品大类 */
    @Excel(name = "料品大类")
    private Long sortRoot;

    /** 物料分类 */
    @Excel(name = "物料分类")
    private Long sortId;

    /** 物料编码 */
    @Excel(name = "物料编码")
    private String code;

    /** 物料名称 */
    @Excel(name = "物料名称")
    private String name;

    /** 型号规格 */
    @Excel(name = "型号规格")
    private String attribute;

    private String unitCode;

    private String unitName;

    private Long version;


    private List<InvRelatedChild> children;

}
