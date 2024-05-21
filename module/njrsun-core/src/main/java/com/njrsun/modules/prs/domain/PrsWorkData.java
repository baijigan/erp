package com.njrsun.modules.prs.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author njrsun
 * @create 2022/1/5 10:32
 */
@Data
public class PrsWorkData {

    private String name ;

    private List<String> value = new ArrayList<>();

    private BigDecimal invQuantity;

    private BigDecimal schedule;
    private String attribute;
    private String unitName;
}
