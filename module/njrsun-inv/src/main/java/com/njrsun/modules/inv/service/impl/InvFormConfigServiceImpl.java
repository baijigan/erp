package com.njrsun.modules.inv.service.impl;

import java.util.List;
import java.util.Map;

import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.inv.domain.InvFormConfig;
import com.njrsun.modules.inv.mapper.InvFormConfigMapper;
import com.njrsun.modules.inv.service.IInvFormConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 单据配置Service业务层处理
 * 
 * @author njrsun
 * @date 2021-08-10
 */
@Service
public class InvFormConfigServiceImpl implements IInvFormConfigService
{
    @Autowired
    private InvFormConfigMapper invFormConfigMapper;

    /**
     * 查询单据配置
     * 
     * @param key 单据配置ID
     * @return 单据配置
     */
    @Override
    public InvFormConfig selectInvFormConfigByKey(String key)
    {
        return invFormConfigMapper.selectInvFormConfigById(key);
    }

    /**
     * 查询单据配置列表
     * 
     * @param query 单据配置
     * @return 单据配置
     */
    @Override
    public List<InvFormConfig> selectInvFormConfigList(Map<String,String > query)
    {
        return invFormConfigMapper.selectInvFormConfigList(query);
    }

    /**
     * 新增单据配置
     * 
     * @param invFormConfig 单据配置
     * @return 结果
     */
    @Override
    public int insertInvFormConfig(InvFormConfig invFormConfig)
    {
        invFormConfig.setCreateBy(SecurityUtils.getUsername());
        return invFormConfigMapper.insertInvFormConfig(invFormConfig);
    }

    /**
     * 修改单据配置
     * 
     * @param invFormConfig 单据配置
     * @return 结果
     */
    @Override
    public int updateInvFormConfig(InvFormConfig invFormConfig)
    {
         invFormConfig.setUpdateBy(SecurityUtils.getUsername());
        return invFormConfigMapper.updateInvFormConfig(invFormConfig);
    }

    /**
     * 批量删除单据配置
     * 
     * @param configIds 需要删除的单据配置ID
     * @return 结果
     */
    @Override
    public int deleteInvFormConfigByIds(Long[] configIds)
    {
        return invFormConfigMapper.deleteInvFormConfigByIds(configIds);
    }

    /**
     * 删除单据配置信息
     * 
     * @param configId 单据配置ID
     * @return 结果
     */
    @Override
    public int deleteInvFormConfigById(Long configId)
    {
        return invFormConfigMapper.deleteInvFormConfigById(configId);
    }

    @Override
    public int isSameConfig(InvFormConfig invFormConfig) {
        if(StringUtils.isNull(invFormConfig.getConfigId())) {
            invFormConfig.setConfigId(-1L);
        }
        return invFormConfigMapper.isSameConfig(invFormConfig);
    }
}
