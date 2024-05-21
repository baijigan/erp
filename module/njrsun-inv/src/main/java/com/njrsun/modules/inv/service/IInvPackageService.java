package com.njrsun.modules.inv.service;


import com.njrsun.modules.inv.domain.InvPackage;

import java.util.List;

/**
 * 包装单位Service接口
 * 
 * @author njrsun
 * @date 2021-04-06
 */
public interface IInvPackageService 
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
    public InvPackage insertInvPackage(InvPackage invPackage);

    /**
     * 修改包装单位
     * 
     * @param invPackage 包装单位
     * @return 结果
     */
    public InvPackage updateInvPackage(InvPackage invPackage);

    /**
     * 批量删除包装单位
     * 
     * @param uniqueIds 需要删除的包装单位ID
     * @return 结果
     */
    public int deleteInvPackageByIds(Long[] uniqueIds);

    /**
     * 删除包装单位信息
     * 
     * @param uniqueId 包装单位ID
     * @return 结果
     */
    public int deleteInvPackageById(Long uniqueId);

    /**
     * 获取某个包装单位
     * @param uniqueId
     * @return
     */
    InvPackage selectInvPackage(Long uniqueId);

    /**
     * 判断是否重复Name
     * @param invPackage
     * @return
     */
    String isSameName(InvPackage invPackage);

    /**
     * 判断是否有重复Code
     * @param invPackage
     * @return
     */
    String isSameCode(InvPackage invPackage);

}
