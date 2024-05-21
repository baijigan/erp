package com.cingsoft.cloud.analyse.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cingsoft.cloud.analyse.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cingsoft.cloud.analyse.core.AjaxResult;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/dashboard/template")
public class TemplateController extends BaseController {

    @Autowired
    private TemplateService templateService;

    @GetMapping(value = "/order/list")
    public AjaxResult getOrderList(HttpServletResponse response)
    {
        allowCrossDomain(response);
        JSONArray list= templateService.getOrderList();

        String[][] data= new String[list.size()][3];
        for(int i=0; i<list.size(); i++){
            JSONObject obj= list.getJSONObject(i);
            data[i][0]= obj.getString("InvItemName");

            int quantity= obj.getIntValue("MainQuantity");
            if(quantity>50)data[i][1]= "<span  class='colorGrass'>↑"+ obj.getString("MainQuantity")+ "</span>";
            else data[i][1]= "<span  class='colorRed'>↓"+ obj.getString("MainQuantity")+ "</span>";
        }

        JSONArray arr= templateService.getData(ItemEnum.TotalSalesYear.name());
        //System.out.println("arr:"+arr);

        arr= templateService.getData(ItemEnum.TotalSalesPrv.name());
        //System.out.println("arr:"+arr);

        arr= templateService.getData(ItemEnum.TotalDebtYear.name());
        //System.out.println("arr:"+arr);

        arr= templateService.getData(ItemEnum.TotalDebtPrv.name());
        //System.out.println("arr:"+arr);

        AjaxResult ajax = AjaxResult.success( (Object)data );
        return ajax;
    }

    @GetMapping(value = "/order/total")
    public AjaxResult getOrderTotal(HttpServletResponse response)
    {
        allowCrossDomain(response);

        JSONArray totalItem= new JSONArray();
        JSONArray arr= templateService.getData(ItemEnum.TotalSalesYear.name());
        Integer s1= arr.getJSONObject(0).getIntValue("sumAmount")/10000;
        JSONObject item= TemplateController.newItem("本年销售总额", s1, "万元 ");
        totalItem.add(item);

        arr= templateService.getData(ItemEnum.TotalSalesPrv.name());
        Integer s2= arr.getJSONObject(0).getIntValue("sumAmount")/10000;
        item= TemplateController.newItem("去年同期销售总额", s2, "万元 ");
        totalItem.add(item);

        Integer k= (s1-s2)*100/(s2+s1)/2;
        // System.out.println(k);
        item= TemplateController.newItem("销售指数", k,"%   ");
        totalItem.add(item);

        arr= templateService.getData(ItemEnum.TotalDebtYear.name());
        Integer d1= arr.getJSONObject(0).getIntValue("sumAmount")/10000;
        item= TemplateController.newItem("本年应收款总额", d1, "万元 ");
        totalItem.add(item);

        arr= templateService.getData(ItemEnum.TotalDebtPrv.name());
        Integer d2= arr.getJSONObject(0).getIntValue("sumAmount")/10000;
        item= TemplateController.newItem("去年同期应收款总额", d2, "万元 ");
        totalItem.add(item);

        k= (d1-d2)*100/(d2+d1)/2;
        item= TemplateController.newItem("应收款指数", k, "%   ");
        totalItem.add(item);

        AjaxResult ajax = AjaxResult.success( (Object)totalItem );
        return ajax;
    }

    @GetMapping(value = "/sales/person/total")
    public AjaxResult getSalesPersonTotal(HttpServletResponse response)
    {
        allowCrossDomain(response);

        JSONArray arr= templateService.getData(ItemEnum.TotalSalesPerson.name());
        JSONArray data= new JSONArray();

        for(int i=0; i<arr.size(); i++){
            JSONObject obj= new JSONObject();
            obj.put("name", arr.getJSONObject(i).getString("name"));
            obj.put("value", arr.getJSONObject(i).getIntValue("sumAmount"));
            data.add(obj);
        }

        JSONObject rt= new JSONObject();
        rt.put("data", data);
        AjaxResult ajax = AjaxResult.success( (Object)rt );
        return ajax;
    }

    @GetMapping(value = "/sales/area/total")
    public AjaxResult getSalesAreaTotal(HttpServletResponse response)
    {
        allowCrossDomain(response);

        JSONArray arr= templateService.getData(ItemEnum.TotalSalesArea.name());
        JSONArray data= new JSONArray();

        for(int i=2; i<arr.size(); i++){
            JSONObject obj= new JSONObject();
            obj.put("name", arr.getJSONObject(i).getString("name"));
            obj.put("value", arr.getJSONObject(i).getIntValue("sumAmount"));
            data.add(obj);
        }

        JSONObject rt= new JSONObject();
        rt.put("data", data);
        AjaxResult ajax = AjaxResult.success( (Object)rt );
        return ajax;
    }

    public static JSONObject newItem(String title, Integer value, String unit) {
        JSONObject item= new JSONObject();
        item.put("title", title);

        JSONObject number= new JSONObject();
        int[] v= new int[1];
        v[0]= value;

        number.put("number", v);
        number.put("toFixed", 0);
        number.put("textAlign", "right");
        number.put("content", "{nt} "+ unit);

        JSONObject style= new JSONObject();
        style.put("fontSize", 12);
        item.put("style", style);
        item.put("number", number);
        return item;
    }

    public static void allowCrossDomain(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET,POST");
        response.addHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
    }
}
