package com.njrsun.modules.prs.service;

import java.util.List;
import com.njrsun.modules.prs.domain.PrsProcessRoute;

/**
 * 工艺路线Service接口
 * 
 * @author njrsun
 * @date 2022-01-10
 */
public interface IPrsProcessRouteService 
{
    /**
     * 查询工艺路线
     * 
     * @param uniqueId 工艺路线ID
     * @return 工艺路线
     */
    public PrsProcessRoute selectPrsProcessRouteById(String processCode);

    /**
     * 查询工艺路线列表
     * 
     * @param prsProcessRoute 工艺路线
     * @return 工艺路线集合
     */
    public List<PrsProcessRoute> selectPrsProcessRouteList(PrsProcessRoute prsProcessRoute);

    /**
     * 新增工艺路线
     * 
     * @param prsProcessRoute 工艺路线
     * @return 结果
     */
    public int insertPrsProcessRoute(PrsProcessRoute prsProcessRoute);

    /**
     * 修改工艺路线
     * 
     * @param prsProcessRoute 工艺路线
     * @return 结果
     */
    public int updatePrsProcessRoute(PrsProcessRoute prsProcessRoute);

    /**
     * 批量删除工艺路线
     * 
     * @param uniqueIds 需要删除的工艺路线ID
     * @return 结果
     */
    public int deletePrsProcessRouteByIds(List<PrsProcessRoute> list);


    PrsProcessRoute batchCheck(List<PrsProcessRoute> list);

    PrsProcessRoute batchAntiCheck(List<PrsProcessRoute> list);
}
