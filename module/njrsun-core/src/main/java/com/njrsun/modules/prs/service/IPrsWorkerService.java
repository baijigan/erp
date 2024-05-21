package com.njrsun.modules.prs.service;

import java.util.List;
import com.njrsun.modules.prs.domain.PrsWorker;

/**
 * 工人Service接口
 * 
 * @author njrsun
 * @date 2021-12-23
 */
public interface IPrsWorkerService 
{
    /**
     * 查询工人
     * 
     * @param uniqueId 工人ID
     * @return 工人
     */
    public PrsWorker selectPrsWorkerById(Long uniqueId);

    /**
     * 查询工人列表
     * 
     * @param prsWorker 工人
     * @return 工人集合
     */
    public List<PrsWorker> selectPrsWorkerList(PrsWorker prsWorker);

    /**
     * 新增工人
     * 
     * @param prsWorker 工人
     * @return 结果
     */
    public int insertPrsWorker(PrsWorker prsWorker);

    /**
     * 修改工人
     * 
     * @param prsWorker 工人
     * @return 结果
     */
    public int updatePrsWorker(PrsWorker prsWorker);

    /**
     * 批量删除工人
     * 
     * @param uniqueIds 需要删除的工人ID
     * @return 结果
     */
    public int deletePrsWorkerByIds(String[] uniqueIds);

    /**
     * 删除工人信息
     * 
     * @param uniqueId 工人ID
     * @return 结果
     */
    public int deletePrsWorkerById(Long uniqueId);
}
