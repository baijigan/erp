package com.njrsun.modules.prs.mapper;

import java.util.List;
import com.njrsun.modules.prs.domain.PrsOperateSection;

/**
 * 标准工序段Mapper接口
 * 
 * @author njrsun
 * @date 2022-01-10
 */
public interface PrsOperateSectionMapper 
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
     * 删除标准工序段
     * 
     * @param uniqueId 标准工序段ID
     * @return 结果
     */
    public int deletePrsOperateSectionById(Long uniqueId);

    /**
     * 批量删除标准工序段
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsOperateSectionByIds(Long[] uniqueIds);
}
