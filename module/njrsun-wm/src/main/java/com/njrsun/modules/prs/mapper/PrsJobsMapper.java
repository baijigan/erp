package com.njrsun.modules.prs.mapper;

import java.util.List;
import com.njrsun.modules.prs.domain.PrsJobs;

/**
 * 工种Mapper接口
 * 
 * @author njrsun
 * @date 2022-01-11
 */
public interface PrsJobsMapper 
{
    /**
     * 查询工种
     * 
     * @param uniqueId 工种ID
     * @return 工种
     */
    public PrsJobs selectPrsJobsById(Long uniqueId);

    /**
     * 查询工种列表
     * 
     * @param prsJobs 工种
     * @return 工种集合
     */
    public List<PrsJobs> selectPrsJobsList(PrsJobs prsJobs);

    /**
     * 新增工种
     * 
     * @param prsJobs 工种
     * @return 结果
     */
    public int insertPrsJobs(PrsJobs prsJobs);

    /**
     * 修改工种
     * 
     * @param prsJobs 工种
     * @return 结果
     */
    public int updatePrsJobs(PrsJobs prsJobs);

    /**
     * 删除工种
     * 
     * @param uniqueId 工种ID
     * @return 结果
     */
    public int deletePrsJobsById(Long uniqueId);

    /**
     * 批量删除工种
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsJobsByIds(Long[] uniqueIds);

    PrsJobs selectPrsJobsByCode(String s);
}
