package com.njrsun.modules.inv.service.impl;


import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.inv.domain.InvPackage;
import com.njrsun.modules.inv.mapper.InvPackageMapper;
import com.njrsun.modules.inv.service.IInvPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 包装单位Service业务层处理
 * 
 * @author njrsun
 * @date 2021-04-06
 */
@Service
public class InvPackageServiceImpl implements IInvPackageService
{
    @Autowired
    private InvPackageMapper invPackageMapper;

    /**
     * 查询包装单位列表
     * 
     * @param invPackage 包装单位
     * @return 包装单位
     */
    @Override
    public List<InvPackage> selectInvPackageList(InvPackage invPackage)
    {
        return invPackageMapper.selectInvPackageList(invPackage);
    }

    /**
     * 新增包装单位
     * 
     * @param invPackage 包装单位
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InvPackage insertInvPackage(InvPackage invPackage)
    {
        invPackage.setCreateBy(SecurityUtils.getUsername());
 //       invPackage.setCreateTime(DateUtils.getNowDate());
//        invPackage.setUpdateTime(DateUtils.getNowDate());
//        invPackage.setUpdateBy(SecurityUtils.getUsername());
         invPackageMapper.insertInvPackage(invPackage);
         return  invPackageMapper.selectInvPackageById(invPackage.getUniqueId());
    }

    /**
     * 修改包装单位
     * 
     * @param invPackage 包装单位
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InvPackage updateInvPackage(InvPackage invPackage)
    {
        invPackage.setUpdateBy(SecurityUtils.getUsername());
      //  invPackage.setUpdateTime(DateUtils.getNowDate());
        int i = invPackageMapper.updateInvPackage(invPackage);
        if( i == 0 ){
            return null;
        }
        return invPackageMapper.selectInvPackageById(invPackage.getUniqueId());
    }

    /**
     * 批量删除包装单位
     * 
     * @param uniqueIds 需要删除的包装单位ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteInvPackageByIds(Long[] uniqueIds)
    {
        int i;
        try {
         i =    invPackageMapper.deleteInvPackageByIds(uniqueIds);
        }catch (Exception e){
            throw  new CommonException("无法删除");
        }
        return i;

    }

    /**
     * 删除包装单位信息
     * 
     * @param uniqueId 包装单位ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteInvPackageById(Long uniqueId)
    {
        return invPackageMapper.deleteInvPackageById(uniqueId);
    }

    @Override
    public InvPackage selectInvPackage(Long uniqueId) {

        return invPackageMapper.selectInvPackageById(uniqueId);
    }

    /**
     * 是否有相同的Name
     * @param invPackage
     * @return
     */
    @Override
    public String isSameName(InvPackage invPackage) {
             InvPackage ex  =  invPackageMapper.isSameName(invPackage);
              Long  id = invPackage.getUniqueId() == null ? -1L : invPackage.getUniqueId();
             if(StringUtils.isNotNull(ex) && !(ex.getUniqueId().equals(id))){
                 return UserConstants.UNIQUE;
             }
             else{
                 return UserConstants.NOT_UNIQUE;
             }
    }

    /**
     * 判断是否有相同的code
     * @param invPackage
     * @return
     */
    @Override
    public String isSameCode(InvPackage invPackage) {
        InvPackage ex = invPackageMapper.isSameCode(invPackage);
        Long id = invPackage.getUniqueId() == null ? -1L :invPackage.getUniqueId();
        if(StringUtils.isNotNull(ex) && !(ex.getUniqueId().equals(id))){
            return UserConstants.UNIQUE;
        }
        else {
            return UserConstants.NOT_UNIQUE;
        }

    }
}
