package com.njrsun.modules.om.mapper;

import com.njrsun.modules.om.domain.ExportFapiao;
import com.njrsun.modules.om.domain.OmFapiaoMaster;
import com.njrsun.modules.om.domain.OmFapiaoReport;
import com.njrsun.modules.om.domain.OmFapiaoSalve;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 销售开票主Mapper接口
 * 
 * @author njrsun
 * @date 2022-04-04
 */
public interface OmFapiaoMasterMapper 
{
    /**
     * 查询销售开票主
     * 
     * @param uniqueId 销售开票主ID
     * @return 销售开票主
     */
    public OmFapiaoMaster selectOmFapiaoMasterById(String omCode);

    /**
     * 查询销售开票主列表
     * 
     * @param omFapiaoMaster 销售开票主
     * @return 销售开票主集合
     */
    public List<OmFapiaoMaster> selectOmFapiaoMasterList(OmFapiaoMaster omFapiaoMaster);

    /**
     * 新增销售开票主
     * 
     * @param omFapiaoMaster 销售开票主
     * @return 结果
     */
    public int insertOmFapiaoMaster(OmFapiaoMaster omFapiaoMaster);

    /**
     * 修改销售开票主
     * 
     * @param omFapiaoMaster 销售开票主
     * @return 结果
     */
    public int updateOmFapiaoMaster(OmFapiaoMaster omFapiaoMaster);

    /**
     * 删除销售开票主
     * 
     * @param uniqueId 销售开票主ID
     * @return 结果
     */
    public int deleteOmFapiaoMasterById(Long uniqueId);

    /**
     * 批量删除销售开票主
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteOmFapiaoMasterByIds(List<String> codes);

    /**
     * 批量删除销售开票从
     * 
     * @param customerIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteOmFapiaoSalveByOmCodes(List<String> codes);
    
    /**
     * 批量新增销售开票从
     * 
     * @param omFapiaoSalveList 销售开票从列表
     * @return 结果
     */
    public int batchOmFapiaoSalve(List<OmFapiaoSalve> omFapiaoSalveList);
    

    /**
     * 通过销售开票主ID删除销售开票从信息
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    public int deleteOmFapiaoSalveByOmCode(String omCode);

    OmFapiaoMaster selectOmFapiaoMasterForUpdate(String omCode);

    void updateStatus(@Param("omCode") String omCode, @Param("status") String s);

    void updateCheckStatus(@Param("omCode") String omCode,@Param("status") String s, @Param("username") String username,@Param("date") Date date);

    OmFapiaoMaster selectOmFapiaoMasterByCodeForUpdate(String omCode);

    Integer updateWorkStatus(@Param("value") String value,@Param("item") OmFapiaoMaster omFapiaoMaster);

    OmFapiaoMaster selectOmFapiaoMasterByMaster(OmFapiaoMaster omFapiaoMaster);

    List<OmFapiaoReport> selectFapiaoReport(OmFapiaoReport omFapiaoReport);

    List<Map<String, String>> getDetail(OmFapiaoMaster omFapiaoMaster);

    List<ExportFapiao> export(OmFapiaoMaster omFapiaoMaster);

    void updateEntryStatus(@Param("omCode") String omCode, @Param("status") String s, @Param("period") Integer period, @Param("vatNo") String vatNo);

    void updateVatStatus(@Param("omCode") String omCode, @Param("status") String s);

    void cancelAllEntries();
}
