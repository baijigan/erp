package com.njrsun.modules.prs.mapper;

import com.njrsun.modules.prs.domain.PrsEquipment;

import java.util.List;

/**
 * 设备列Mapper接口
 * 
 * @author njrsun
 * @date 2021-06-25
 */
public interface PrsEquipmentMapper 
{
    /**
     * 查询设备
     * 
     * @param uniqueId 设备列ID
     * @return 设备列
     */
    public PrsEquipment selectPrsEquipmentById(Long uniqueId);

    /**
     * 查询设备列列表
     * 
     * @param prsEquipment 设备列
     * @return 设备列集合
     */
    public List<PrsEquipment> selectPrsEquipmentList(PrsEquipment prsEquipment);

    /**
     * 新增设备
     *
     * @param prsEquipment 设备列
     * @return 结果
     */
    public int insertPrsEquipment(PrsEquipment prsEquipment);

    /**
     * 修改设备
     * 
     * @param prsEquipment 设备列
     * @return 结果
     */
    public int updatePrsEquipment(PrsEquipment prsEquipment);

    /**
     * 删除设备
     * 
     * @param uniqueId 设备列ID
     * @return 结果
     */
    public int deletePrsEquipmentById(Long uniqueId);

    /**
     * 批量删除设备
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsEquipmentByIds(Long[] uniqueIds);
}
