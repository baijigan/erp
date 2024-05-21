package com.njrsun.modules.inv.service;

import com.njrsun.modules.inv.domain.InvUnit;

import java.util.List;


/**
 * 计量单位Service接口
 * 
 * @author njrsun
 * @date 2021-04-06
 */
public interface IInvUnitService 
{
    /**
     * 查询计量单位
     * 
     * @param uniqueId 计量单位ID
     * @return 计量单位
     */
    public InvUnit selectInvUnitById(Long uniqueId);

    /**
     * 查询计量单位列表
     * 
     * @param invUnit 计量单位
     * @return 计量单位集合
     */
    public List<InvUnit> selectInvUnitList(InvUnit invUnit);

    /**
     * 新增计量单位
     * 
     * @param invUnit 计量单位
     * @return 结果
     */
    public InvUnit insertInvUnit(InvUnit invUnit);

    /**
     * 修改计量单位
     * 
     * @param invUnit 计量单位
     * @return 结果
     */
    public InvUnit updateInvUnit(InvUnit invUnit);

    /**
     * 批量删除计量单位
     * 
     * @param uniqueIds 需要删除的计量单位ID
     * @return 结果
     */
    public int deleteInvUnitByIds(Long[] uniqueIds);

    /**
     * 删除计量单位信息
     * 
     * @param uniqueId 计量单位ID
     * @return 结果
     */
    public int deleteInvUnitById(Long uniqueId);

    /**
     * 判断是否有重复Name
     * @param invUnit
     * @return
     */
    String isSameName(InvUnit invUnit);

    /**
     * 判断是否有重复Code
     * @param invUnit
     * @return
     */
    String isSameCode(InvUnit invUnit);
}
