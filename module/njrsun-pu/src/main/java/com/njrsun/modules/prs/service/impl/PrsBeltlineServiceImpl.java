package com.njrsun.modules.prs.service.impl;

import java.util.List;

import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.njrsun.modules.prs.mapper.PrsBeltlineMapper;
import com.njrsun.modules.prs.domain.PrsBeltline;
import com.njrsun.modules.prs.service.IPrsBeltlineService;

/**
 * 生产线Service业务层处理
 * 
 * @author njrsun
 * @date 2021-12-24
 */
@Service
public class PrsBeltlineServiceImpl implements IPrsBeltlineService 
{
    @Autowired
    private PrsBeltlineMapper prsBeltlineMapper;
    
    @Autowired
    private SysCoderServiceImpl sysCoderService;

    /**
     * 查询生产线
     * 
     * @param uniqueId 生产线ID
     * @return 生产线
     */
    @Override
    public PrsBeltline selectPrsBeltlineById(Long uniqueId)
    {
        return prsBeltlineMapper.selectPrsBeltlineById(uniqueId);
    }

    /**
     * 查询生产线列表
     * 
     * @param prsBeltline 生产线
     * @return 生产线
     */
    @Override
    public List<PrsBeltline> selectPrsBeltlineList(PrsBeltline prsBeltline)
    {
        return prsBeltlineMapper.selectPrsBeltlineList(prsBeltline);
    }

    /**
     * 新增生产线
     * 
     * @param prsBeltline 生产线
     * @return 结果
     */
    @Override
    public int insertPrsBeltline(PrsBeltline prsBeltline)
    {
        prsBeltline.setCode(sysCoderService.generate("prs_resource_type","0"));
        prsBeltline.setCreateBy(SecurityUtils.getUsername());
        return prsBeltlineMapper.insertPrsBeltline(prsBeltline);
    }

    /**
     * 修改生产线
     * 
     * @param prsBeltline 生产线
     * @return 结果
     */
    @Override
    public int updatePrsBeltline(PrsBeltline prsBeltline)
    {
        prsBeltline.setUpdateBy(SecurityUtils.getUsername());
        return prsBeltlineMapper.updatePrsBeltline(prsBeltline);
    }

    /**
     * 批量删除生产线
     * 
     * @param uniqueIds 需要删除的生产线ID
     * @return 结果
     */
    @Override
    public int deletePrsBeltlineByIds(Long[] uniqueIds)
    {
        return prsBeltlineMapper.deletePrsBeltlineByIds(uniqueIds);
    }

    /**
     * 删除生产线信息
     * 
     * @param uniqueId 生产线ID
     * @return 结果
     */
    @Override
    public int deletePrsBeltlineById(Long uniqueId)
    {
        return prsBeltlineMapper.deletePrsBeltlineById(uniqueId);
    }
}
