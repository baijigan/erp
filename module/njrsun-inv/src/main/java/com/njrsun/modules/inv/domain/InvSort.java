package com.njrsun.modules.inv.domain;

import lombok.Data;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 物料分类对象 inv_sort
 * 
 * @author njrsun
 * @date 2021-04-06
 */
@Data
public class InvSort extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分类id */
    @Excel(name = "分类id")
    private Long sortId;

    /** 父类id */
    @Excel(name = "父类id")
    private Long parentId;

    /** 父级列表 */
    @Excel(name = "父级列表")
    private String ancestors;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String sortName;

    /** 分类代码 */
    @Excel(name = "分类代码")
    private String sortCode;

    /** 最后流水号 */
    @Excel(name = "最后流水号")
    private Long serialNumber;

    @Excel(name="计量单位编码")
    private String unitCode;
    @Excel(name = "计量单位名称")
    private String unitName;


    /** 流水号宽度 */
    @Excel(name = "流水号宽度")
    private Long serialLength;


    /** 分类状态（0正常 1停用） */
    @Excel(name = "分类状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    @Excel(name = "供应方式")
    private String supplyType;

    /** 版本号 */
    private Long version;
    private List<InvSort> children = new ArrayList<>();

}
