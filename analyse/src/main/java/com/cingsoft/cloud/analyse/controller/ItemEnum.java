package com.cingsoft.cloud.analyse.controller;

public enum ItemEnum {
    TotalSalesYear(1, "本年销售额"),
    TotalSalesPrv(2, "同期销售额"),
    TotalDebtYear(3, "去年应收账款"),
    TotalDebtPrv(4, "同期应收账款"),
    TotalSalesPerson(5, "销售员销售汇总"),
    TotalSalesArea(6, "地区销售汇总"),
    ManuOut(7, "生产出库"),
    CoopOut(8, "委外出库"),
    OtherIn(9, "其它入库"),
    OtherOut(10, "其它出库"),
    DeductOut(11, "入库倒冲"),
    TackOut(12, "按单领用"),
    ArtificialOut(13, "手动领用");

    int id;
    String name;

    ItemEnum(int id, String name) {
        this.id = id;
        this.name= name;
    }
}

