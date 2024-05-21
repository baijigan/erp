package com.njrsun.workflow.service;

import com.njrsun.common.core.domain.PageQuery;
import com.njrsun.common.core.page.TableDataInfo;
import com.njrsun.flowable.core.domain.ProcessQuery;
import com.njrsun.workflow.domain.vo.WfDeployVo;

import java.util.List;

/**
 * @author njrsun
 * @createTime 2022/6/30 9:03
 */
public interface IWfDeployService {

    TableDataInfo<WfDeployVo> queryPageList(ProcessQuery processQuery, PageQuery pageQuery);

    TableDataInfo<WfDeployVo> queryPublishList(String processKey, PageQuery pageQuery);

    void updateState(String definitionId, String stateCode);

    String queryBpmnXmlById(String definitionId);

    void deleteByIds(List<String> deployIds);
}
