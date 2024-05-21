package com.njrsun.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njrsun.common.core.domain.PageQuery;
import com.njrsun.common.core.page.TableDataInfo;
import com.njrsun.common.helper.LoginHelper;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.workflow.domain.WfCopy;
import com.njrsun.workflow.domain.bo.WfCopyBo;
import com.njrsun.workflow.domain.bo.WfTaskBo;
import com.njrsun.workflow.domain.vo.WfCopyVo;
import com.njrsun.workflow.mapper.WfCopyMapper;
import com.njrsun.workflow.service.IWfCopyService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 流程抄送Service业务层处理
 *
 * @author njrsun
 * @date 2022-05-19
 */
@RequiredArgsConstructor
@Service
public class WfCopyServiceImpl implements IWfCopyService {

    private final WfCopyMapper baseMapper;

    private final HistoryService historyService;

    /**
     * 查询流程抄送
     *
     * @param copyId 流程抄送主键
     * @return 流程抄送
     */
    @Override
    public WfCopyVo queryById(Long copyId){
        return baseMapper.selectVoById(copyId);
    }

    /**
     * 查询流程抄送列表
     *
     * @param bo 流程抄送
     * @return 流程抄送
     */
    @Override
    public TableDataInfo<WfCopyVo> selectPageList(WfCopyBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<WfCopy> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(WfCopy::getCreateTime);
        Page<WfCopyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    public TableDataInfo<WfCopyVo> selectPageList(WfCopyBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<WfCopy> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(WfCopy::getCreateTime);
        Page<WfCopyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询流程抄送列表
     *
     * @param bo 流程抄送
     * @return 流程抄送
     */
    @Override
    public List<WfCopyVo> selectList(WfCopyBo bo) {
        LambdaQueryWrapper<WfCopy> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<WfCopy> buildQueryWrapper(WfCopyBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<WfCopy> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, WfCopy::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getProcessName()), WfCopy::getProcessName, bo.getProcessName());
        lqw.like(StringUtils.isNotBlank(bo.getOriginatorName()), WfCopy::getOriginatorName, bo.getOriginatorName());
        return lqw;
    }

    @Override
    public Boolean makeCopy(WfTaskBo taskBo) {
        if (StringUtils.isBlank(taskBo.getCopyUserIds())) {
            // 若抄送用户为空，则不需要处理，返回成功
            return true;
        }
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(taskBo.getProcInsId()).singleResult();
        String[] ids = taskBo.getCopyUserIds().split(",");
        List<WfCopy> copyList = new ArrayList<>(ids.length);
        Long originatorId = LoginHelper.getUserId();
        String originatorName = LoginHelper.getNickName();
        String title = historicProcessInstance.getProcessDefinitionName() + "-" + taskBo.getTaskName();
        return baseMapper.insertBatch(copyList);
    }
}
