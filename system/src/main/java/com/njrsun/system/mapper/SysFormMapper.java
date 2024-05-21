package com.njrsun.system.mapper;


import com.njrsun.common.core.domain.entity.SysForm;
import com.njrsun.system.domain.SysConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * 物料单据Mapper接口
 * 
 * @author njrsun
 * @date 2021-08-10
 */
public interface SysFormMapper
{
    /**
     * 查询物料单据
     * 
     * @param formId 物料单据ID
     * @return 物料单据
     */
    public SysForm selectInvFormById(Long formId);

    /**
     * 查询物料单据列表
     * 
     * @param invForm 物料单据
     * @return 物料单据集合
     */
    public List<SysForm> selectInvFormList(SysForm invForm);

    /**
     * 新增物料单据
     * 
     * @param invForm 物料单据
     * @return 结果
     */
    public int insertInvForm(SysForm invForm);

    /**
     * 修改物料单据
     * 
     * @param invForm 物料单据
     * @return 结果
     */
    public int updateInvForm(SysForm invForm);

    /**
     * 删除物料单据
     * 
     * @param formId 物料单据ID
     * @return 结果
     */
    public int deleteInvFormById(Long formId);

    /**
     * 批量删除物料单据
     * 
     * @param formIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteInvFormByIds(Long[] formIds);

    int isSameInvForm(SysForm invForm);

    SysForm selectInvFormByPath(String formPath);

    ArrayList<SysConfig> selectInvFormByIds(String[] split);

    int selectInvFormByParentId(Long form);
}
