package com.cingsoft.cloud.helper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cingsoft.cloud.helper.entity.Demo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface DemoService extends IService<Demo> {

    List<Map> getItemId(String itemCode);

    List<Map> getBalance(String itemCode);
    List<Map> getInitial(String itemCode);

    List<Map> getPurchaseIn(String itemCode);
    List<Map> getMachIn(String itemCode);
    List<Map> getCoopIn(String itemCode);
    List<Map> getSaleOut(String itemCode);
    List<Map> getManuOut(String itemCode);
    List<Map> getCoopOut(String itemCode);
    List<Map> getOtherIn(String itemCode);
    List<Map> getOtherOut(String itemCode);
    List<Map> getDeductOut(String itemCode);
    List<Map> getTackOut(String itemCode);
    List<Map> getArtificialOut(String itemCode);
}
