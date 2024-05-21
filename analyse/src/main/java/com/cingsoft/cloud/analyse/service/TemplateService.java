package com.cingsoft.cloud.analyse.service;

import com.alibaba.fastjson.JSONArray;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    public JSONArray getOrderList();
    public JSONArray getData(String dataType);
}
