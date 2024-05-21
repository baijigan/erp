package com.njrsun.system.domain.vo;

import com.njrsun.common.core.domain.BaseEntity;

import java.util.Arrays;

/**
 * @author njrsun
 * @create 2021/8/28 9:48
 */

public class Assist  extends BaseEntity {
    private String[] omCode;
    private Boolean type;

    public String[] getOmCode() {
        return omCode;
    }

    public void setOmCode(String[] omCode) {
        this.omCode = omCode;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Assist{" +
                "omCode=" + Arrays.toString(omCode) +
                ", type=" + type +
                '}';
    }
}
