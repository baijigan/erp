package com.njrsun.modules.prs.service.impl;

import java.util.List;

import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.njrsun.modules.prs.mapper.PrsJobsMapper;
import com.njrsun.modules.prs.domain.PrsJobs;
import com.njrsun.modules.prs.service.IPrsJobsService;

/**
 * 工种Service业务层处理
 * 
 * @author njrsun
 * @date 2022-01-11
 */
@Service
public class PrsJobsServiceImpl implements IPrsJobsService 
{
    @Autowired
    private PrsJobsMapper prsJobsMapper;

    @Autowired
    private SysCoderServiceImpl sysCoderService;

    /**
     * 查询工种
     * 
     * @param uniqueId 工种ID
     * @return 工种
     */
    @Override
    public PrsJobs selectPrsJobsById(Long uniqueId)
    {
        return prsJobsMapper.selectPrsJobsById(uniqueId);
    }

    /**
     * 查询工种列表
     * 
     * @param prsJobs 工种
     * @return 工种
     */
    @Override
    public List<PrsJobs> selectPrsJobsList(PrsJobs prsJobs)
    {
        return prsJobsMapper.selectPrsJobsList(prsJobs);
    }

    /**
     * 新增工种
     * 
     * @param prsJobs 工种
     * @return 结果
     */
    @Override
    public int insertPrsJobs(PrsJobs prsJobs)
    {
        prsJobs.setCreateBy(SecurityUtils.getUsername());
        prsJobs.setCode(sysCoderService.generate("prs_resource_type","6"));
        return prsJobsMapper.insertPrsJobs(prsJobs);
    }

    /**
     * 修改工种
     * 
     * @param prsJobs 工种
     * @return 结果
     */
    @Override
    public int updatePrsJobs(PrsJobs prsJobs)
    {
        prsJobs.setUpdateBy(SecurityUtils.getUsername());
        return prsJobsMapper.updatePrsJobs(prsJobs);
    }

    /**
     * 批量删除工种
     * 
     * @param uniqueIds 需要删除的工种ID
     * @return 结果
     */
    @Override
    public int deletePrsJobsByIds(Long[] uniqueIds)
    {
        return prsJobsMapper.deletePrsJobsByIds(uniqueIds);
    }

    /**
     * 删除工种信息
     * 
     * @param uniqueId 工种ID
     * @return 结果
     */
    @Override
    public int deletePrsJobsById(Long uniqueId)
    {
        return prsJobsMapper.deletePrsJobsById(uniqueId);
    }
}
