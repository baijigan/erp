package com.njrsun.modules.om.mapper;

import com.njrsun.modules.om.domain.OmCustomer;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 客户列Mapper接口
 * 
 * @author njrsun
 * @date 2021-06-24
 */
public interface OmCustomerMapper 
{
    /**
     * 查询客户列
     * 
     * @param shortName 客户列ID
     * @return 客户列
     */
    public OmCustomer selectOmCustomerById(String shortName);

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
    public int insertOmCustomer(OmCustomer omCustomer);

    /**
     * 修改客户列
     * 
     * @param omCustomer 客户列
     * @return 结果
     */
    public int updateOmCustomer(OmCustomer omCustomer);

    /**
     * 删除客户列
     * 
     * @param shortName 客户列ID
     * @return 结果
     */
    public int deleteOmCustomerById(@Param("shortName") String shortName,@Param("version") Integer i);

    /**
     * 批量删除客户列
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteOmCustomerByIds(Long[] uniqueIds);

    Integer isExist( @Param("name") String name,@Param("shortName") String shortName);

    Integer selectMinVersion(String shortName);

    Integer isUpdate(OmCustomer omCustomer);

    Integer updateImportOmCustomer(OmCustomer omCustomer);

    int dump();

    List<Map<String, String>> selectCustomerMapList();
}
