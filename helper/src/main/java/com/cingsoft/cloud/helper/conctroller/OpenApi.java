package com.cingsoft.cloud.helper.conctroller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cingsoft.cloud.helper.dbase.DBUtils;
import com.cingsoft.cloud.helper.entity.Demo;
import com.cingsoft.cloud.helper.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Eastern unbeaten
 * @version 1.0
 * @date 2019/6/11 7:16
 * @mail chenshiyun2011@163.com
 */
@RestController
@RequestMapping("/")
public class OpenApi {

    @Autowired
    private DemoService demoService;

/*
    @GetMapping("/list")
    public ResponseEntity<Object> login() {
        return ResponseEntity.ok(demoService.list());
    }
*/

    @GetMapping("/list2")
    public ResponseEntity<Object> list2(@RequestParam(name = "code") String itemCode) {
        //String itemCode= "A3CPA001-010";
        long startTime = System.currentTimeMillis();
        System.out.println(itemCode);

        List<Map> itemId= demoService.getItemId(itemCode);
        System.out.println(itemId);
        long id= itemId.size()==1 ? (long)itemId.get(0).get("invitemid") :0;
        System.out.println(id);

        ItemQuantity item= new ItemQuantity(itemCode);
        JSONObject rt= item.runParser();

        long endTime = System.currentTimeMillis();
        System.out.println("程序总时间：" + (endTime - startTime) + "ms");

        return ResponseEntity.ok( rt );
    }

    @GetMapping("/list3")
    public ResponseEntity<Object> list3() {
        JSONObject rt= new JSONObject();
        rt.put("ref", ItemList.m_ref);
        return ResponseEntity.ok( rt );
    }

    @GetMapping("/list4")
    public ResponseEntity<Object> list4() {
        JSONObject rt= new JSONObject();
        rt.put("data", ItemList.m_data);
        return ResponseEntity.ok( rt );
    }

    @GetMapping("/add")
    public ResponseEntity<Object> add() {
        Demo d = new Demo();
        /*
        d.setId(UUID.randomUUID().toString());
        d.setName("name");
        d.setNickName("nickName");
        d.setPassword("password");
        */
        demoService.save(d);
        return ResponseEntity.ok(d);
    }

}
