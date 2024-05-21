package com.njrsun.modules.prs.mapper;

import java.util.List;
import com.njrsun.modules.prs.domain.PrsTeam;

/**
 * 班组Mapper接口
 * 
 * @author njrsun
 * @date 2021-12-23
 */
public interface PrsTeamMapper 
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
     * 删除班组
     * 
     * @param uniqueId 班组ID
     * @return 结果
     */
    public int deletePrsTeamById(Long uniqueId);

    /**
     * 批量删除班组
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsTeamByIds(Long[] uniqueIds);

    

    /**
     * 通过班组ID删除工人信息
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    public int deletePrsWorkerByTeamCode(Long uniqueId);

    PrsTeam check(PrsTeam prsTeam);
}
