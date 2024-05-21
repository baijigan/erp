package com.njrsun.modules.inv.mapper;


import com.njrsun.modules.inv.domain.InvPackage;

import java.util.List;

/**
 * 包装单位Mapper接口
 * 
 * @author njrsun
 * @date 2021-04-06
 */

public interface InvPackageMapper 
{

    /**
     * 查询包装单位列表
     * 
     * @param invPackage 包装单位
     * @return 包装单位集合
     */
    public List<InvPackage> selectInvPackageList(InvPackage invPackage);

    /**
     * 新增包装单位
     * 
     * @param invPackage 包装单位
     * @return 结果
     */
    public int insertInvPackage(InvPackage invPackage);

    /**
     * 修改包装单位
     * 
     * @param invPackage 包装单位
     * @return 结果
     */
    public int updateInvPackage(InvPackage invPackage);

    /**
     * 删除包装单位
     * 
     * @param uniqueId 包装单位ID
     * @return 结果
     */
    public int deleteInvPackageById(Long uniqueId);

    /**
     * 批量删除包装单位
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteInvPackageByIds(Long[] uniqueIds);

    /**
     * 判断是否有重复Name
     * @param invPackage
     * @return
     */

    public InvPackage isSameName(InvPackage invPackage);

    /**
     * 根据ID查询invPackage
     * @param id
     * @return
     */
    public InvPackage selectInvPackageById(Long id);

    /**
     * 判断是否有重复Code
     * @param invPackage
     * @return
     */

    public InvPackage isSameCode(InvPackage invPackage);

}
