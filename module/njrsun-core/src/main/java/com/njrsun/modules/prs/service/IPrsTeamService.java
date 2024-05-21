package com.njrsun.modules.prs.service;

import java.util.List;
import com.njrsun.modules.prs.domain.PrsTeam;

/**
 * 班组Service接口
 * 
 * @author njrsun
 * @date 2021-12-23
 */
public interface IPrsTeamService 
{
    /**
     * 查询班组
     * 
     * @param uniqueId 班组ID
     * @return 班组
     */
    public PrsTeam selectPrsTeamById(Long uniqueId);

    /**
     * 查询班组列表
     * 
     * @param prsTeam 班组
     * @return 班组集合
     */
    public List<PrsTeam> selectPrsTeamList(PrsTeam prsTeam);

    /**
     * 新增班组
     * 
     * @param prsTeam 班组
     * @return 结果
     */
    public int insertPrsTeam(PrsTeam prsTeam);

    /**
     * 修改班组
     * 
     * @param prsTeam 班组
     * @return 结果
     */
    public int updatePrsTeam(PrsTeam prsTeam);

    /**
     * 批量删除班组
     * 
     * @param uniqueIds 需要删除的班组ID
     * @return 结果
     */
    public int deletePrsTeamByIds(Long[] uniqueIds);

}
