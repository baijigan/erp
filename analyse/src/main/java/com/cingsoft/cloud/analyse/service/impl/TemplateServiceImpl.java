package com.cingsoft.cloud.analyse.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cingsoft.cloud.analyse.mapper.TemplateMapper;
import com.cingsoft.cloud.analyse.service.TemplateService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private TemplateMapper templateMapper;

    public JSONArray getOrderList(){
        List<Map> list= templateMapper.getOrderList();
        JSONArray jsonArray = JSONArray.parseArray(JSONObject.toJSONString(list));
        return jsonArray;
    }

    public JSONArray getData(String dataType){
        List<Map> list= templateMapper.getData(dataType);
        JSONArray jsonArray = JSONArray.parseArray(JSONObject.toJSONString(list));
        return jsonArray;
    }

}
