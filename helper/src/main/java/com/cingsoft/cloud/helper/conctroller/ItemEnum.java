package com.cingsoft.cloud.helper.conctroller;

public enum ItemEnum {
    Initial(1, "期初库存"),
    Balance(2, "现存量"),
    PurchaseIn(3, "采购入库"),
    MachIn(4, "生产入库"),
    CoopIn(5, "委外入库"),
    SaleOut(6, "销售出库"),
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
