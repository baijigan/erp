package com.njrsun.modules.inv.service.impl;

import java.util.List;

import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.DateUtils;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.inv.domain.InvUnit;
import com.njrsun.modules.inv.mapper.InvUnitMapper;
import com.njrsun.modules.inv.service.IInvUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 计量单位Service业务层处理
 * 
 * @author njrsun
 * @date 2021-04-06
 */
@Service
public class InvUnitServiceImpl implements IInvUnitService
{
    @Autowired
    private InvUnitMapper invUnitMapper;

    /**
     * 查询计量单位
     * 
     * @param uniqueId 计量单位ID
     * @return 计量单位
     */
    @Override
    public InvUnit selectInvUnitById(Long uniqueId)
    {
        return invUnitMapper.selectInvUnitById(uniqueId);
    }

    /**
     * 查询计量单位列表
     * 
     * @param invUnit 计量单位
     * @return 计量单位
     */
    @Override
    public List<InvUnit> selectInvUnitList(InvUnit invUnit)
    {
        return invUnitMapper.selectInvUnitList(invUnit);
    }

    /**
     * 新增计量单位
     * 
     * @param invUnit 计量单位
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InvUnit insertInvUnit(InvUnit invUnit)
    {
        invUnit.setUpdateTime(DateUtils.getNowDate());
        invUnit.setCreateTime(DateUtils.getNowDate());
        invUnit.setCreateBy(SecurityUtils.getUsername());
        invUnit.setUpdateBy(SecurityUtils.getUsername());
         invUnitMapper.insertInvUnit(invUnit);

         return invUnitMapper.selectInvUnitById(invUnit.getUniqueId());
    }

    /**
     * 修改计量单位
     * 
     * @param invUnit 计量单位
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InvUnit updateInvUnit(InvUnit invUnit)
    {
        invUnit.setUpdateBy(SecurityUtils.getUsername());
        invUnit.setUpdateTime(DateUtils.getNowDate());
        int i = invUnitMapper.updateInvUnit(invUnit);
        if( i == 0){
            return null;
        }
        else{
            return invUnitMapper.selectInvUnitById(invUnit.getUniqueId());
        }
    }

    /**
     * 批量删除计量单位
     * 
     * @param uniqueIds 需要删除的计量单位ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteInvUnitByIds(Long[] uniqueIds)
    {
        int i;
        try{
           i = invUnitMapper.deleteInvUnitByIds(uniqueIds);
        }catch (Exception e){
            throw new CommonException("无法删除");
        }
        return i;
    }

    /**
     * 删除计量单位信息
     * 
     * @param uniqueId 计量单位ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteInvUnitById(Long uniqueId)
    {
        return invUnitMapper.deleteInvUnitById(uniqueId);
    }

    /**
     * 判断是否有重名
     * @param invUnit
     * @return
     */

    @Override
    public String isSameName(InvUnit invUnit) {
        invUnit.setName(invUnit.getName().trim());
        long l = invUnit.getUniqueId() == null ? -1L : invUnit.getUniqueId();
         InvUnit ex  =  invUnitMapper.isSameName(invUnit);
         if(StringUtils.isNotNull(ex) && ex.getUniqueId() != l){
             return UserConstants.UNIQUE;
         }
         return UserConstants.NOT_UNIQUE;
    }

    @Override
    public String isSameCode(InvUnit invUnit) {
        invUnit.setCode(invUnit.getCode().trim());
        long l = invUnit.getUniqueId() == null ? -1L : invUnit.getUniqueId();
         InvUnit ex =   invUnitMapper.isSameCode(invUnit);
         if(StringUtils.isNotNull(ex) && ex.getUniqueId() != l){
             return UserConstants.UNIQUE;
         }
        return UserConstants.NOT_UNIQUE;
    }
}

