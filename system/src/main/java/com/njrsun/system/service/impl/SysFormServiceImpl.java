package com.njrsun.system.service.impl;

import com.njrsun.common.core.domain.entity.SysForm;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.system.domain.SysConfig;
import com.njrsun.system.mapper.SysConfigMapper;
import com.njrsun.system.mapper.SysFormMapper;
import com.njrsun.system.service.ISysFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 物料单据Service业务层处理
 * 
 * @author njrsun
 * @date 2021-08-10
 */
@Service
public class SysFormServiceImpl implements ISysFormService
{
    @Autowired
    private SysFormMapper sysFormMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    /**
     * 查询物料单据
     * 
     * @param formId 物料单据ID
     * @return 物料单据
     */
    @Override
    public SysForm selectInvFormById(Long formId)
    {
        return sysFormMapper.selectInvFormById(formId);
    }

    /**
     * 查询物料单据列表
     * 
     * @param invForm 物料单据
     * @return 物料单据
     */
    @Override
    public List<SysForm> selectInvFormList(SysForm invForm)
    {
        return sysFormMapper.selectInvFormList(invForm);
    }

    /**
     * 新增物料单据
     * 
     * @param invForm 物料单据
     * @return 结果
     */
    @Override
    public int insertInvForm(SysForm invForm)
    {

        if(invForm.getParentId() == 0){
            invForm.setAncestors("0");
        }else{

            SysForm invForm1 = sysFormMapper.selectInvFormById(invForm.getParentId());
            invForm.setAncestors(invForm1.getAncestors() +","+ invForm1.getFormId());
        }
        invForm.setCreateBy(SecurityUtils.getUsername());
        return sysFormMapper.insertInvForm(invForm);
    }

    /**
     * 修改物料单据
     * 
     * @param invForm 物料单据
     * @return 结果
     */
    @Override
    public int updateInvForm(SysForm invForm)
    {
        invForm.setUpdateBy(SecurityUtils.getUsername());
        return sysFormMapper.updateInvForm(invForm);
    }

    /**
     * 批量删除物料单据
     * 
     * @param formIds 需要删除的物料单据ID
     * @return 结果
     */
    @Override
    public int deleteInvFormByIds(Long[] formIds)
    {
        for (Long formId : formIds) {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setFormId(formId.toString());
            ArrayList<SysConfig> sysConfigs = sysConfigMapper.selectConfigs(sysConfig);
            if(sysConfigs.size() != 0){
            throw  new CommonException("存在单据参数,无法删除分支");
        }
    }
        for (Long formId : formIds) {
           int i =  sysFormMapper.selectInvFormByParentId(formId);
           if( i != 0){
               throw  new CommonException("存在子项 无法删除");
           }
        }

        return sysFormMapper.deleteInvFormByIds(formIds);
    }

    /**
     * 删除物料单据信息
     * 
     * @param formId 物料单据ID
     * @return 结果
     */
    @Override
    public int deleteInvFormById(Long formId)
    {
        return sysFormMapper.deleteInvFormById(formId);
    }

    @Override
    public int isSameInvForm(SysForm invForm) {
        if( StringUtils.isNull(invForm.getFormId())){
            invForm.setFormId(-1L);
        }
        return sysFormMapper.isSameInvForm(invForm);

    }

    @Override
    public ArrayList<SysForm> build() {
        SysForm invForm = new SysForm();
        List<SysForm> invForms = sysFormMapper.selectInvFormList(invForm);

        ArrayList<SysForm> list = buildTree(invForms);

        return list;
    }

    private ArrayList<SysForm> buildTree(List<SysForm> invForms) {
        ArrayList<SysForm> invForms1 = new ArrayList<>();
        for (SysForm invForm : invForms) {
            if(invForm.getParentId() == 0){
                recursionFn(invForms,invForm);
                invForms1.add(invForm);
            }
        }
        return invForms1;
    }

    private void recursionFn(List<SysForm> invForms, SysForm invForm) {
       getChildList(invForms, invForm);
        List<SysForm> list = invForm.getChildren();
        for (SysForm form : list) {
            if(hasChild(invForms,form)){
                recursionFn(invForms,form);
            }
        }
    }

    private boolean hasChild(List<SysForm> invForms, SysForm form) {
        List<SysForm> list = new ArrayList<>();
        Iterator<SysForm> iterator = invForms.iterator();
        while (iterator.hasNext()) {
            SysForm next = iterator.next();
            if(StringUtils.isNotNull(next.getParentId()) && next.getParentId().longValue() == form.getFormId()){
                list.add(next);
            }
        }
        if(list.size() > 0){
            return true;
        }else{
            return false;
        }
    }


    private void getChildList(List<SysForm> invForms ,SysForm invForm){
        List<SysForm> list = invForm.getChildren();
        for (SysForm form : invForms) {
            if(form.getParentId().equals(invForm.getFormId())){
                list.add(form);
            }
        }

    }
}
