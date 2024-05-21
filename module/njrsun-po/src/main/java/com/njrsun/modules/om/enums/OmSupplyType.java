package com.njrsun.modules.om.enums;

/**
 * @author njrsun
 * @create 2021/10/11 9:18
 */
public enum OmSupplyType {

    /**
     * 订产定销
     */
    MAKE_TO_ORDER("0"),

    /**
     * 订产通销
     */
    INVENTORY_ALLOCATION("1"),
    /**
     * 库存供应
     */
    K_C_G_Y("2"),
    /**
     * 委外加工
     */
    W_W_J_G("3"),
    /**
     * 外采直销
     */
    W_C_Z_X("4");


    OmSupplyType(String value){
        this.value = value;
    }
    private String value;
    public String getValue(){
        return this.value;
    }


}
