package com.njrsun.modules.inv.service;

import com.njrsun.modules.inv.domain.InvSort;

import java.util.List;
import java.util.Map;


/**
 * 物料分类Service接口
 * 
 * @author njrsun
 * @date 2021-04-06
 */
public interface IInvSortService 
{
    /**
     * 查询物料分类列表
     * 
     * @param invSort 物料分类
     * @return 物料分类集合
     */
    public List<InvSort> selectInvSortList(InvSort invSort);

    /**
     * 新增物料分类
     * 
     * @param invSort 物料分类
     * @return 结果
     */
    public InvSort insertInvSort(InvSort invSort);

    /**
     * 修改物料分类
     * 
     * @param invSort 物料分类
     * @return 结果
     */
    public InvSort updateInvSort(InvSort invSort);

    /**
     * 批量删除物料分类
     * 
     * @param sortId 需要删除的物料分类ID
     * @return 结果
     */
    public int deleteInvSortByIds(Long sortId);

    /**
     * 构建导航树
     * @return
     */
    List<InvSort> buildTree();

    /**
     * 判断是否有重复Name
     * @param invSort
     * @return
     */
    String isSameName(InvSort invSort);

    /**
     *
     * 判断是否有重复Code
     * @param invSort
     * @return
     */
    String isSameCode(InvSort invSort);

    /**
     * 是否存在子项
     * @param sortId
     * @return
     */
    String hasItem(Long sortId);

    /**
     * 通过sortId 查询 sortName
     * @param sortId
     * @return
     */
    String selectInvSortBySortId(Long sortId);

    String getExcelName(Long sortId);



    List<Map<String,String>> selectInvChildBySortName(String sortName);

    String importData(List<InvSort> invSortList, String fileName);

    String getSortRoot(String sortId);
}
