package com.njrsun.modules.prs.service.impl;

import java.util.List;

import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.modules.prs.domain.PrsEquipment;
import com.njrsun.modules.prs.mapper.PrsEquipmentMapper;
import com.njrsun.modules.prs.service.IPrsEquipmentService;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 设备列Service业务层处理
 * 
 * @author njrsun
 * @date 2021-06-25
 */
@Service
public class PrsEquipmentServiceImpl implements IPrsEquipmentService
{
    @Autowired
    private PrsEquipmentMapper prsEquipmentMapper;

        @Autowired
        private SysCoderServiceImpl sysCoderService;

    /**
     * 查询设备列
     * 
     * @param uniqueId 设备列ID
     * @return 设备列
     */
    @Override
    public PrsEquipment selectPrsEquipmentById(Long uniqueId)
    {
        return prsEquipmentMapper.selectPrsEquipmentById(uniqueId);
    }

    /**
     * 查询设备列列表
     * 
     * @param prsEquipment 设备列
     * @return 设备列
     */
    @Override
    public List<PrsEquipment> selectPrsEquipmentList(PrsEquipment prsEquipment)
    {
            return prsEquipmentMapper.selectPrsEquipmentList(prsEquipment);
    }

    /**
     * 新增设备列
     * 
     * @param prsEquipment 设备列
     * @return 结果
     */
    @Override
    public int insertPrsEquipment(PrsEquipment prsEquipment)
    {
        prsEquipment.setCode(sysCoderService.generate("prs_resource_type","5"));
        prsEquipment.setCode(sysCoderService.generate("prs_resource_type","5"));
        return prsEquipmentMapper.insertPrsEquipment(prsEquipment);
    }

    /**
     * 修改设备列
     * 
     * @param prsEquipment 设备列
     * @return 结果
     */
    @Override
    public int updatePrsEquipment(PrsEquipment prsEquipment)
    {
        return prsEquipmentMapper.updatePrsEquipment(prsEquipment);
    }

    /**
     * 批量删除设备列
     * 
     * @param uniqueIds 需要删除的设备列ID
     * @return 结果
     */
    @Override
    public int deletePrsEquipmentByIds(Long[] uniqueIds)
    {
        return prsEquipmentMapper.deletePrsEquipmentByIds(uniqueIds);
    }

    /**
     * 删除设备列信息
     * 
     * @param uniqueId 设备列ID
     * @return 结果
     */
    @Override
    public int deletePrsEquipmentById(Long uniqueId)
    {
        return prsEquipmentMapper.deletePrsEquipmentById(uniqueId);
    }
}
