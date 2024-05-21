package com.njrsun.modules.prs.mapper;

import java.util.ArrayList;
import java.util.List;

import com.njrsun.modules.prs.domain.PrsYieldMaster;
import com.njrsun.modules.prs.domain.PrsYieldSalve;
import org.apache.ibatis.annotations.Param;

/**
 * 生产报工单主Mapper接口
 * 
 * @author njrsun
 * @date 2022-01-13
 */
public interface PrsYieldMasterMapper 
{
    /**
     * 查询生产报工单主
     * 
     * @param uniqueId 生产报工单主ID
     * @return 生产报工单主
     */
    public PrsYieldMaster selectPrsYieldMasterById(PrsYieldMaster prsYieldMaster);

    /**
     * 查询生产报工单主列表
     * 
     * @param prsYieldMaster 生产报工单主
     * @return 生产报工单主集合
     */
    public List<PrsYieldMaster> selectPrsYieldMasterList(PrsYieldMaster prsYieldMaster);

    /**
     * 新增生产报工单主
     * 
     * @param prsYieldMaster 生产报工单主
     * @return 结果
     */
    public int insertPrsYieldMaster(PrsYieldMaster prsYieldMaster);

    /**
     * 修改生产报工单主
     * 
     * @param prsYieldMaster 生产报工单主
     * @return 结果
     */
    public int updatePrsYieldMaster(PrsYieldMaster prsYieldMaster);

    /**
     * 删除生产报工单主
     * 
     * @param uniqueId 生产报工单主ID
     * @return 结果
     */
    public int deletePrsYieldMasterById(Long uniqueId);

    /**
     * 批量删除生产报工单主
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsYieldMasterByIds(List<PrsYieldMaster> uniqueIds);

    /**
     * 批量删除生产报工单子
     * 
     * @param customerIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsYieldSalveByPrsCodes(List<PrsYieldMaster> uniqueIds);
    
    /**
     * 批量新增生产报工单子
     * 
     * @param prsYieldSalveList 生产报工单子列表
     * @return 结果
     */
    public int batchPrsYieldSalve(List<PrsYieldSalve> prsYieldSalveList);
    

    /**
     * 通过生产报工单主ID删除生产报工单子信息
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    public int deletePrsYieldSalveByPrsCode(Long uniqueId);

    PrsYieldMaster selectPrsYieldMasterForUpdate(String prsCode);

    ArrayList<Long> selectPrsYieLdSalveIdByCode(String prsCode);

    void deletePrsYieIdSalveByIds(Long[] longs);

    int updatePrsYieIdSalveList(ArrayList<PrsYieldSalve> editData);

    List<PrsYieldMaster> selectPrsYieIdStatus(List<PrsYieldMaster> list);

    void check(@Param("prsCode") String prsCode, @Param("status") String status);

    List<PrsYieldSalve> selectPrsSlave(PrsYieldMaster prsYieldMaster);

    List<PrsYieldSalve> selectPrsSlaveByCode(PrsYieldMaster prsYieldMaster);

}
