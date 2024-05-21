package com.cingsoft.cloud.analyse.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TemplateMapper {
    List<Map> getOrderList();
    List<Map> getData(@Param("dataType") String dataType);

}
