package com.njrsun.modules.prs.service.impl;

import java.util.List;

import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.njrsun.modules.prs.mapper.PrsOperateSectionMapper;
import com.njrsun.modules.prs.domain.PrsOperateSection;
import com.njrsun.modules.prs.service.IPrsOperateSectionService;

/**
 * 标准工序段Service业务层处理
 * 
 * @author njrsun
 * @date 2022-01-10
 */
@Service
public class PrsOperateSectionServiceImpl implements IPrsOperateSectionService 
{
    @Autowired
    private PrsOperateSectionMapper prsOperateSectionMapper;

    @Autowired
    private SysCoderServiceImpl sysCoderService;

    /**
     * 查询标准工序段
     * 
     * @param uniqueId 标准工序段ID
     * @return 标准工序段
     */
    @Override
    public PrsOperateSection selectPrsOperateSectionById(Long uniqueId)
    {
        return prsOperateSectionMapper.selectPrsOperateSectionById(uniqueId);
    }

    /**
     * 查询标准工序段列表
     * 
     * @param prsOperateSection 标准工序段
     * @return 标准工序段
     */
    @Override
    public List<PrsOperateSection> selectPrsOperateSectionList(PrsOperateSection prsOperateSection)
    {
        return prsOperateSectionMapper.selectPrsOperateSectionList(prsOperateSection);
    }

    /**
     * 新增标准工序段
     * 
     * @param prsOperateSection 标准工序段
     * @return 结果
     */
    @Override
    public int insertPrsOperateSection(PrsOperateSection prsOperateSection)
    {
        prsOperateSection.setCreateBy(SecurityUtils.getUsername());
        prsOperateSection.setCode(sysCoderService.generate("prs_resource_type","2"));
        prsOperateSection.setCode(sysCoderService.generate("prs_resource_type","2"));
        return prsOperateSectionMapper.insertPrsOperateSection(prsOperateSection);
        prsOperateSection.setCreateBy(SecurityUtils.getUsername());
        prsOperateSection.setCode(sysCoderService.generate("prs_resource_type","2"));
        prsOperateSection.setCode(sysCoderService.generate("prs_resource_type","2"));
        return prsOperateSectionMapper.insertPrsOperateSection(prsOperateSection);
    }


    /**
     * 修改标准工序段
     * @param prsOperateSection 标准工序段
     * @return 结果
     */
    @Override
    public int updatePrsOperateSection(PrsOperateSection prsOperateSection)
    {
        prsOperateSection.setUpdateBy(SecurityUtils.getUsername());
        return prsOperateSectionMapper.updatePrsOperateSection(prsOperateSection);
    }

    /**
     * 批量删除标准工序段
     * @param uniqueIds 需要删除的标准工序段ID
     * @return 结果
     */
    @Override
    public int deletePrsOperateSectionByIds(Long[] uniqueIds)
    {
        return prsOperateSectionMapper.deletePrsOperateSectionByIds(uniqueIds);
    }


    /**
     * 删除标准工序段信息
     * @param uniqueId 标准工序段ID
     * @return 结果
     */
    @Override
    public int deletePrsOperateSectionById(Long uniqueId)
    {
        return prsOperateSectionMapper.deletePrsOperateSectionById(uniqueId);
    }
}
