package com.njrsun.system.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import com.njrsun.common.core.domain.entity.SysForm;
import com.njrsun.common.utils.spring.SpringUtils;
import com.njrsun.system.mapper.SysConfigMapper;
import com.njrsun.system.mapper.SysFormMapper;
import com.njrsun.system.service.ISysConfigService;
import com.njrsun.system.domain.SysConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.njrsun.common.annotation.DataSource;
import com.njrsun.common.constant.Constants;
import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.core.redis.RedisCache;
import com.njrsun.common.core.text.Convert;
import com.njrsun.common.enums.DataSourceType;
import com.njrsun.common.exception.CustomException;
import com.njrsun.common.utils.StringUtils;

/**
 * 参数配置 服务层实现
 * 
 * @author njrsun
 */

@Service
public class SysConfigServiceImpl implements ISysConfigService
{
    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysFormMapper sysFormMapper;

    @Value("${njrsun.tenant}")
    private boolean isTenant;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init()
    {
        // 租户模式不自启动缓存
        if(isTenant==true)return;

        List<SysConfig> configsList = configMapper.selectConfigList(new SysConfig());
        for (SysConfig config : configsList)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 查询参数配置信息
     * 
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public SysConfig selectConfigById(Long configId)
    {
        SysConfig config = new SysConfig();
        config.setConfigId(configId);
        return configMapper.selectConfig(config);
    }

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey)
    {
        String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue))
        {
            return configValue;
        }
        SysConfig config = new SysConfig();
        String[] split = configKey.split("\\.");
        config.setConfigKey(configKey);
        config.setModuleId(split[0]);
        SysConfig retConfig = configMapper.selectConfig(config);
        if (StringUtils.isNotNull(retConfig))
        {
            redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 查询参数配置列表
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config)
    {
        List<SysConfig> sysConfigs = configMapper.selectConfigList(config);
        if("".equals(config.getFormId())){
            return sysConfigs.stream().filter((t -> !"".equals(t.getFormId()))).collect(Collectors.toList());
        }else if("".equals(config.getModuleId())){
            return sysConfigs.stream().filter(t -> !"".equals(t.getModuleId())).collect(Collectors.toList());
        }else{
            return sysConfigs;
        }

    }

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config)
    {
        int row = configMapper.insertConfig(config);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config)
    {
        int row = configMapper.updateConfig(config);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     * 
     * @param configIds 需要删除的参数ID
     * @return 结果
     */
    @Override
    public int deleteConfigByIds(Long[] configIds)
    {
        for (Long configId : configIds)
        {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType()))
            {
                throw new CustomException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
        }
        int count = configMapper.deleteConfigByIds(configIds);
        if (count > 0)
        {
            Collection<String> keys = redisCache.keys(Constants.SYS_CONFIG_KEY + "*");
            redisCache.deleteObject(keys);
        }
        return count;
    }

    /**
     * 清空缓存数据
     */
    @Override
    public void clearCache()
    {
        Collection<String> keys = redisCache.keys(Constants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }

    /**
     * 校验参数键名是否唯一
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String checkConfigKeyUnique(SysConfig config)
    {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public  HashMap<String, String> selectConfigByFormPath(String[] formPath) {
        HashMap<String, String> map = new HashMap<>();
        if(formPath.length == 1 && formPath[0].equals("")){
            return map;
        }
        for (String s : formPath) {
            HashMap<String, String> formPathCache = getFormPathCache(s);
            if(StringUtils.isNotEmpty(formPathCache)){
                map.putAll(formPathCache);
            }
            else{
                ArrayList<SysConfig> sysConfigs = new ArrayList<>();
                SysForm  form =  sysFormMapper.selectInvFormByPath(s);
                if(StringUtils.isNotNull(form)){
                    String ancestors = form.getAncestors();
                    if(ancestors.length() != 1){
                        String substring = ancestors.substring(2);
                        String[] split = substring.split(",");
                        sysConfigs = sysFormMapper.selectInvFormByIds(split);
                    }
                    SysConfig sysConfig = new SysConfig();
                    sysConfig.setFormId(form.getFormId().toString());
                    ArrayList<SysConfig> sysConfigs1 = configMapper.selectConfigs(sysConfig);
                    if(sysConfigs1.size() != 0){
                        sysConfigs.addAll(sysConfigs1);
                    }
                    HashMap<String, String> stringStringHashMap = new HashMap<>();
                    for (SysConfig config : sysConfigs) {
                        map.put(config.getConfigKey(),config.getConfigValue());
                        stringStringHashMap.put(config.getConfigKey(),config.getConfigValue());
                    }
                    if(StringUtils.isNotEmpty(stringStringHashMap)){
                        setFormPathCache(s,stringStringHashMap);
                    }
                }
            }
        }
        return map;
    }

    @Override
    public String getLoginConfig() {
        return selectConfigByKey("sys.title.login");
    }

    /**
     * 设置cache key
     * 
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey)
    {
        return Constants.SYS_CONFIG_KEY + configKey;
    }
    public  HashMap<String, String> getFormPathCache(String formPath) {
        Object cacheObj = SpringUtils.getBean(RedisCache.class).getCacheObject(getCacheKey(formPath));
        if (StringUtils.isNotNull(cacheObj))
        {
            return StringUtils.cast(cacheObj);
        }
        return null;
    }

    public  void setFormPathCache(String formPath, HashMap<String, String> map) {
        SpringUtils.getBean(RedisCache.class).setCacheObject(getCacheKey(formPath), map);
    }
}
