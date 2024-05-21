package com.njrsun.modules.prs.service.impl;

import java.util.List;

import com.njrsun.common.constant.Constants;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.system.service.impl.SysCoderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.njrsun.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.njrsun.modules.prs.domain.PrsProcessSection;
import com.njrsun.modules.prs.mapper.PrsProcessRouteMapper;
import com.njrsun.modules.prs.domain.PrsProcessRoute;
import com.njrsun.modules.prs.service.IPrsProcessRouteService;

/**
 * 工艺路线Service业务层处理
 * 
 * @author njrsun
 * @date 2022-01-10
 */
@Service
public class PrsProcessRouteServiceImpl implements IPrsProcessRouteService 
{
    @Autowired
    private PrsProcessRouteMapper prsProcessRouteMapper;
    @Autowired
    private SysCoderServiceImpl sysCoderService;

    /**
     * 查询工艺路线
     * 
     * @param processCode 工艺路线ID
     * @return 工艺路线
     */
    @Override
    public PrsProcessRoute selectPrsProcessRouteById(String processCode)
    {
        return prsProcessRouteMapper.selectPrsProcessRouteById(processCode);
    }

    /**
     * 查询工艺路线列表
     * 
     * @param prsProcessRoute 工艺路线
     * @return 工艺路线
     */
    @Override
    public List<PrsProcessRoute> selectPrsProcessRouteList(PrsProcessRoute prsProcessRoute)
    {
        return prsProcessRouteMapper.selectPrsProcessRouteList(prsProcessRoute);
    }

    /**
     * 新增工艺路线
     * 
     * @param prsProcessRoute 工艺路线
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertPrsProcessRoute(PrsProcessRoute prsProcessRoute)
    {
        return rows;
    }

    /**
     * 修改工艺路线
     * 
     * @param prsProcessRoute 工艺路线
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePrsProcessRoute(PrsProcessRoute prsProcessRoute)
    {
        prsProcessRoute.setUpdateBy(SecurityUtils.getUsername());
        prsProcessRoute.setUpdateBy(SecurityUtils.getUsername());
        prsProcessRoute.setUpdateBy(SecurityUtils.getUsername());
        prsProcessRoute.setUpdateBy(SecurityUtils.getUsername());
        prsProcessRouteMapper.deletePrsProcessSectionByProcessCode(prsProcessRoute.getProcessCode());
        return prsProcessRouteMapper.updatePrsProcessRoute(prsProcessRoute);
    }

    /**
     * 批量删除工艺路线
     * 
     * @param list 需要删除的工艺路线ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deletePrsProcessRouteByIds(List<PrsProcessRoute> list)
    {
        List<PrsProcessRoute> prsProcessRoutes = prsProcessRouteMapper.selectPrsStatusList(list);
        for (PrsProcessRoute prsProcessRoute : prsProcessRoutes) {
            if(prsProcessRoute.getInvoiceStatus().equals(Constants.CHECK)){
                throw  new CommonException("审核状态，无法删除");
            }
        }
        List<String> collect = list.stream().map(PrsProcessRoute::getProcessCode).distinct().collect(Collectors.toList());
        prsProcessRouteMapper.deletePrsProcessSectionByProcessCodes(collect);
        return prsProcessRouteMapper.deletePrsProcessRouteByIds(collect);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrsProcessRoute batchCheck(List<PrsProcessRoute> list) {

       List<PrsProcessRoute>  status =  prsProcessRouteMapper.selectPrsStatusList(list);
        for (PrsProcessRoute prsProcessRoute : status) {
            if(!prsProcessRoute.getInvoiceStatus().equals("0")){
                throw  new CommonException(prsProcessRoute.getProcessCode() + "工艺路线非开立状态");
            }


        }
        for (PrsProcessRoute prsProcessRoute : list) {
            prsProcessRouteMapper.check(prsProcessRoute.getProcessCode(),SecurityUtils.getUsername(), Constants.CHECK);
        }
        if(list.size()> 0){
            return selectPrsProcessRouteById(list.get(0).getProcessCode());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrsProcessRoute batchAntiCheck(List<PrsProcessRoute> list) {
        List<PrsProcessRoute>  status =  prsProcessRouteMapper.selectPrsStatusList(list);
        for (PrsProcessRoute prsProcessRoute : status) {
            if(!prsProcessRoute.getInvoiceStatus().equals("1")){
                throw  new CommonException(prsProcessRoute.getProcessCode() + "工艺路线非审核状态");
            }


        }
        for (PrsProcessRoute prsProcessRoute : list) {
            prsProcessRouteMapper.anticheck(prsProcessRoute.getProcessCode(), Constants.RETURN);
        }
        if(list.size()> 0){
            return selectPrsProcessRouteById(list.get(0).getProcessCode());
        }
        return null;
    }


    /**
     * 新增工序段信息
     * 
     * @param prsProcessRoute 工艺路线对象
     */
    public void insertPrsProcessSection(PrsProcessRoute prsProcessRoute)
    {
        List<PrsProcessSection> prsProcessSectionList = prsProcessRoute.getPrsProcessSectionList();
        String code = prsProcessRoute.getProcessCode();
        if (StringUtils.isNotNull(prsProcessSectionList))
        {
            List<PrsProcessSection> list = new ArrayList<PrsProcessSection>();
            for (PrsProcessSection prsProcessSection : prsProcessSectionList)
            {
                prsProcessSection.setProcessCode(code);
                list.add(prsProcessSection);
            }
            if (list.size() > 0)
            {
                prsProcessRouteMapper.batchPrsProcessSection(list);
            }
        }
    }
}
