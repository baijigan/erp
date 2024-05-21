package com.cingsoft.cloud.helper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cingsoft.cloud.helper.entity.Demo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface DemoMapper extends BaseMapper<Demo> {

    List<Map> getItemId(@Param("itemCode")String itemCode);

    List<Map> getInitial(@Param("itemCode")String itemCode);
    List<Map> getBalance(@Param("itemCode")String itemCode);

    List<Map> getPurchaseIn(@Param("itemCode")String itemCode);
    List<Map> getMachIn(@Param("itemCode")String itemCode);
    List<Map> getCoopIn(@Param("itemCode")String itemCode);

    List<Map> getSaleOut(@Param("itemCode")String itemCode);
    List<Map> getManuOut(@Param("itemCode")String itemCode);
    List<Map> getCoopOut(@Param("itemCode")String itemCode);

    List<Map> getOtherIn(@Param("itemCode")String itemCode);
    List<Map> getOtherOut(@Param("itemCode")String itemCode);

    List<Map> getDeductOut(@Param("itemCode")String itemCode);
    List<Map> getTackOut(@Param("itemCode")String itemCode);
    List<Map> getArtificialOut(@Param("itemCode")String itemCode);
}
