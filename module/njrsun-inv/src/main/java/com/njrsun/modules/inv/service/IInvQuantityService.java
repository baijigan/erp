package com.njrsun.modules.inv.service;

import com.njrsun.modules.inv.domain.InvQuantity;

import java.util.List;


/**
 * 物料数量Service接口
 * 
 * @author njrsun
 * @date 2021-04-07
 */
public interface IInvQuantityService 
{
    /**
     * 查询物料数量
     * 
     * @param uniqueId 物料数量ID
     * @return 物料数量
     */
    public InvQuantity selectInvQuantityById(Long uniqueId);

    /**
     * 查询物料数量列表
     * 
     * @param invQuantity 物料数量
     * @return 物料数量集合
     */
    public List<InvQuantity> selectInvQuantityList(InvQuantity invQuantity);

    /**
     * 新增物料数量
     * 
     * @param invQuantity 物料数量
     * @return 结果
     */
    public int insertInvQuantity(InvQuantity invQuantity);

    /**
     * 修改物料数量
     * 
     * @param invQuantity 物料数量
     * @return 结果
     */
    public int updateInvQuantity(InvQuantity invQuantity);

    /**
     * 批量删除物料数量
     * 
     * @param uniqueIds 需要删除的物料数量ID
     * @return 结果
     */
    public int deleteInvQuantityByIds(Long[] uniqueIds);

    /**
     * 删除物料数量信息
     * 
     * @param uniqueId 物料数量ID
     * @return 结果
     */
    public int deleteInvQuantityById(Long uniqueId);

    InvQuantity selectInvQuantityByName(String name);

    InvQuantity selectInvQuantityByCode(String code);
}
