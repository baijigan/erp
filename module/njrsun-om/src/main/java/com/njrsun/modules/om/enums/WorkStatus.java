package com.njrsun.modules.om.enums;

/**
 * @author njrsun
 * @create 2021/9/18 17:55
 */
public enum WorkStatus {

    /*正常 */
    NORMAl("0"),
    /*挂起 */
    HANG("1"),
    /*手工关闭 */
    MANUAL_CLOSING("2"),
    /*系统关闭 */
    SYSTEM_CLOSING("3");

    private String value;

    WorkStatus(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
