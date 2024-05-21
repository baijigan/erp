package com.njrsun.modules.inv.mapper;

import com.njrsun.modules.inv.domain.InvFormConfig;

import java.util.List;
import java.util.Map;


/**
 * 单据配置Mapper接口
 * 
 * @author njrsun
 * @date 2021-08-10
 */
public interface InvFormConfigMapper 
{
    /**
     * 查询单据配置
     * 
     * @param key 单据配置ID
     * @return 单据配置
     */
    public InvFormConfig selectInvFormConfigById(String key);

    /**
     * 查询单据配置列表
     * 
     * @param query 单据配置
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
     * 删除单据配置
     * 
     * @param configId 单据配置ID
     * @return 结果
     */
    public int deleteInvFormConfigById(Long configId);

    /**
     * 批量删除单据配置
     * 
     * @param configIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteInvFormConfigByIds(Long[] configIds);

    int isSameConfig(InvFormConfig invFormConfig);

}
