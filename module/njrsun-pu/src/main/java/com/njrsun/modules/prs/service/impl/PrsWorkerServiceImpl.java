package com.njrsun.modules.prs.service.impl;

import java.util.List;

import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.prs.domain.PrsTeam;
import com.njrsun.modules.prs.mapper.PrsProductMasterMapper;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.njrsun.modules.prs.mapper.PrsWorkerMapper;
import com.njrsun.modules.prs.domain.PrsWorker;
import com.njrsun.modules.prs.service.IPrsWorkerService;

/**
 * 工人Service业务层处理
 * 
 * @author njrsun
 * @date 2021-12-23
 */
@Service
public class PrsWorkerServiceImpl implements IPrsWorkerService 
{
    @Autowired
    private PrsWorkerMapper prsWorkerMapper;
    @Autowired
    private SysCoderServiceImpl sysCoderService;
    @Autowired
    private PrsProductMasterMapper prsProductMasterMapper;

    /**
     * 查询工人
     * 
     * @param uniqueId 工人ID
     * @return 工人
     */
    @Override
    public PrsWorker selectPrsWorkerById(Long uniqueId)
    {
        return prsWorkerMapper.selectPrsWorkerById(uniqueId);
    }

    /**
     * 查询工人列表
     * 
     * @param prsWorker 工人
     * @return 工人
     */
    @Override
    public List<PrsWorker> selectPrsWorkerList(PrsWorker prsWorker)
    {
        return prsWorkerMapper.selectPrsWorkerList(prsWorker);
    }

    /**
     * 新增工人
     * 
     * @param prsWorker 工人
     * @return 结果
     */
    @Override
    public int insertPrsWorker(PrsWorker prsWorker)
    {
         prsWorker.setCode(sysCoderService.generate("prs_resource_type","4"));
         prsWorker.setCreateBy(SecurityUtils.getUsername());
        return prsWorkerMapper.insertPrsWorker(prsWorker);
    }

    /**
     * 修改工人
     * 
     * @param prsWorker 工人
     * @return 结果
     */
    @Override
    public int updatePrsWorker(PrsWorker prsWorker)
    {
        prsWorker.setUpdateBy(SecurityUtils.getUsername());
        prsWorker.setUpdateBy(SecurityUtils.getUsername());
        return prsWorkerMapper.updatePrsWorker(prsWorker);
    }

    /**
     * 批量删除工人
     * 
     * @param uniqueIds 需要删除的工人ID
     * @return 结果
     */
    @Override
    public int deletePrsWorkerByIds(String[] uniqueIds)
    {
        return prsWorkerMapper.deletePrsWorkerByIds(uniqueIds);
    }

    /**
     * 删除工人信息
     * 
     * @param uniqueId 工人ID
     * @return 结果
     */
    @Override
    public int deletePrsWorkerById(Long uniqueId)
    {
        return prsWorkerMapper.deletePrsWorkerById(uniqueId);
    }


    public void check(PrsWorker prsWorker){
        if(StringUtils.isNull(prsWorker.getUniqueId())){
        }
    }
}
