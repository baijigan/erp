package com.cingsoft.cloud.helper.conctroller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.cingsoft.cloud.helper.HelperApplication;
import com.cingsoft.cloud.helper.dbase.DBUtils;
import com.cingsoft.cloud.helper.service.DemoService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

public class ItemQuantity {
    public String m_itemCode= null;
    public BigDecimal m_initial= null;
    public BigDecimal m_balance= null;

    ItemQuantity(String itemCode){
        this.m_itemCode= itemCode;
        this.m_initial= getInitial(itemCode);
        this.m_balance= getBalance(itemCode);
    }

    private BigDecimal getBalance(String itemCode){
        ItemList balance= new ItemList(ItemEnum.Balance, this.m_itemCode);
        BigDecimal balanceSum= balance.getTotal("mainquantity");
        return balanceSum;
        // DBUtils.executeInsert("insert into gngz(invCode, papTitle, qtyBal) values('"+ itemCode+ "', '现存量', "+  num+ ")");
    }

    private BigDecimal getInitial(String itemCode){
        ItemList initial= new ItemList(ItemEnum.Initial, this.m_itemCode);
        BigDecimal initialSum= initial.getTotal("beginmainquantity");
        return initialSum;

        // DBUtils.executeInsert("insert into gngz(invCode, papTitle, qtyBal) values('"+ itemCode+ "', '现存量', "+  num+ ")");
    }

    public JSONObject runParser(){
        ItemList.m_ref.clear();
        ItemList.m_data.clear();

        ItemList purchaseIn= new ItemList(ItemEnum.PurchaseIn, this.m_itemCode);
        BigDecimal purchaseInSum= purchaseIn.getTotal("smainquantity");

        ItemList machIn= new ItemList(ItemEnum.MachIn, this.m_itemCode);
        BigDecimal machInSum= machIn.getTotal("smainquantity");

        ItemList coopIn= new ItemList(ItemEnum.CoopIn, this.m_itemCode);
        BigDecimal coopInSum= coopIn.getTotal("smainquantity");

        ItemList saleOut= new ItemList(ItemEnum.SaleOut, this.m_itemCode);
        BigDecimal saleOutSum= saleOut.getTotal("fmainquantity");

        ItemList manuOut= new ItemList(ItemEnum.ManuOut, this.m_itemCode);
        BigDecimal manuOutSum= manuOut.getTotal("fmainquantity");

        ItemList coopOut= new ItemList(ItemEnum.CoopOut, this.m_itemCode);
        BigDecimal coopOutSum= coopOut.getTotal("fmainquantity");

        ItemList otherIn= new ItemList(ItemEnum.OtherIn, this.m_itemCode);
        BigDecimal otherInSum= otherIn.getTotal("smainquantity");

        ItemList otherOut= new ItemList(ItemEnum.OtherOut, this.m_itemCode);
        BigDecimal otherOutSum= otherOut.getTotal("fmainquantity");

        System.out.println(purchaseInSum+":"+machInSum+":"+coopInSum+":"+otherInSum);
        BigDecimal jf= purchaseInSum.add(machInSum).add(coopInSum).add(otherInSum);
        BigDecimal df= saleOutSum.add(manuOutSum).add(coopOutSum).add(otherOutSum);

        ItemList deductOut= new ItemList(ItemEnum.DeductOut, this.m_itemCode);
        BigDecimal deductOutSum= deductOut.getTotal("fmainquantity");

        ItemList tackOut= new ItemList(ItemEnum.TackOut, this.m_itemCode);
        BigDecimal tackOutSum= tackOut.getTotal("fmainquantity");

        ItemList ArtificialOut= new ItemList(ItemEnum.ArtificialOut, this.m_itemCode);
        BigDecimal ArtfOutSum= ArtificialOut.getTotal("fmainquantity");

        System.out.println("");

        String desc= "物料编码："+ m_itemCode+ " 截止时间："+ DateUtil.getNow()+ "\r\n";
        desc+= "期初："+ m_initial +" 借方:  "+ jf+ " 贷方："+ df+ " 现存量："+ m_balance+ "\r\n";
        BigDecimal calcBalance= m_initial.add(jf).subtract(df);
        desc+= "账面量："+ calcBalance+ " 对账结果："+ calcBalance.subtract(m_balance)+ "\r\n";
        desc+= "\r\n";

        desc+= "生产出库:  "+ manuOutSum+ " 入库倒冲："+ deductOutSum+ " 按单领用："+ tackOutSum+ " 其它出库："+ ArtfOutSum + "\r\n";
        BigDecimal calcBalance2= manuOutSum.subtract(deductOutSum).subtract(tackOutSum).subtract(ArtfOutSum);
        desc+= "对账结果："+ calcBalance2+ "\r\n";
        desc+= "\r\n";

        JSONObject jdesc= new JSONObject( );
        jdesc.put("物料编码", m_itemCode);
        jdesc.put("截止时间", DateUtil.getNow());
        jdesc.put("期初", m_initial );
        jdesc.put("借方", jf);
        jdesc.put("贷方", df);
        jdesc.put("现存量", m_balance);
        jdesc.put("账面量", calcBalance);
        jdesc.put("对账结果", calcBalance.subtract(m_balance));

        jdesc.put("生产出库", manuOutSum);
        jdesc.put("入库倒冲", deductOutSum);
        jdesc.put("按单领用", tackOutSum);
        jdesc.put("手工出库", ArtfOutSum);
        jdesc.put("冲账结果", calcBalance2);

        String data= ItemQuantity.jsonArraySort(ItemList.m_data);
        ItemList.m_data= JSONArray.parseArray(data);

        BigDecimal prv= m_initial;
        for(int i=0; i<ItemList.m_data.size(); i++){
            JSONObject item= ItemList.m_data.getJSONObject(i);
            item.put("autoId", i+1);
            int tag= item.getIntValue("tag");
            if(tag==0){
                prv= prv.add(item.getBigDecimal("SMainQuantity"));
                prv= prv.subtract(item.getBigDecimal("FMainQuantity"));
                item.put("Balance", prv);
            }
        }

        String ref= ItemQuantity.jsonArraySort(ItemList.m_ref, "RecCode", false);
        ItemList.m_ref= JSONArray.parseArray(ref);
        for(int i=0; i<ItemList.m_ref.size(); i++){
            JSONObject item= ItemList.m_ref.getJSONObject(i);
            item.put("autoId", i+1);
        }

        //System.out.println("ref-"+ ItemList.m_ref);

        //System.out.println( ItemList.m_data );
        desc+= "过账余额："+prv+ "\r\n";
        jdesc.put("过账余额", prv);

        System.out.println(desc);

        JSONObject rt= new JSONObject();
        rt.put("desc", desc);
        rt.put("jdesc", jdesc);
        //rt.put("data", ItemList.m_data);
        //rt.put("ref", ItemList.m_ref);
        return rt;
    }

    public static String jsonArraySort(JSONArray jsonArr) {
        //JSONArray jsonArr = JSON.parseArray(jsonArrStr);
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }

        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "EffectDateTime";
            String string1;
            String string2;
            @Override
            public int compare(JSONObject a, JSONObject b) {
                try {
                    string1= a.getString(KEY_NAME).replaceAll("-", "");
                    string2= b.getString(KEY_NAME).replaceAll("-", "");
                } catch (JSONException e) {
                    // 处理异常
                }
                //这里是按照时间逆序排列,不加负号为正序排列
                return string1.compareTo(string2);

                //这里是按照时间逆序排列,不加负号为正序排列
                //return -string1.compareTo(string2);
            }
        });
        for (int i = 0; i < jsonArr.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }
        return sortedJsonArray.toString();
    }

    public static String jsonArraySort(JSONArray jsonArr, String sortKey, boolean is_desc) {
        //JSONArray jsonArr = JSON.parseArray(jsonArrStr);
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }

        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "RecCode";
            String string1;
            String string2;
            @Override
            public int compare(JSONObject a, JSONObject b) {
                try {
                    string1= a.getString(KEY_NAME);
                    string2= b.getString(KEY_NAME);
                } catch (JSONException e) {
                    // 处理异常
                }

                //这里是按照时间逆序排列,不加负号为正序排列
                return string1.compareTo(string2);

                //这里是按照时间逆序排列,不加负号为正序排列
                //return -string1.compareTo(string2);
            }
        });
        for (int i = 0; i < jsonArr.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }
        return sortedJsonArray.toString();
    }

}
