package com.njrsun.modules.inv.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.inv.Constant.InvConstants;
import com.njrsun.modules.inv.domain.InvItems;
import com.njrsun.modules.inv.domain.InvRelated;
import com.njrsun.modules.inv.domain.InvRelatedChild;
import com.njrsun.modules.inv.mapper.InvRelatedMapper;
import com.njrsun.modules.inv.mapper.InvSortMapper;
import com.njrsun.modules.inv.service.IInvRelatedService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 物料关联Service业务层处理
 * 
 * @author njrsun
 * @date 2021-05-31
 */
@Service
public class InvRelatedServiceImpl implements IInvRelatedService
{
    @Autowired
    private InvRelatedMapper invRelatedMapper;
    @Autowired
    private  InvItemsServiceImpl invItemsService;
    @Autowired
    private InvSortMapper invSortMapper;

    /**
     * 查询物料关联
     * 
     * @param invCode 物料关联ID
     * @return 物料关联
     */
    @Override
    public InvItems selectInvRelatedByCode(String invCode)
    {

        InvItems items1 = invItemsService.selectInvItemsByCode(invCode);
        InvItems items = new InvItems();
        BeanUtils.copyProperties(items1,items);
        ArrayList<InvRelated> invRelateds = invRelatedMapper.selectInvRelatedByCode(invCode);
        if(invRelateds.size() > 0) {
            List<InvRelatedChild> children = items.getChildren();
            for (InvRelated ex : invRelateds) {
                InvRelatedChild invRelatedChild = new InvRelatedChild();
                BeanUtils.copyProperties(ex, invRelatedChild);
                children.add(invRelatedChild);
            }
        }
        return items;

    }

    /**
     * 查询物料关联列表
     * 
     * @param query 物料关联
     * @return 物料关联
     */
    @Override
    public List<InvRelated> selectInvRelatedList(Map<String,Object> query)
    {
        return invRelatedMapper.selectInvRelatedList(query);
    }

    /**
     * 新增物料关联
     * 
     * @param invItems 物料关联
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String insertInvRelated(InvItems invItems)
    {

        String s = invItemsService.hasSameName(invItems);
        if(s.equals(UserConstants.NOT_UNIQUE)){
            invItemsService.insertInvItems(invItems);
            // 批量新增物料关联表
            ArrayList<InvRelated> invRelated1 = getInvRelated(invItems);
            Integer integer = hasSameInvRelated(invRelated1);
            if(integer >0){
                throw new CommonException(invItems.getName() +  "重复 ！ 添加失败..");
            }
            else{
                invRelatedMapper.insertInvRelated(invRelated1);
                return  invItems.getCode();
            }

        }
        else{
             throw  new CommonException(s + "重复");
        }
    }


    /**
     * 判断是否有相同的关联表数据
     * @param invRelated
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer hasSameInvRelated(ArrayList<InvRelated> invRelated) {
        int size = invRelated.size();
        Integer flag = 0;
        switch (size){
            case 1:{
              ArrayList<String>  list1 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(0));
                for (String s : list1) {
                    if(invRelatedMapper.countCode(s) == 1){
                       flag++;
                    }
                }
                break;
            }
            case 2:{
                ArrayList<String>   list1 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(0));
                ArrayList<String>  list2 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(1));
                for (String s : list1) {
                    for (String s1 : list2) {
                        if(s.equals(s1) && (2 ==invRelatedMapper.countCode(s))){
                            flag++;
                        }
                    }
                }
                break;
            }
            case 3:{
                ArrayList<String>   list1 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(0));
                ArrayList<String>  list2 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(1));
                ArrayList<String> list3 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(2));
                for (String s : list1) {
                    for (String s1 : list2) {
                        for (String s2 : list3) {
                            if(s.equals(s1) && s.equals(s2) && (3 ==invRelatedMapper.countCode(s))){
                                flag++;
                            }
                        }
                    }
                }
                break;
            }
            case 4:{
                ArrayList<String>  list1 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(0));
                ArrayList<String>  list2 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(1));
                ArrayList<String>  list3 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(2));
                ArrayList<String>  list4 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(3));
                for (String s : list1) {
                    for (String s1 : list2) {
                        for (String s2 : list3) {
                            for (String s3 : list4) {
                                if(s.equals(s1) && s.equals(s2) && s.equals(s3) && (4 ==invRelatedMapper.countCode(s))){
                                    flag++;
                                }
                            }

                        }
                    }
                }
                break;
            }
            case 5:{
                ArrayList<String> list1 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(0));
                ArrayList<String> list2 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(1));
                ArrayList<String>  list3 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(2));
                ArrayList<String>  list4 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(3));
                ArrayList<String>  list5 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(4));
                for (String s : list1) {
                    for (String s1 : list2) {
                        for (String s2 : list3) {
                            for (String s3 : list4) {
                                for (String s4 : list5) {
                                    if(s.equals(s1) && s.equals(s2) &&s.equals(s3) && s.equals(s4) && (5 ==invRelatedMapper.countCode(s))){
                                        flag++;
                                    }
                                }

                            }

                        }
                    }
                }
                break;
            }
            case 6:{
                ArrayList<String> list1 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(0));
                ArrayList<String> list2 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(1));
                ArrayList<String>  list3 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(2));
                ArrayList<String>  list4 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(3));
                ArrayList<String>  list5 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(4));
                ArrayList<String>  list6 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(5));
                for (String s : list1) {
                    for (String s1 : list2) {
                        for (String s2 : list3) {
                            for (String s3 : list4) {
                                for (String s4 : list5) {
                                    for (String s5 : list6) {
                                        if (s.equals(s1) && s.equals(s2) &&s.equals(s3) && s.equals(s4) && s.equals(s5) && 6 ==invRelatedMapper.countCode(s)) {
                                            flag++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case 7:{
                ArrayList<String> list1 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(0));
                ArrayList<String> list2 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(1));
                ArrayList<String>  list3 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(2));
                ArrayList<String>  list4 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(3));
                ArrayList<String>  list5 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(4));
                ArrayList<String>  list6 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(5));
                ArrayList<String> list7 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(6));
                for (String s : list1) {
                    for (String s1 : list2) {
                        for (String s2 : list3) {
                            for (String s3 : list4) {
                                for (String s4 : list5) {
                                    for (String s5 : list6) {
                                        for (String s6 : list7) {
                                            if (s.equals(s1) && s.equals(s2) &&s.equals(s3) && s.equals(s4) && s.equals(s5) && s.equals(s6) && 7 ==invRelatedMapper.countCode(s)) {
                                                flag++;
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case 8:{
                ArrayList<String> list1 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(0));
                ArrayList<String> list2 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(1));
                ArrayList<String>  list3 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(2));
                ArrayList<String>  list4 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(3));
                ArrayList<String>  list5 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(4));
                ArrayList<String>  list6 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(5));
                ArrayList<String> list7 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(6));
                ArrayList<String> list8 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(7));
                for (String s : list1) {
                    for (String s1 : list2) {
                        for (String s2 : list3) {
                            for (String s3 : list4) {
                                for (String s4 : list5) {
                                    for (String s5 : list6) {
                                        for (String s6 : list7) {
                                            for (String s7 : list8) {
                                                if (s.equals(s1) && s.equals(s2) &&s.equals(s3) && s.equals(s4) && s.equals(s5) && s.equals(s6) && s.equals(s7)    && 8 ==invRelatedMapper.countCode(s)) {
                                                    flag++;
                                                }
                                            }

                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case 9:{
                ArrayList<String> list1 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(0));
                ArrayList<String> list2 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(1));
                ArrayList<String>  list3 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(2));
                ArrayList<String>  list4 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(3));
                ArrayList<String>  list5 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(4));
                ArrayList<String>  list6 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(5));
                ArrayList<String> list7 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(6));
                ArrayList<String> list8 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(7));
                ArrayList<String> list9 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(8));
                for (String s : list1) {
                    for (String s1 : list2) {
                        for (String s2 : list3) {
                            for (String s3 : list4) {
                                for (String s4 : list5) {
                                    for (String s5 : list6) {
                                        for (String s6 : list7) {
                                            for (String s7 : list8) {
                                                for (String s8 : list9) {
                                                    if (s.equals(s1) && s.equals(s2) &&s.equals(s3) && s.equals(s4) && s.equals(s5) && s.equals(s6) && s.equals(s7) && s.equals(s8)   && 9 ==invRelatedMapper.countCode(s)) {
                                                        flag++;
                                                    }
                                                }

                                            }

                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case 10:{
                ArrayList<String> list1 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(0));
                ArrayList<String> list2 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(1));
                ArrayList<String>  list3 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(2));
                ArrayList<String>  list4 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(3));
                ArrayList<String>  list5 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(4));
                ArrayList<String>  list6 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(5));
                ArrayList<String> list7 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(6));
                ArrayList<String> list8 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(7));
                ArrayList<String> list9 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(8));
                ArrayList<String> list10 = invRelatedMapper.selectInvCodeByNameAndParam(invRelated.get(9));
                for (String s : list1) {
                    for (String s1 : list2) {
                        for (String s2 : list3) {
                            for (String s3 : list4) {
                                for (String s4 : list5) {
                                    for (String s5 : list6) {
                                        for (String s6 : list7) {
                                            for (String s7 : list8) {
                                                for (String s8 : list9) {
                                                    for (String s9 : list10) {
                                                        if (s.equals(s1) && s.equals(s2) &&s.equals(s3) && s.equals(s4) && s.equals(s5) && s.equals(s6) && s.equals(s7) && s.equals(s8) && s.equals(s9)   && 10 ==invRelatedMapper.countCode(s)) {
                                                            flag++;
                                                        }
                                                    }

                                                }

                                            }

                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            default:{
                    throw  new CommonException("添加失败!!");
            }
        }
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
     public ArrayList<InvRelated> getInvRelated(InvItems invRelated) {
        Long sortId = invRelated.getSortId();
        String ancestors =  invSortMapper.selectAncestorsBySortId(sortId);
        String[] split = ancestors.split(",");
        if(split.length > 1){
            invRelated.setSortRoot(split[1]);
        }
        else{
            invRelated.setSortRoot(sortId.toString());
        }
        List<InvRelatedChild> children = invRelated.getChildren();
        ArrayList<InvRelated> invRelateds = new ArrayList<>();
        for (InvRelatedChild child : children) {
            InvRelated ex = new InvRelated();
            ex.setInvSortRoot(Long.valueOf(invRelated.getSortRoot()));
            ex.setInvCode(invRelated.getCode());
            ex.setInvName(invRelated.getName());
            ex.setInvAttribute(invRelated.getAttribute());
            ex.setInvSortId(invRelated.getSortId());
            BeanUtils.copyProperties(child,ex);
            invRelateds.add(ex);
        }
        return invRelateds;

    }


    /**
     * 修改物料关联
     * 
     * @param invRelated 物料关联
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvItems updateInvRelated(InvItems invRelated)
    {
        String s = invItemsService.hasSameName(invRelated);
        if(UserConstants.NOT_UNIQUE.equals(s)){
            invItemsService.updateInvItems(invRelated);
        }else{
            throw  new CommonException( s + " 重复 ");
        }
        ArrayList<InvRelated> invRelated1 = getInvRelated(invRelated);
        invRelatedMapper.deleteInvRelatedByCode(invRelated.getCode());
        invRelatedMapper.insertInvRelated(invRelated1);
        return selectInvRelatedByCode(invRelated.getCode());

    }

    /**
     * 批量删除物料关联
     * 
     * @param uniqueIds 需要删除的物料关联ID
     * @return 结果
     */
    @Override
    public int deleteInvRelatedByIds(Long[] uniqueIds)
    {
        return invRelatedMapper.deleteInvRelatedByIds(uniqueIds);
    }

    /**
     * 删除物料关联信息
     * 
     * @param invCode 物料关联ID
     * @return 结果
     */
    @Override
    public int deleteInvRelatedByCode(String invCode)
    {
       return      invItemsService.deleteInvItemsByCode(invCode);

    }

    @Override
    public List<InvItems> getItems(String name) {
        return   invItemsService.getItems(name, InvConstants.YUANY);
    }

    @Override
    public void deleteInvRelatedBySortId(Long sortId) {
        invRelatedMapper.deleteInvRelatedBySortId(sortId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String checkRelated(String sortId) {
       ArrayList<InvItems> list =    invItemsService.selectInvItemsBySortId(sortId);
        StringBuilder errorMsg = new StringBuilder();
        Integer error = 0;
        for (InvItems items : list) {
            ArrayList<InvRelated> invRelated = splitInvItems(items);
            Integer integer = hasSameInvRelated(invRelated);
            if(integer != 1){
                error++;
                errorMsg.append("</br>" + error +"、 "+ items.getName() +  "检验不通过 !!");
            }
        }
        if(error > 0 ){
            throw  new CommonException(errorMsg.toString());
        }
        else{
            return "检验通过";
        }
    }

    @Override
    public Boolean containSameInv(InvItems invItems) {
        List<InvRelatedChild> children = invItems.getChildren();
        ArrayList<String> strings = new ArrayList<>();
        for (InvRelatedChild child : children) {
            strings.add(child.getRelCode());
        }
        HashSet<String> strings1 = new HashSet<>(strings);
        return strings.size() == strings1.size();
    }

    @Override
    public void review(String sortId) {
        List<InvItems> list = invItemsService.selectInvBySortId(sortId);
        Integer i = 0;
        StringBuilder msg = new StringBuilder();
        for (InvItems invRelated : list) {
            String invName = invRelated.getName();
            String[] split = invName.split("\\+");
            String [] split1 = null;
            for (String s : split) {
                if(s.contains("g/L")){
                     split1 = s.split("g/L");
                }else if (s.contains("g/kg")){
                     split1 = s.split("g/kg");
                }
                if(StringUtils.isNull(split1)){
                    msg.append("</br>" + (++i) + "、" + invName + "  名称不正确！");
                    break;
                }else{
                    try{
                        BigDecimal bigDecimal = new BigDecimal(split1[0]);
                    }catch (NumberFormatException e){
                        msg.append("</br>" + (++i) + "、" + invName + "  配比存在格式错误！");
                        break;
                    }
                    String name =  split1[1];
                    InvItems inv =   invItemsService.selectInvByName(name);
                    if(StringUtils.isNull(inv)){
                        msg.append("</br>" + (++i) + "、" + invName + "  在原料中无法获取！");
                        break;
                    }
                }
            }
        }
            if(i>0){
                throw  new CommonException(msg.toString());
            }
    }

    private ArrayList<InvRelated> splitInvItems(InvItems items) {
        String name = items.getName();
        HashMap<String, String> map = new HashMap<>();
        if(!name.contains("%")){
            throw new CommonException(name + " 有误！");
        }
        String[] split = name.split("\\+");
        for (String s : split) {
            String[] split1 = s.split("%");
            map.put(split1[1],split1[0]);
        }
        Set<String> strings = map.keySet();
        ArrayList<InvRelated> invRelateds = new ArrayList<>();
        for (String string : strings) {
            InvRelated invRelated = new InvRelated();
             invRelated.setRelName(string);
             invRelated.setRelParam(map.get(string));
             invRelated.setInvAttribute(items.getAttribute());
             invRelateds.add(invRelated);
        }
        return invRelateds;
    }
}
