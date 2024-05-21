package com.njrsun.modules.prs.service;

import java.util.List;
import com.njrsun.modules.prs.domain.PrsBeltline;

/**
 * 生产线Service接口
 * 
 * @author njrsun
 * @date 2021-12-24
 */
public interface IPrsBeltlineService 
{
    /**
     * 查询生产线
     * 
     * @param uniqueId 生产线ID
     * @return 生产线
     */
    public PrsBeltline selectPrsBeltlineById(Long uniqueId);

    /**
     * 查询生产线列表
     * 
     * @param prsBeltline 生产线
     * @return 生产线集合
     */
    public List<PrsBeltline> selectPrsBeltlineList(PrsBeltline prsBeltline);

    /**
     * 新增生产线
     * 
     * @param prsBeltline 生产线
     * @return 结果
     */
    public int insertPrsBeltline(PrsBeltline prsBeltline);

    /**
     * 修改生产线
     * 
     * @param prsBeltline 生产线
     * @return 结果
     */
    public int updatePrsBeltline(PrsBeltline prsBeltline);

    /**
     * 批量删除生产线
     * 
     * @param uniqueIds 需要删除的生产线ID
     * @return 结果
     */
    public int deletePrsBeltlineByIds(Long[] uniqueIds);

    /**
     * 删除生产线信息
     * 
     * @param uniqueId 生产线ID
     * @return 结果
     */
    public int deletePrsBeltlineById(Long uniqueId);
}
