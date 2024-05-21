package com.njrsun.modules.prs.service;

import com.njrsun.modules.prs.domain.PrsEquipment;

import java.util.List;


/**
 * 设备列Service接口
 * 
 * @author njrsun
 * @date 2021-06-25
 */
public interface IPrsEquipmentService 
{
    /**
     * 查询设备列
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
     * 新增设备列
     * 
     * @param prsEquipment 设备列
     * @return 结果
     */
    public int insertPrsEquipment(PrsEquipment prsEquipment);

    /**
     * 修改设备列
     * 
     * @param prsEquipment 设备列
     * @return 结果
     */
    public int updatePrsEquipment(PrsEquipment prsEquipment);

    /**
     * 批量删除设备列
     * 
     * @param uniqueIds 需要删除的设备列ID
     * @return 结果
     */
    public int deletePrsEquipmentByIds(Long[] uniqueIds);

    /**
     * 删除设备列信息
     * 
     * @param uniqueId 设备列ID
     * @return 结果
     */
    public int deletePrsEquipmentById(Long uniqueId);
}
