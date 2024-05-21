package com.njrsun.modules.inv.service;

import com.njrsun.modules.inv.domain.InvItemsLimit;

import java.util.List;
import java.util.Map;

/**
 * @author njrsun
 * @create 2021/7/22 10:20
 */
public interface IInvItemsLimitService {
    List<InvItemsLimit> selectLimitList(Map<String, String> query);

    Integer updateLimit(InvItemsLimit invItemsLimit);
}
