package com.njrsun.modules.om.enums;

/**
 * @author njrsun
 * @create 2021/9/16 11:06
 */
public enum OmInvoiceType {

    /* 销售合同 */
    Contract("0"),
    /* 销售订单 */
    Order("1"),
    /* 发货通知单 */
    Deliver("2"),
    /* 退货通知单 */
    Reject("3");


    OmInvoiceType(String type){
        this.type = type;
    }
    private String type;
    public String getType(){
        return this.type;
    }
}
