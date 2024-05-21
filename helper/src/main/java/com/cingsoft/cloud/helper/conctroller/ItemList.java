package com.cingsoft.cloud.helper.conctroller;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cingsoft.cloud.helper.HelperApplication;
import com.cingsoft.cloud.helper.service.DemoService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ItemList {
    public ItemEnum m_item= null;
    public JSONArray jsonArray= null;

    public static JSONArray m_data= new JSONArray();
    public static JSONArray m_ref= new JSONArray();

    ItemList(ItemEnum item, String itemCode){
        long startTime = System.currentTimeMillis();

        this.m_item= item;
        this.jsonArray= queryList(itemCode);

        long endTime = System.currentTimeMillis();
        System.out.println(m_item.name()+ " 程序运行时间：" + (endTime - startTime) + "ms");
    }

    private JSONArray queryList(String itemCode){
        DemoService demoService= SpringContextUtil.getBeanByClass(DemoService.class);
        String func= "get"+ m_item.name();
        //System.out.println(func);

        try {
            Method method = demoService.getClass().getMethod(func, Class.forName("java.lang.String"));
            List<Map> purch = (List<Map>)method.invoke(demoService, itemCode);

            JSONArray jsonArray = JSONArray.parseArray(JSONObject.toJSONString(purch));
            for(int i=0; i<jsonArray.size(); i++){
                JSONObject item= jsonArray.getJSONObject(i);
                JSONObject obj= new JSONObject();

                obj.put("RecType", m_item.name);
                obj.put("RecCode", item.getString("reccode"));

                String s= item.getString("effectDateTime");
                String sdate= DateUtil.timeStamp2Date(s);

                obj.put("EffectDateTime", sdate);
                if(item.containsKey("smainquantity"))obj.put("SMainQuantity", item.getBigDecimal("smainquantity"));
                else obj.put("SMainQuantity", 0);

                if(item.containsKey("fmainquantity"))obj.put("FMainQuantity", item.getBigDecimal("fmainquantity"));
                else obj.put("FMainQuantity", 0);

                if(m_item.id==11 || m_item.id==12 || m_item.id==13)obj.put("tag", m_item.id);
                else obj.put("tag", 0);

                if(m_item.id>=3){
                    ItemList.m_data.add(obj);
                }

                if(m_item.id==7 || m_item.id==11 || m_item.id==12 || m_item.id==13){
                    String code= item.getString("workreccode");
                    if(code==null)code="SCDD-NULL";
                    else{
                        code= code.trim();
                        if(code=="" ||code.length()==0)code= "SCDD-EMPTY";
                    }

                    obj.put("RecCode" , code);
                    obj.put("RefCode", item.getString("reccode"));
                    ItemList.m_ref.add( obj );
                }
            }

            if(m_item.id==12){
                // System.out.println(jsonArray.size()+"-"+ jsonArray);
            }
            return jsonArray;
        }catch (Exception e){
            e.printStackTrace();
            JSONArray jsonArray = JSONArray.parseArray("{}");
            return jsonArray;
        }
    }

    public BigDecimal getTotal(String key){

        BigDecimal cnt= new BigDecimal(0);
        for(int i=0; i<this.jsonArray.size(); i++){
            JSONObject obj= this.jsonArray.getJSONObject(i);
            cnt= cnt.add(obj.getBigDecimal(key));
        }

        // if(m_item.id==12)System.out.println("take总数："+ jsonArray.size());
        return cnt;
    }

    public String getItems(String key){
        StringBuffer item= new StringBuffer();
        for(int i=0; i<this.jsonArray.size(); i++){
            JSONObject obj= this.jsonArray.getJSONObject(i);

            if(i>0)item.append(",");
            item.append(obj.getString(key));
        }

        return item.toString();
    }
}
