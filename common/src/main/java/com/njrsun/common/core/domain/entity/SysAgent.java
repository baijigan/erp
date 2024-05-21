package com.njrsun.common.core.domain.entity;

/**
 * @author njrsun
 * @create 2021/8/18 9:56
 */
public class SysAgent {
    private String agentId;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override
    public String toString() {
        return "SysAgent{" +
                "agentId='" + agentId + '\'' +
                '}';
    }
}
