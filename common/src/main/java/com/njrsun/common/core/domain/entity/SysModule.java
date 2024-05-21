package com.njrsun.common.core.domain.entity;


import java.util.ArrayList;

/**
 * @author njrsun
 * @create 2021/4/26 11:23
 */

public class SysModule  {

    private Long uniqueId;

    private String code;

    private String name;

    private ArrayList<SysDictType> children;

    public ArrayList<SysDictType> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<SysDictType> children) {
        this.children = children;
    }

    public Long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SysModule{" +
                "uniqueId=" + uniqueId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", children=" + children +
                '}';
    }
}
