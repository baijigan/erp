package com.njrsun.modules.prs.domain;

import com.njrsun.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author njrsun
 * @create 2022/1/12 10:25
 */
@Data
public class PrsOutputReport {
    @Excel(name = "标准车间")
    private String workType;
    @Excel(name ="生产线")
    private String beltline;
    @Excel(name = "工序编码")
    private String sectionCode;
    @Excel(name = "工序名称")
    private String sectionName;
    @Excel(name = "完工数量")
    private BigDecimal quantity;
    @Excel(name = "标准工时")
    private BigDecimal duration;
    @Excel(name = "使用工时")
    private String useDuration;
    @Excel(name = "报损数量")
    private BigDecimal badQuantity;
    @Excel(name = "计件方式")
    private String countType;
    @Excel(name = "工人编码")
    private String workersIds;
    @Excel(name = "工人名称")
    private String workersNames;
    @Excel(name = "工种")
    private String jods;
    @Excel(name = "系数")
    private BigDecimal ratio;


}
