package com.njrsun.modules.prs.mapper;

import com.njrsun.modules.prs.domain.PrsOrderExport;
import com.njrsun.modules.prs.domain.PrsPickExport;
import com.njrsun.modules.prs.domain.PrsPickMaster;
import com.njrsun.modules.prs.domain.PrsPickSalve;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 生产领料单Mapper接口
 * 
 * @author njrsun
 * @date 2021-11-18
 */
public interface PrsPickMasterMapper 
{
    /**
     * 查询生产领料单
     * 
     * @param uniqueId 生产领料单ID
     * @return 生产领料单
     */
    public PrsPickMaster selectPrsPickMasterById(PrsPickMaster prsPickMaster);

    /**
     * 查询生产领料单列表
     * 
     * @param prsPickMaster 生产领料单
     * @return 生产领料单集合
     */
    public List<PrsPickMaster> selectPrsPickMasterList(PrsPickMaster prsPickMaster);

    /**
     * 新增生产领料单
     * 
     * @param prsPickMaster 生产领料单
     * @return 结果
     */
    public int insertPrsPickMaster(PrsPickMaster prsPickMaster);

    /**
     * 修改生产领料单
     * 
     * @param prsPickMaster 生产领料单
     * @return 结果
     */
    public int updatePrsPickMaster(PrsPickMaster prsPickMaster);

    /**
     * 删除生产领料单
     * 
     * @param uniqueId 生产领料单ID
     * @return 结果
     */
    public int deletePrsPickMasterById(Long uniqueId);

    /**
     * 批量删除生产领料单
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsPickMasterByIds(List<PrsPickMaster> list);

    /**
     * 批量删除生产领料单从
     * 
     * @param customerIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsPickSalveByPrsCodes(List<PrsPickMaster> list);
    
    /**
     * 批量新增生产领料单从
     * 
     * @param prsPickSalveList 生产领料单从列表
     * @return 结果
     */
    public int batchPrsPickSalve(List<PrsPickSalve> prsPickSalveList);
    

    /**
     * 通过生产领料单ID删除生产领料单从信息
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    public int deletePrsPickSalveByPrsCode(Long uniqueId);

    PrsPickMaster selectPrsPickForUpdate(String prsCode);

    ArrayList<Long> selectPrsPickSalveIdByCode(String prsCode);

    void deletePrsPickSalveByIds(Long[] longs);

    int updatePrsPickSalve(ArrayList<PrsPickSalve> editData);

    List<PrsPickMaster> selectPrsInvoiceListByCode(List<PrsPickMaster> list);

    Integer updateWorkStatus(@Param("status") String value, @Param("item") PrsPickMaster prsPickMaster);

    List<PrsPickExport> getDetail(PrsPickExport prsPickExport);

    Integer changeStatus(@Param("check") String check, @Param("username") String username,@Param("item")PrsPickMaster prsPickMaster);

    List<String> selectPrsCode(PrsPickMaster prsPickMaster);

    List<PrsPickSalve> selectQuantity(String code);

    List<Map<String, String>> selectPrsPickByPrsOrderCode(PrsOrderExport prsOrderExport);

    List<Map<String, String>> upData(PrsPickMaster prsProductExport);

    PrsPickMaster selectPrsByOrderCode(String prsCode);

    List<Map<String, String>> lead(Map<String,String> query);
    List<Map<String, String>> leadInto(Map<String, String> map);

    int changeQuantity(@Param("uniqueId")String uniqueId, @Param("quantity") BigDecimal quantity);
    int changeQuantity_r(@Param("uniqueId") String uniqueId, @Param("quantity") BigDecimal quantity);

    PrsPickMaster selectPrsPickMasterForUpdate(String woCode);

    PrsPickSalve selectPrsPickSalveById(Long uniqueId);

    PrsPickSalve selectPrsPickSalveByIdForUpdate(Long uniqueId);
}
