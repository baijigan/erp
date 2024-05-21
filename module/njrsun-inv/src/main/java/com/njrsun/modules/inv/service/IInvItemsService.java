package com.njrsun.modules.inv.service;


import com.njrsun.modules.inv.domain.InvItem;
import com.njrsun.modules.inv.domain.InvItems;
import com.njrsun.modules.inv.domain.InvSort;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物料名称Service接口
 * 
 * @author njrsun
 * @date 2021-04-06
 */
public interface IInvItemsService 
{
    /**
     * 查询物料名称
     * 
     * @param uniqueId 物料名称ID
     * @return 物料名称
     */
    public InvItems selectInvItemsById(Long uniqueId);

    /**
     * 查询物料名称列表
     * 
     * @param invItems 物料名称
     * @return 物料名称集合
     */
    public List<InvItems> selectInvItemsList(InvItems invItems, Boolean type, ArrayList<Long> sortCodes);


    List<InvItem> selectMachInvItemsList(InvItems items);
    /**
     * 新增物料名称
     * 
     * @param invItems 物料名称
     * @return 结果
     */
    public int insertInvItems(InvItems invItems);

    /**
     * 修改物料名称
     * 
     * @param invItems 物料名称
     * @return 结果
     */
    public int updateInvItems(InvItems invItems);

    /**
     * 批量删除物料名称
     * 
     * @param uniqueIds 需要删除的物料名称ID
     * @return 结果
     */
    public int deleteInvItemsByIds(Long[] uniqueIds);

    /**
     * 删除物料名称信息
     * 
     * @param uniqueId 物料名称ID
     * @return 结果
     */
    public int deleteInvItemsByCode(String uniqueId);

    String hasSameName(InvItems invItems);

    String importData(List<InvItems> invItemsList, boolean updateSupport, String operName,Long sortId);

    void deleteInvItemsAllByCodeId(Long sortId);

    String importInvRelated(List<InvItems> invItemsList, boolean updateSupport, String fileName, Long sortId);

    List<InvItems> selectInvItemsAndRelated(Map<String, Object> query);

    List<InvItems> getItems(String name,String code);

    ArrayList<InvItems> selectInvItemsBySortId(String sortId);

    List<InvItems> selectInvItemsByContainSortId(HashMap<String,String> query);

    InvItems selectInvItemsByCode(String invCode);

    List<InvItems> selectBatchBySortId(Map<String,String> query);

    List<HashMap<String, String>> selectInvItemsDetailByCode(String invCode);

    ArrayList<InvItems> importInv(String originalFilename, InputStream inputStream) throws Exception;

    void checkDuplicat(String sortId);

    ArrayList<InvItems> importMachInv(String originalFilename, InputStream inputStream,String sortRoot);

    ArrayList<InvSort> selectInvSort();


}
