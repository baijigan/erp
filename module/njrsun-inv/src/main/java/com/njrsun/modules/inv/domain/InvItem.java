package com.njrsun.modules.inv.domain;

import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 物料名称对象 inv_items
 * 
 * @author njrsun
 * @date 2021-04-06
 */
@Data
public class InvItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Excel(name="序号")
    private Long uniqueId;

   // @Excel(name="顶级目录")
    private String sortRoot;

    /** 分类ID */
    @Excel(name ="分类ID")
    private Long sortId;

    /** 物料id */
    @Excel(name = "物料CODE")
    private String code;

    /** 物料名称 */
    @Excel(name = "物料名称")
    private String name;

    /** 型号规格 */
    @Excel(name = "型号规格")
    private String attribute;
    @Excel(name ="规格描述")
    private String property;
 @Excel(name = "图纸号")
 private String drawingNo;
    /** 计量单位编码 */
    @Excel(name = "计量单位编码")
    private String unitCode;
    /** 计量单位名称 */
    @Excel(name = "计量单位名称")
    private String unitName;
    @Excel(name ="是否允许负库存",readConverterExp = "0=不启用,1=启用")
    private String enableDebt;
    /** 状态（0正常 1停用） */
    private String status;
    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;
    /** 版本号 */
    private Long version;
    @Excel(name ="当前库存数量")
    private BigDecimal quantity;
    @Excel(name = "下限数量")
    private BigDecimal lowerLimit;
    @Excel(name = "上限数量")
    private BigDecimal upperLimit;
    private String supplyType;
    @Excel(name = "锁定数量")
    private BigDecimal lockQuantity;
    @Excel(name = "在途数量")
    private BigDecimal wayQuantity;

    private List<InvRelatedChild> children = new ArrayList<>();

  private BigDecimal availableQuantity;


}
