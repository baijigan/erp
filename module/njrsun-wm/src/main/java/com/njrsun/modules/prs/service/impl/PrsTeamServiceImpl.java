package com.njrsun.modules.prs.service.impl;

import java.util.List;

import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.modules.prs.mapper.PrsWorkerMapper;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.njrsun.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.njrsun.modules.prs.mapper.PrsTeamMapper;
import com.njrsun.modules.prs.domain.PrsTeam;
import com.njrsun.modules.prs.service.IPrsTeamService;

/**
 * 班组Service业务层处理
 * 
 * @author njrsun
 * @date 2021-12-23
 */
@Service
public class PrsTeamServiceImpl implements IPrsTeamService 
{
    @Autowired
    private PrsTeamMapper prsTeamMapper;

    @Autowired
    private PrsWorkerMapper prsWorkerMapper;

    @Autowired
    private SysCoderServiceImpl sysCoderService;

    /**
     * 查询班组
     * 
     * @param uniqueId 班组ID
     * @return 班组
     */
    @Override
    public PrsTeam selectPrsTeamById(Long uniqueId)
    {
        return prsTeamMapper.selectPrsTeamById(uniqueId);
    }

    /**
     * 查询班组列表
     * 
     * @param prsTeam 班组
     * @return 班组
     */
    @Override
    public List<PrsTeam> selectPrsTeamList(PrsTeam prsTeam)
    {
        return prsTeamMapper.selectPrsTeamList(prsTeam);
    }

    /**
     * 新增班组
     * 
     * @param prsTeam 班组
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPrsTeam(PrsTeam prsTeam)
    {
        prsTeam.setCreateBy(SecurityUtils.getUsername());
        prsTeam.setCode(sysCoderService.generate("prs_resource_type","3"));
        int rows = prsTeamMapper.insertPrsTeam(prsTeam);
        return rows;
    }

    /**
     * 修改班组
     * 
     * @param prsTeam 班组
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePrsTeam(PrsTeam prsTeam)
    {
        return prsTeamMapper.updatePrsTeam(prsTeam);
    }

    /**
     * 批量删除班组
     * 
     * @param uniqueIds 需要删除的班组ID
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePrsTeamByIds(Long[] uniqueIds)
    {
        return prsTeamMapper.deletePrsTeamByIds(uniqueIds);
    }

    public void check(PrsTeam prsTeam){
        if(StringUtils.isNull(prsTeam.getUniqueId())){
            prsTeam.setUniqueId(-1L);
        }
        PrsTeam check = prsTeamMapper.check(prsTeam);
        if(StringUtils.isNotNull(check)){
            throw  new CommonException("代码重复");
        }

    }
}
