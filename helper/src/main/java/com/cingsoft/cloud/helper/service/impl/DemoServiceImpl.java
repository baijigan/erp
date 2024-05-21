package com.cingsoft.cloud.helper.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cingsoft.cloud.helper.entity.Demo;
import com.cingsoft.cloud.helper.mapper.DemoMapper;
import com.cingsoft.cloud.helper.service.DemoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> implements DemoService {

    @Override
    public List<Map> getItemId(String itemCode){
        return baseMapper.getItemId(itemCode);
    }

    @Override
    public List<Map> getBalance(String itemCode) {
        return baseMapper.getBalance(itemCode);
    }

    @Override
    public List<Map> getInitial(String itemCode) {
        return baseMapper.getInitial(itemCode);
    }

    @Override
    public List<Map> getPurchaseIn(String itemCode){
        return baseMapper.getPurchaseIn(itemCode);
    }

    @Override
    public List<Map> getMachIn(String itemCode){
        return baseMapper.getMachIn(itemCode);
    }

    @Override
    public List<Map> getCoopIn(String itemCode){
        return baseMapper.getCoopIn(itemCode);
    }

    @Override
    public List<Map> getSaleOut(String itemCode){
        return baseMapper.getSaleOut(itemCode);
    }

    @Override
    public List<Map> getManuOut(String itemCode){
        return baseMapper.getManuOut(itemCode);
    }

    @Override
    public List<Map> getCoopOut(String itemCode){
        return baseMapper.getCoopOut(itemCode);
    }

    @Override
    public List<Map> getOtherIn(String itemCode){
        return baseMapper.getOtherIn(itemCode);
    }

    @Override
    public List<Map> getOtherOut(String itemCode){
        return baseMapper.getOtherOut(itemCode);
    }

    @Override
    public List<Map> getDeductOut(String itemCode){
        return baseMapper.getDeductOut(itemCode);
    }

    @Override
    public List<Map> getTackOut(String itemCode){
        return baseMapper.getTackOut(itemCode);
    }

    @Override
    public List<Map> getArtificialOut(String itemCode){
        return baseMapper.getArtificialOut(itemCode);
    }
}
