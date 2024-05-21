package com.njrsun.modules.inv.mapper;

import com.njrsun.common.dto.InvSortDTO;
import com.njrsun.modules.inv.domain.InvSort;

import java.util.List;
import java.util.Map;


/**
 * 物料分类Mapper接口
 * 
 * @author njrsun
 * @date 2021-04-06
 */

public interface InvSortMapper 
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
    public int insertInvSort(InvSort invSort);

    /**
     * 修改物料分类
     * 
     * @param invSort 物料分类
     * @return 结果
     */
    public int updateInvSort(InvSort invSort);

    /**
     * 删除物料分类
     * 
     * @param uniqueId 物料分类ID
     * @return 结果
     */
    public int deleteInvSortById(Long uniqueId);

    /**
     * 批量删除物料分类
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteInvSortByIds(Long[] uniqueIds);

    /**
     * 根据 sortId 查询
     * @param sortId
     * @return
     */
    InvSort selectInvSortListBySortId(Long sortId);

    /**
     * 构建导航树
     * @param sortId
     * @return
     */
    int hasChild(Long sortId);

    /**
     *判断是否有重复Name
     * @param invSort
     * @return
     */

    InvSort isSameName(InvSort invSort);

    /**
     * 判断是否有重复Code
     * @param invSort
     * @return
     */

    InvSort isSameCode(InvSort invSort);

    void addSerialNumberBySortId(Long sortId);

    String selectInvSortBySortId(Long sortId);



    String selectAncestorsBySortId(Long sortId);

    InvSort selectInvSortByName(String ex);

    void updateSerialNumber(Long sortId);

    List<String> selectSortCodeByName(String[] split1);


    List<InvSortDTO> selectInvSortDTOList(InvSortDTO invSort);

    List<Map<String, String>> selectInvChildBySortName(Long sortId);

    InvSort selectInvSortByRootName(String sortName);

    void clear();

    void batchInsertSort(List<InvSort> invSortList);
}
