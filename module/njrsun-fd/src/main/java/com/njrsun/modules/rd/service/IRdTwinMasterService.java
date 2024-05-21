package com.njrsun.modules.rd.service;

import java.util.List;
import java.util.Map;
import com.njrsun.modules.rd.domain.RdTwinMaster;
import com.njrsun.modules.rd.domain.RdTwinSalve;

/**
 * 孪生物料Service接口
 * 
 * @author njrsun
 * @date 2022-12-05
 */
public interface IRdTwinMasterService 
{
    /**
     * 查询孪生物料单据
     * 
     * @param  RdTwinMaster
     * @return RdTwinMaster
     */
    public RdTwinMaster selectRdTwinMasterByCode(RdTwinMaster rdTwinMaster);

    /**
     * 查询孪生物料列表
     * 
     * @param RdTwinMaster
     * @return List<RdTwinMaster>
     */
    public List<RdTwinMaster> selectRdTwinMasterList(RdTwinMaster rdTwinMaster);

    /**
     * 查询孪生物料清单
     *
     * @param RdTwinMaster
     * @return List<RdTwinMaster>
     */
    public List<Map<String,String>> selectRdTwinMasterDetail(RdTwinMaster rdTwinMaster);

    /**
     * 新增孪生物料
     * 
     * @param RdTwinMaster
     * @return 结果
     */
    public int insertRdTwinMaster(RdTwinMaster rdTwinMaster);

    /**
     * 修改孪生物料
     * 
     * @param RdTwinMaster
     * @return 结果
     */
    public int updateRdTwinMaster(RdTwinMaster rdTwinMaster);

    /**
     * 删除孪生物料
     * 
     * @param List<RdTwinMaster>
     * @return 结果
     */
    public int deleteRdTwinMasterByCodes(List<RdTwinMaster> rdTwinMaster);

    /**
     * 下一条孪生物料
     *
     * @param List<RdTwinMaster>
     * @return 结果
     */
    public RdTwinMaster getNext(RdTwinMaster rdTwinMaster);

    /**
     * 审核孪生物料
     *
     * @param List<RdTwinMaster>
     * @return 结果
     */
    public RdTwinMaster batchCheck(List<RdTwinMaster> list);

    /**
     * 反审核孪生物料
     *
     * @param List<RdTwinMaster>
     * @return 结果
     */
    public RdTwinMaster batchAntiCheck(List<RdTwinMaster> list);

    /**
     * 挂起孪生物料
     *
     * @param RdTwinMaster
     * @return 结果
     */
    public void changeWorkStatus(RdTwinMaster rdTwinMaster);

    /**
     * 链路孪生物料
     *
     * @param RdTwinSalve
     * @return 结果
     */
    public List<Map<String, String>> chainDetail(RdTwinSalve rdTwinSalve);
    public  List<Map<String,String>> linkDetail(Long uniqueId);

    /**
     * 供下游调用引入孪生物料
     *
     * @param RdTwinSalve
     * @return 结果
     */
    public List<Map<String, String>> lead(Map<String, String> list);
    public List<Map<String, String>> leadInto(Map<String, String> list);
}
