package com.njrsun.modules.inv.service.impl;

import com.njrsun.modules.inv.domain.InvItemsLimit;
import com.njrsun.modules.inv.mapper.IIvItemsLimitMapper;
import com.njrsun.modules.inv.service.IInvItemsLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author njrsun
 * @create 2021/7/22 10:20
 */
@Service
public class InvItemsLimitServiceImpl implements IInvItemsLimitService {

    @Autowired
    private IIvItemsLimitMapper iIvItemsLimitMapper;
    @Override
    public List<InvItemsLimit> selectLimitList(Map<String, String> query) {

        return  iIvItemsLimitMapper.selectLimitList(query);



    }

    @Override
    public Integer updateLimit(InvItemsLimit invItemsLimit) {

        Integer i = iIvItemsLimitMapper.updateLimit(invItemsLimit);

        return i;
    }
}
