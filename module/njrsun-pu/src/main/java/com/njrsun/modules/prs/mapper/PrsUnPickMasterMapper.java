package com.njrsun.modules.prs.mapper;

import com.njrsun.modules.prs.domain.PrsOrderExport;
import com.njrsun.modules.prs.domain.PrsUnPickExport;
import com.njrsun.modules.prs.domain.PrsUnPickMaster;
import com.njrsun.modules.prs.domain.PrsUnPickSalve;
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
public interface PrsUnPickMasterMapper
{
    /**
     * 查询生产领料单
     * 
     * @param uniqueId 生产领料单ID
     * @return 生产领料单
     */
    public PrsUnPickMaster selectPrsUnPickMasterById(PrsUnPickMaster prsUnPickMaster);

    /**
     * 查询生产领料单列表
     * 
     * @param prsUnPickMaster 生产领料单
     * @return 生产领料单集合
     */
    public List<PrsUnPickMaster> selectPrsUnPickMasterList(PrsUnPickMaster prsUnPickMaster);

    /**
     * 新增生产领料单
     * 
     * @param prsUnPickMaster 生产领料单
     * @return 结果
     */
    public int insertPrsUnPickMaster(PrsUnPickMaster prsUnPickMaster);

    /**
     * 修改生产领料单
     * 
     * @param prsUnPickMaster 生产领料单
     * @return 结果
     */
    public int updatePrsUnPickMaster(PrsUnPickMaster prsUnPickMaster);

    /**
     * 删除生产领料单
     * 
     * @param uniqueId 生产领料单ID
     * @return 结果
     */
    public int deletePrsUnPickMasterById(Long uniqueId);

    /**
     * 批量删除生产领料单
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsUnPickMasterByIds(List<PrsUnPickMaster> list);

    /**
     * 批量删除生产领料单从
     * 
     * @param customerIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsUnPickSalveByPrsCodes(List<PrsUnPickMaster> list);
    
    /**
     * 批量新增生产领料单从
     * 
     * @param prsUnPickSalveList 生产领料单从列表
     * @return 结果
     */
    public int batchPrsUnPickSalve(List<PrsUnPickSalve> prsUnPickSalveList);
    

    /**
     * 通过生产领料单ID删除生产领料单从信息
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    public int deletePrsUnPickSalveByPrsCode(Long uniqueId);

    PrsUnPickMaster selectPrsUnPickForUpdate(String prsCode);

    ArrayList<Long> selectPrsUnPickSalveIdByCode(String prsCode);

    void deletePrsUnPickSalveByIds(Long[] longs);

    int updatePrsUnPickSalve(ArrayList<PrsUnPickSalve> editData);

    List<PrsUnPickMaster> selectPrsInvoiceListByCode(List<PrsUnPickMaster> list);

    Integer updateWorkStatus(@Param("status") String value, @Param("item") PrsUnPickMaster prsUnPickMaster);

    List<PrsUnPickExport> getDetail(PrsUnPickExport prsUnPickExport);

    Integer changeStatus(@Param("check") String check, @Param("username") String username,@Param("item")PrsUnPickMaster prsUnPickMaster);

    List<String> selectPrsCode(PrsUnPickMaster prsUnPickMaster);

    List<PrsUnPickSalve> selectQuantity(String code);
    
    List<Map<String, String>> selectPrsUnPickByPrsOrderCode(PrsOrderExport prsOrderExport);

    List<Map<String, String>> upData(PrsUnPickMaster prsProductExport);

    PrsUnPickMaster selectPrsByOrderCode(String prsCode);

    PrsUnPickMaster selectPrsUnPickMasterByCodeForUpdate(String woCode);

    List<PrsUnPickSalve> selectUnPickSalveByCode(PrsUnPickMaster s);

    Integer updateCheck(PrsUnPickMaster unPickMaster, String notUnique, String username);

    int antiupdateCheck(PrsUnPickMaster unPickMaster, String yellow, Object o);

    List<Map<String, String>> lead(Map<String, String> query);

    List<Map<String, String>> leadInto(Map<String, String> query);

    PrsUnPickSalve selectPrsUnPickSalveById(Long uniqueId);

    void changeQuantity(@Param("uniqueId")Long uniqueId, @Param("quantity")BigDecimal quantity);
}
