package com.njrsun.modules.inv.service;

import com.njrsun.modules.inv.domain.InvItems;
import com.njrsun.modules.inv.domain.InvRelated;

import java.util.List;
import java.util.Map;


/**
 * 物料关联Service接口
 * 
 * @author njrsun
 * @date 2021-05-31
 */
public interface IInvRelatedService 
{
    /**
     * 查询物料关联
     * 
     * @param invCode 物料关联ID
     * @return 物料关联
     */
    public InvItems selectInvRelatedByCode(String invCode);

    /**
     * 查询物料关联列表
     * 
     * @param query 物料关联
     * @return 物料关联集合
     */
    public List<InvRelated> selectInvRelatedList(Map<String,Object> query);

    /**
     * 新增物料关联
     * 
     * @param invItems 物料关联
     * @return 结果
     */
    public String insertInvRelated(InvItems invItems);

    /**
     * 修改物料关联
     * 
     * @param invRelated 物料关联
     * @return 结果
     */
    public InvItems updateInvRelated(InvItems invRelated);

    /**
     * 批量删除物料关联
     * 
     * @param uniqueIds 需要删除的物料关联ID
     * @return 结果
     */
    public int deleteInvRelatedByIds(Long[] uniqueIds);

    /**
     * 删除物料关联信息
     * 
     * @param invCode 物料关联ID
     * @return 结果
     */
    public int deleteInvRelatedByCode(String invCode);

    List<InvItems> getItems(String name);

    void deleteInvRelatedBySortId(Long sortId);

    String checkRelated(String sortId);

    Boolean containSameInv(InvItems invItems);

    void review(String sortId);
}
