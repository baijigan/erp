package com.njrsun.modules.inv.service;

import com.njrsun.modules.inv.domain.InvFormConfig;

import java.util.List;
import java.util.Map;


/**
 * 单据配置Service接口
 * 
 * @author njrsun
 * @date 2021-08-10
 */
public interface IInvFormConfigService 
{
    /**
     * 查询单据配置
     * 
     * @param configId 单据配置ID
     * @return 单据配置
     */
    public InvFormConfig selectInvFormConfigByKey(String key);

    /**
     * 查询单据配置列表
     * 
     * @param invFormConfig 单据配置
     * @return 单据配置集合
     */
    public List<InvFormConfig> selectInvFormConfigList(Map<String,String > query);

    /**
     * 新增单据配置
     * 
     * @param invFormConfig 单据配置
     * @return 结果
     */
    public int insertInvFormConfig(InvFormConfig invFormConfig);

    /**
     * 修改单据配置
     * 
     * @param invFormConfig 单据配置
     * @return 结果
     */
    public int updateInvFormConfig(InvFormConfig invFormConfig);

    /**
     * 批量删除单据配置
     * 
     * @param configIds 需要删除的单据配置ID
     * @return 结果
     */
    public int deleteInvFormConfigByIds(Long[] configIds);

    /**
     * 删除单据配置信息
     * 
     * @param configId 单据配置ID
     * @return 结果
     */
    public int deleteInvFormConfigById(Long configId);

    int isSameConfig(InvFormConfig invFormConfig);

}
