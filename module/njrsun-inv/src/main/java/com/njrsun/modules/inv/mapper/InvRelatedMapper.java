package com.njrsun.modules.inv.mapper;

import com.njrsun.modules.inv.domain.InvRelated;
import com.njrsun.modules.inv.domain.InvRelatedChild;
import com.njrsun.modules.inv.domain.InvRelatedMaster;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 物料关联Mapper接口
 * 
 * @author njrsun
 * @date 2021-05-31
 */
public interface InvRelatedMapper 
{
    /**
     * 查询物料关联
     * 
     * @param uniqueId 物料关联ID
     * @return 物料关联
     */
    public InvRelatedMaster selectInvRelatedById(Long uniqueId);

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
     * @param invRelated 物料关联
     * @return 结果
     */
    public int insertInvRelated(List<InvRelated> invRelated);

    /**
     * 修改物料关联
     * 
     * @param invRelated 物料关联
     * @return 结果
     */
    public int updateInvRelated(InvRelated invRelated);

    /**
     * 删除物料关联
     * 
     * @param uniqueId 物料关联ID
     * @return 结果
     */
    public int deleteInvRelatedById(Long uniqueId);

    /**
     * 批量删除物料关联
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteInvRelatedByIds(Long[] uniqueIds);

    int deleteInvRelatedByCode(String invCode);

    ArrayList<InvRelated>  selectInvRelatedByCode(String invCode);

    void insertInvRelatedOne(InvRelated invRelated);

    InvRelated isExistRelated(@Param("code") String code, @Param("relCode") String relCode);

    ArrayList<InvRelatedChild> selectInvRelated(@Param("ex") ArrayList<String> ex,@Param("code") String code);

    void deleteInvRelatedBySortId(Long sortId);

    ArrayList<String>  selectInvCodeByNameAndParam(InvRelated invRelated);

    int countCode(String s);


    List<InvRelated> selectInvBySortId(String sortId);
}
