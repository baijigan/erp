package com.njrsun.modules.prs.mapper;

import java.util.List;
import com.njrsun.modules.prs.domain.PrsProcessRoute;
import com.njrsun.modules.prs.domain.PrsProcessSection;
import org.apache.ibatis.annotations.Param;

/**
 * 工艺路线Mapper接口
 * 
 * @author njrsun
 * @date 2022-01-10
 */
public interface PrsProcessRouteMapper 
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
     * 删除工艺路线
     * 
     * @param uniqueId 工艺路线ID
     * @return 结果
     */
    public int deletePrsProcessRouteById(Long uniqueId);

    /**
     * 批量删除工艺路线
     * 
     * @param uniqueIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsProcessRouteByIds(List<String> codes);

    /**
     * 批量删除工序段
     * 
     * @param customerIds 需要删除的数据ID
     * @return 结果
     */
    public int deletePrsProcessSectionByProcessCodes(List<String> codes);
    
    /**
     * 批量新增工序段
     * 
     * @param prsProcessSectionList 工序段列表
     * @return 结果
     */
    public int batchPrsProcessSection(List<PrsProcessSection> prsProcessSectionList);
    

    /**
     * 通过工艺路线ID删除工序段信息
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    public int deletePrsProcessSectionByProcessCode(String uniqueId);

    List<PrsProcessRoute> selectPrsStatusList(List<PrsProcessRoute> list);

    void check(@Param("code") String processCode,@Param("user")String username , @Param("status") String status);

    void anticheck(@Param("code") String processCode,@Param("status") String status);
}
