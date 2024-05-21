package com.njrsun.modules.inv.mapper;

import com.njrsun.modules.inv.domain.InvItemsLimit;

import java.util.List;
import java.util.Map;

/**
 * @author njrsun
 * @create 2021/7/22 10:19
 */
public interface IIvItemsLimitMapper {
    List<InvItemsLimit> selectLimitList(Map<String, String> query);

    int updateLimit(InvItemsLimit invItemsLimit);

}
