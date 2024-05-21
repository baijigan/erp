package com.njrsun.modules.inv.mapper;

import com.njrsun.common.dto.InvItemDTO;
import com.njrsun.modules.inv.domain.InvItem;
import com.njrsun.modules.inv.domain.InvItems;

import com.njrsun.modules.inv.domain.InvSort;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物料名称Mapper接口
 * 
 * @author njrsun
 * @date 2021-04-06
 */

public interface InvItemsMapper 
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
    public List<InvItems> selectInvItemsList(@Param("invItems")InvItems invItems,@Param("sortCodes") ArrayList<Long> sortCodes);

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
     * 删除物料名称
     * 
     * @param code 物料名称ID
     * @return 结果
     */
    public int deleteInvItemsByCode(@Param("code") String code);

    /**
     * 批量删除物料名称
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteInvItemsByIds(Long[] uniqueIds);


    InvItems selectInvItemsByName(@Param("name") String name, @Param("attribute") String attribute, @Param("sortId")Long sortId);

    InvItems selectInvItemBySortId(Long sortId);

    InvItems selectInvItemsByNameAndAttribute(@Param("name")String name, @Param("attribute")String attribute);

    List<InvItems> selectInvItemsListLeft(@Param("invItems")InvItems invItems,@Param("sortCodes")ArrayList<Long> sortCodes);

    InvItems selectInvItemByCode(@Param("code") String code,@Param("sortId") Long sortId);

    List<InvItems> selectInvItemListBySortId(Long sortId);

    InvItems selectInvItemsBySortIdNameAttribute(@Param("sortId") Long sortId, @Param("name") String name, @Param("attribute") String attribute);


    InvItems selectInvItemByCodes(String invCode);

     List<InvItems> selectInvItemByAbsName(String itemName);

    List<InvItems> selectInvItemsAndRelated(Map<String, Object> query);


    List<InvItems> selectInvItemsExport(InvItems invItems);
    List<InvItem> selectInvExport(InvItems invItems);

    List<InvItems> selectItemsByName(@Param("name") String name ,@Param("code") String code);

    InvItems selectInvItemsByCode(String code);

    Long selectVersionByCode(String code);

    String selectSortRootByCode(String invCode);

    ArrayList<InvItems> selectInvItemsBySortId(String sortId);

    List<InvItems> selectInvItemByContainsSortId(@Param("sortId") String sortId,@Param("invName") String invName,@Param("invCode") String invCode);

    List<InvItems> selectBatchBySortId(Map<String,String> query);

    void delOutSalve( @Param("invCode") String invCode, @Param("quantity") BigDecimal quantity);

    /**
     * 更加物料数量
     * @param quantity
     * @param invCode
     */
    void addInvQuantity(@Param("quantity")BigDecimal quantity, @Param("invCode") String invCode);

    void deleteInvItemsBySortId(Long sortId);

    List<HashMap<String, String>> selectInvItemsDetailByCode(String invCode);

    List<InvItems> selectBatchInvByCode(List<String> collect);


    InvItemDTO selectInvDto(String code);

    InvItemDTO selectInvDtoByCode(String invCode);
    InvItemDTO selectInvFromRd(String code);

    InvItemDTO selectInvIsExist();


    InvItems selectInvByName(String name);

    List<InvItems> selectInvBySortId(String sortId);

    List<InvItems> selectInvPesticideBySortId(String sortId);

    ArrayList<InvSort> selectInvSort();


    InvItems selectInvByCodeForUpdate(String invCode);

    void reduceQuantity(@Param("invCode") String invCode, @Param("quantity") BigDecimal quantity);

    void addLockQuantity(@Param("quantity") BigDecimal amount, @Param("invCode") String invCode);
}
