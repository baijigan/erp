package com.njrsun.modules.om.service;

import com.njrsun.modules.om.domain.OmCustomer;

import java.util.List;
import java.util.Map;


/**
 * 客户列Service接口
 * 
 * @author njrsun
 * @date 2021-06-24
 */
public interface IOmCustomerService 
{
    /**
     * 查询客户列
     * 
     * @param uniqueId 客户列ID
     * @return 客户列
     */
    public OmCustomer selectOmCustomerById(String uniqueId);

    /**
     * 查询客户列列表
     * 
     * @param omCustomer 客户列
     * @return 客户列集合
     */
    public List<OmCustomer> selectOmCustomerList(OmCustomer omCustomer);

    /**
     * 新增客户列
     * 
     * @param omCustomer 客户列
     * @return 结果
     */
    public Integer insertOmCustomer(OmCustomer omCustomer);

    /**
     * 修改客户列
     * 
     * @param omCustomer 客户列
     * @return 结果
     */
    public Integer updateOmCustomer(OmCustomer omCustomer);

    /**
     * 批量删除客户列
     * 
     * @param uniqueIds 需要删除的客户列ID
     * @return 结果
     */
    public int deleteOmCustomerByIds(Long[] uniqueIds);

    /**
     * 删除客户列信息
     * 
     * @param shortName 客户列ID
     * @return 结果
     */
    public int deleteOmCustomerById(String shortName);

    String importData(List<OmCustomer> omCustomerList, boolean updateSupport);

    int dumpOmCustomer();


    List<Map<String, String>> selectCustomerMapList();
}
