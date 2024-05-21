package com.njrsun.modules.inv.controller;

import com.njrsun.common.core.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author njrsun
 * @create 2021/6/30 10:38
 */
@Api(tags = "物料批次档")
@RestController
@RequestMapping("/inv/batch")
public class InvBatchController extends BaseController {


//    @Autowired
//    private IInvItemsService invItemsService;
//    @Autowired
//    private IInvSortService invSortService;
//
//    @PreAuthorize("@ss.hasPermi('inv:batch:list')")
//    @ApiOperation("物料批次查询列表")
//    @GetMapping("/list")
//    public TableDataInfo getList(@RequestParam Map<String,String> query){
//        startPage();
//       List<InvItems> list =  invItemsService.selectBatchBySortId(query);
//        return getDataTable(list);
//    }
//
//    @PreAuthorize("@ss.hasPermi('inv:batch:export')")
//    @ApiOperation("导出")
//    @GetMapping("/export")
//    public AjaxResult export(@RequestParam Map<String,String> query){
//        String sortId = query.get("sortId");
//        String name ;
//        if("".equals(sortId)){
//            name = "物料批次档";
//        }
//        else{
//            name = invSortService.getExcelName(Long.valueOf(sortId));
//        }
//        List<InvItems> invItems = invItemsService.selectBatchBySortId(query);
//        ExcelUtil<InvItems> excelUtil = new ExcelUtil<>(InvItems.class);
//        return excelUtil.exportExcel(invItems,name);
//
//    }


}
