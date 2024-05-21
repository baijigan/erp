package com.njrsun.modules.inv.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.dto.InvSortDTO;
import com.njrsun.common.exception.CustomException;
import com.njrsun.common.utils.DateUtils;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.inv.domain.InvItems;
import com.njrsun.modules.inv.domain.InvSort;
import com.njrsun.modules.inv.mapper.InvItemsMapper;
import com.njrsun.modules.inv.mapper.InvSortMapper;
import com.njrsun.modules.inv.service.IInvSortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 物料分类Service业务层处理
 * 
 * @author njrsun
 * @date 2021-04-06
 */
@Service
public class InvSortServiceImpl implements IInvSortService
{
    @Autowired
    private InvSortMapper invSortMapper;
    @Autowired
    private  InvItemsMapper invItemsMapper;


    /**
     * 查询物料分类列表
     * 
     * @param invSort 物料分类
     * @return 物料分类
     */
    @Override
    public List<InvSort> selectInvSortList(InvSort invSort)
    {
        return invSortMapper.selectInvSortList(invSort);
    }

    public List<InvSortDTO> selectInvSortDTOList(InvSortDTO invSort)
    {
        return invSortMapper.selectInvSortDTOList(invSort);
    }

    /**
     * 新增物料分类
     * 
     * @param invSort 物料分类
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InvSort insertInvSort(InvSort invSort)
    {
        InvSort inv =null;
        if(invSort.getParentId() == 0){
            invSort.setAncestors("0");
         }else {
            inv =   invSortMapper.selectInvSortListBySortId(invSort.getParentId());
            invSort.setAncestors(inv.getAncestors()+","+inv.getSortId());
        }
        invSort.setCreateTime(DateUtils.getNowDate());
        invSort.setUpdateTime(DateUtils.getNowDate());
        invSort.setCreateBy(SecurityUtils.getUsername());
        invSort.setUpdateBy(SecurityUtils.getUsername());
        invSortMapper.insertInvSort(invSort);

        return invSortMapper.selectInvSortListBySortId(invSort.getSortId());
    }

    /**
     * 修改物料分类
     * 
     * @param invSort 物料分类
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InvSort updateInvSort(InvSort invSort)
    {
        invSort.setUpdateTime(DateUtils.getNowDate());
        invSort.setUpdateBy(SecurityUtils.getUsername());
        int i = invSortMapper.updateInvSort(invSort);
        if( i != 0){
            return invSortMapper.selectInvSortListBySortId(invSort.getSortId());
        }
        else {
            return null;
        }
    }

    /**
     * 批量删除物料分类
     * 
     * @param sortId 需要删除的物料分类ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteInvSortByIds(Long sortId)
    {
        return invSortMapper.deleteInvSortById(sortId);
    }


    @Override
    public List<InvSort> buildTree() {
        List<InvSort> invSorts = invSortMapper.selectInvSortList(null);
        return build(invSorts);
    }

    /**
     * 判断是否有重复Name
     * @param invSort
     * @return
     */
    @Override
    public String isSameName(InvSort invSort) {
        invSort.setSortName(invSort.getSortName().trim());
         InvSort ex =  invSortMapper.isSameName(invSort);
         Long id = invSort.getSortId() == null ?  -1L: invSort.getSortId();
         if(StringUtils.isNotNull(ex)  && !(ex.getSortId().equals(id))){
             return UserConstants.UNIQUE;
         }
         return UserConstants.NOT_UNIQUE;

    }

    /**
     * 判断是否有重复code
     * @param invSort
     * @return
     */
    @Override
    public String isSameCode(InvSort invSort) {
        invSort.setSortCode(invSort.getSortCode().trim());
           InvSort ex = invSortMapper.isSameCode(invSort);
        long l = invSort.getSortId() == null ? -1L : invSort.getSortId();
        if(StringUtils.isNotNull(ex) && !(ex.getSortId().equals(l))){
            return UserConstants.UNIQUE;
        }else{
            return UserConstants.NOT_UNIQUE;
        }

    }

    @Override
    public String hasItem(Long sortId) {
        InvItems ex = invItemsMapper.selectInvItemBySortId(sortId);
        if(StringUtils.isNull(ex)){
            return UserConstants.NOT_UNIQUE;
        }else {
            return UserConstants.UNIQUE;
        }
    }

    @Override
    public String selectInvSortBySortId(Long sortId) {
         return invSortMapper.selectInvSortBySortId(sortId);

    }

    @Override
    public String getExcelName(Long sortId) {
        StringBuilder code = new StringBuilder();
        InvSort invSort = invSortMapper.selectInvSortListBySortId(sortId);
        String ancestors = invSort.getAncestors();
        if(ancestors.length() > 1 ){
            String[] split = ancestors.split(",");
            for (int i = 1; i < split.length; i++) {
                InvSort sort = invSortMapper.selectInvSortListBySortId(Long.valueOf(split[i]));
                code.append(sort.getSortName()+"-");
            }
            code.append(invSort.getSortName()+"-");
        }
        else{
            code.append(invSort.getSortName()+"-");
        }
        return  code.deleteCharAt(code.lastIndexOf("-")).toString();
    }

    @Override
    public List<Map<String, String>> selectInvChildBySortName(String sortName) {
        InvSort invSort = invSortMapper.selectInvSortByRootName(sortName);
        String sortCode = invSort.getSortCode();
        List<Map<String, String>> maps = invSortMapper.selectInvChildBySortName(invSort.getSortId());
        for (Map<String, String> map : maps) {
            map.put("sort_code",sortCode+map.get("sort_code"));
        }
        return maps;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importData(List<InvSort> invSortList, String fileName) {
        if(StringUtils.isNull(invSortList) || invSortList.size() == 0){
            throw  new CustomException("导入数据不能为空");
        }
        for (InvSort invSort : invSortList) {
            invSort.setCreateBy(SecurityUtils.getUsername());
        }
        invSortMapper.clear();
        invSortMapper.batchInsertSort(invSortList);
        return "成功";
    }





    /**
     * 构建前端所需的导航树
     */
    private List<InvSort> build(List<InvSort> invSorts) {
        List<InvSort> returnList = new ArrayList<>();
        ArrayList<Long> tempList = new ArrayList<>();
        for (InvSort invSort : invSorts) {
            tempList.add(invSort.getSortId());
        }
        for (InvSort invSort : invSorts) {
             if(!tempList.contains(invSort.getParentId())){
                recursionFn(invSorts,invSort);
                returnList.add(invSort);
            }
        }
        return returnList;
    }

    /**
     * 递归寻找主节点所有的子节点
      */
    private void recursionFn(List<InvSort> invSorts, InvSort invSort) {
        List<InvSort> childList=  getChildList(invSorts,invSort);
        invSort.setChildren(childList);
        for (InvSort sort : childList) {
            if(hasChild(invSorts,sort)){
                recursionFn(invSorts,sort);
            }
        }

    }

    private boolean hasChild(List<InvSort> invSorts, InvSort sort) {
        return getChildList(invSorts, sort).size() > 0;
    }

    private List<InvSort> getChildList(List<InvSort> invSorts, InvSort invSort) {
        List<InvSort> list = new ArrayList<>();
        Iterator<InvSort> iterator = invSorts.iterator();
        while (iterator.hasNext()) {
            InvSort next = iterator.next();
            if(StringUtils.isNotNull(next.getParentId()) && next.getParentId().longValue() == invSort.getSortId().longValue()){
                list.add(next);
            }
        }
        return list;
    }

    @Override
    public String getSortRoot(String invSortId) {
        String s = invSortMapper.selectAncestorsBySortId(Long.valueOf(invSortId));
        String[] split1 = s.split(",");
        if(split1.length > 1){
            return split1[1];
        }else{
            return invSortId.toString();
        }
    }
}
