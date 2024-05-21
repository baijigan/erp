package com.njrsun.modules.inv.service.impl;

import java.util.List;
import com.njrsun.common.utils.DateUtils;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.modules.inv.domain.InvQuantity;
import com.njrsun.modules.inv.mapper.InvQuantityMapper;
import com.njrsun.modules.inv.service.IInvQuantityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 物料数量Service业务层处理
 * 
 * @author njrsun
 * @date 2021-04-07
 */

@Service
public class InvQuantityServiceImpl implements IInvQuantityService
{
    @Autowired
    private InvQuantityMapper invQuantityMapper;

    /**
     * 查询物料数量
     * @param uniqueId 物料数量ID
     * @return 物料数量
     */

    @Override
    public InvQuantity selectInvQuantityById(Long uniqueId)
    {
        return invQuantityMapper.selectInvQuantityById(uniqueId);
    }

    /**
     * 查询物料数量列表
     * 
     * @param invQuantity 物料数量
     * @return 物料数量
     */

    @Override
    public List<InvQuantity> selectInvQuantityList(InvQuantity invQuantity)
    {
        return invQuantityMapper.selectInvQuantityList(invQuantity);
    }

    /**
     * 新增物料数量
     * 
     * @param invQuantity 物料数量
     * @return 结果
     */
    @Override
    public int insertInvQuantity(InvQuantity invQuantity)
    {
        invQuantity.setUpdateTime(DateUtils.getNowDate());
        invQuantity.setUpdateBy(SecurityUtils.getUsername());
        return invQuantityMapper.insertInvQuantity(invQuantity);
    }

    /**
     * 修改物料数量
     * 
     * @param invQuantity 物料数量
     * @return 结果
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateInvQuantity(InvQuantity invQuantity)
    {
        invQuantity.setUpdateTime(DateUtils.getNowDate());
        invQuantity.setUpdateBy(SecurityUtils.getUsername());
        return invQuantityMapper.updateInvQuantity(invQuantity);
    }

    /**
     * 批量删除物料数量
     * 
     * @param uniqueIds 需要删除的物料数量ID
     * @return 结果
     */
    @Override
    public int deleteInvQuantityByIds(Long[] uniqueIds)
    {
        return invQuantityMapper.deleteInvQuantityByIds(uniqueIds);
    }

    /**
     * 删除物料数量信息
     * 
     * @param uniqueId 物料数量ID
     * @return 结果
     */
    @Override
    public int deleteInvQuantityById(Long uniqueId)
    {
        return invQuantityMapper.deleteInvQuantityById(uniqueId);
    }

    @Override
    public InvQuantity selectInvQuantityByName(String name) {
        return invQuantityMapper.selectInvQuantityByName(name);
    }

    @Override
    public InvQuantity selectInvQuantityByCode(String code) {
        return invQuantityMapper.selectInvQuantityByCode(code);
    }
}
