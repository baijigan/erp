package com.njrsun.modules.prs.service;

import java.util.List;
import com.njrsun.modules.prs.domain.PrsOperateSection;

/**
 * 标准工序段Service接口
 * 
 * @author njrsun
 * @date 2022-01-10
 */
public interface IPrsOperateSectionService 
{
    /**
     * 查询标准工序段
     * 
     * @param uniqueId 标准工序段ID
     * @return 标准工序段
     */
    public PrsOperateSection selectPrsOperateSectionById(Long uniqueId);

    /**
     * 查询标准工序段列表
     * 
     * @param prsOperateSection 标准工序段
     * @return 标准工序段集合
     */
    public List<PrsOperateSection> selectPrsOperateSectionList(PrsOperateSection prsOperateSection);

    /**
     * 新增标准工序段
     * 
     * @param prsOperateSection 标准工序段
     * @return 结果
     */
    public int insertPrsOperateSection(PrsOperateSection prsOperateSection);

    /**
     * 修改标准工序段
     * 
     * @param prsOperateSection 标准工序段
     * @return 结果
     */
    public int updatePrsOperateSection(PrsOperateSection prsOperateSection);

    /**
     * 批量删除标准工序段
     * 
     * @param uniqueIds 需要删除的标准工序段ID
     * @return 结果
     */
    public int deletePrsOperateSectionByIds(Long[] uniqueIds);

    /**
     * 删除标准工序段信息
     * 
     * @param uniqueId 标准工序段ID
     * @return 结果
     */
    public int deletePrsOperateSectionById(Long uniqueId);
}
