package com.njrsun.modules.om.service;

import com.njrsun.modules.om.domain.ExportFapiao;
import com.njrsun.modules.om.domain.OmFapiaoMaster;
import com.njrsun.modules.om.domain.OmFapiaoReport;

import java.util.List;
import java.util.Map;

/**
 * 销售开票主Service接口
 * 
 * @author njrsun
 * @date 2022-04-04
 */
public interface IOmFapiaoMasterService 
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
     * 批量删除销售开票主
     * 
     * @param uniqueIds 需要删除的销售开票主ID
     * @return 结果
     */
    public int deleteOmFapiaoMasterByIds(List<String> omCodes);

    /**
     * 删除销售开票主信息
     * 
     * @param uniqueId 销售开票主ID
     * @return 结果
     */
    public int deleteOmFapiaoMasterById(Long uniqueId);

    OmFapiaoMaster check(List<OmFapiaoMaster> list);

    OmFapiaoMaster antiCheck(List<OmFapiaoMaster> list);

    public void changeWorkStatus(OmFapiaoMaster omFapiaoMaster);

    public OmFapiaoMaster selectOmFapiaoMasterByCode(OmFapiaoMaster omFapiaoMaster);

    public List<OmFapiaoReport> fapiaoReport(OmFapiaoReport omFapiaoReport);

    List<Map<String, String>> getDetail(OmFapiaoMaster omFapiaoMaster);

    List<ExportFapiao> export(OmFapiaoMaster omFapiaoMaster);

    OmFapiaoMaster entry(List<OmFapiaoMaster> list);

    OmFapiaoMaster antiEntry(List<OmFapiaoMaster> list);

    void cancelAllEntries();
}
