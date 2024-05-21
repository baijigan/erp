package com.njrsun.modules.inv.controller;

import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.core.page.TableDataInfo;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.modules.inv.domain.InvItemsLimit;
import com.njrsun.modules.inv.service.IInvItemsLimitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author njrsun
 * @create 2021/7/22 10:18
 */
@Api(tags = "安全量")
@RestController
@RequestMapping("/inv/limit")
public class InvItemsLimitController extends BaseController {

    @Autowired
    private IInvItemsLimitService iInvItemsLimitService;

    @PreAuthorize("@ss.hasPermi('wm:limit:export')")
    @ApiOperation("导出")
    @GetMapping("/export")
    public AjaxResult export(@RequestParam Map<String,String> query){

       List<InvItemsLimit> ex =  iInvItemsLimitService.selectLimitList(query);
       ExcelUtil<InvItemsLimit> invItemsLimitExcelUtil = new ExcelUtil<>(InvItemsLimit.class);
        String lowerLimit = query.get("lowerLimit");
        String upperLimit = query.get("upperLimit");
        if(UserConstants.NOT_UNIQUE.equals(lowerLimit)){
          return   invItemsLimitExcelUtil.exportExcel(ex,"低于安全量");
        }else if(UserConstants.NOT_UNIQUE.equals(upperLimit)){
          return   invItemsLimitExcelUtil.exportExcel(ex,"高于安全量");
        }else{
            return invItemsLimitExcelUtil.exportExcel(ex,"安全库存");
        }

    }
    @PreAuthorize("@ss.hasPermi('wm:limit:list')")
    @ApiOperation("查看")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam Map<String,String> query){
        startPage();
        List<InvItemsLimit> ex =  iInvItemsLimitService.selectLimitList(query);
        return getDataTable(ex);
    }

    @PreAuthorize("@ss.hasPermi('wm:limit:edit')")
    @ApiOperation("编辑")
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody InvItemsLimit invItemsLimit){
        Integer integer = iInvItemsLimitService.updateLimit(invItemsLimit);
        if(integer > 0){
            Map<String, String> stringStringMap = new HashMap<>();
            stringStringMap.put("invCode",invItemsLimit.getCode());
            return AjaxResult.success(iInvItemsLimitService.selectLimitList(stringStringMap));
        }else{
            return AjaxResult.error("编辑失败");
        }
    }





}
