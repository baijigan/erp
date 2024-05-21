package com.njrsun.modules.prs.domain;

import com.njrsun.common.annotation.Excel;
import lombok.Data;

/**
 * @author njrsun
 * @create 2022/1/7 8:47
 */
@Data
public class PrsProduceReport {

    @Excel(name = "生产线")
    private String beltline;

    @Excel(name = "物料编码")
    private String invCode;

    @Excel(name = "物料名称")
    private String invName;

    @Excel(name = "完工数量")
    private String invQuantity;

}
