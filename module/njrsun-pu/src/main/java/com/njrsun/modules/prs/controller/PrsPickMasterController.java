package com.njrsun.modules.prs.controller;

import com.njrsun.common.annotation.DataScope;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.core.page.TableDataInfo;
import com.njrsun.common.enums.BusinessType;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.modules.mp.contact.MpContacts;
import com.njrsun.modules.prs.domain.*;
import com.njrsun.modules.prs.domain.PrsOrderMaster;
import com.njrsun.modules.prs.domain.PrsPickExport;
import com.njrsun.modules.prs.domain.PrsPickMaster;
import com.njrsun.modules.prs.domain.PrsPickSalve;
import com.njrsun.modules.prs.service.IPrsPickMasterService;
import com.njrsun.modules.prs.service.impl.PrsOrderMasterServiceImpl;
import com.njrsun.api.mp.service.MpMbomPortalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 生产领料单Controller
 * 
 * @author njrsun
 * @date 2021-11-18
 */
@Api(tags = "生产领料单")
@RestController
@RequestMapping("/prs/pick")
public class PrsPickMasterController extends BaseController
{
    @Autowired
    private IPrsPickMasterService prsPickMasterService;

    @Autowired
    private MpMbomPortalService mpMbomPortalService;

    @Autowired
    private PrsOrderMasterServiceImpl prsOrderMasterService;

    /**
     * 查询生产领料单列表
     */
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:pick:list')")
    @GetMapping("/list")
    public TableDataInfo list(PrsPickMaster prsPickMaster)
    {
        startPage();
        List<PrsPickMaster> list = prsPickMasterService.selectPrsPickMasterList(prsPickMaster);
        return getDataTable(list);
    }

    /**
     * 导出生产领料单列表
     */
    @PreAuthorize("@ss.hasPermi('prs:pick:export')")
    @Log(title = "生产领料单", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PrsPickMaster prsPickMaster)
    {
        List<PrsPickMaster> list = prsPickMasterService.selectPrsPickMasterList(prsPickMaster);
        ExcelUtil<PrsPickMaster> util = new ExcelUtil<PrsPickMaster>(PrsPickMaster.class);
        return util.exportExcel(list, "pick");
    }

    /**
     * 获取生产领料单详细信息
     */
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:pick:entity:view')")
    @GetMapping
    public AjaxResult getInfo(PrsPickMaster prsPickMaster)
    {
        return AjaxResult.success(prsPickMasterService.selectPrsPickMasterById(prsPickMaster));
    }

    /**
     * 新增生产领料单
     */
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:pick:entity:add')")
    @Log(title = "生产领料单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PrsPickMaster prsPickMaster)
    {
        int i = prsPickMasterService.insertPrsPickMaster(prsPickMaster);
        if(i == 0){
            return AjaxResult.error();
        }else{
            return AjaxResult.success(prsPickMasterService.selectPrsPickMasterById(prsPickMaster));
        }

    }

    /**
     * 修改生产领料单
     */
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:pick:entity:edit')")
    @Log(title = "生产领料单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PrsPickMaster prsPickMaster)
    {
        int i = prsPickMasterService.updatePrsPickMaster(prsPickMaster);
        if(i == 0){
            return   AjaxResult.error();
        }else{
            return AjaxResult.success(prsPickMasterService.selectPrsPickMasterById(prsPickMaster));
        }
    }

    /**
     * 删除生产领料单
     */
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:pick:remove')")
    @Log(title = "生产领料单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<PrsPickMaster> list)
    {
        return toAjax(prsPickMasterService.deletePrsPickMasterByIds(list));
    }


    @ApiOperation("更新业务状态")
    @Log(title = "挂起完工订单", businessType = BusinessType.HANG)
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:pick:operate')")
    @PostMapping("/operate")
    public AjaxResult changeWorkStatus(@RequestBody PrsPickMaster prsPickMaster){
        prsPickMasterService.changeWorkStatus(prsPickMaster);
        return AjaxResult.success(prsPickMasterService.selectPrsPickMasterById(prsPickMaster));
    }


    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:pick:detail')")
    @ApiOperation("获取明细")
    @GetMapping("/getDetail")
    public TableDataInfo getDetail(PrsPickExport prsPickExport){
        startPage();
        List<PrsPickExport> detail = prsPickMasterService.getDetail(prsPickExport);
        return getDataTable(detail);
    }


    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:pick:export')")
    @ApiOperation("明细导出")
    @GetMapping("/detailExport")
    public AjaxResult detailExport(PrsPickExport prsPickExport){
        List<PrsPickExport> detail = prsPickMasterService.getDetail(prsPickExport);
        ExcelUtil<PrsPickExport> util = new ExcelUtil<>(PrsPickExport.class);
        return util.exportExcel(detail, "生产完工单明细");
    }


    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:pick:import')")
    @ApiOperation("选择生产订单")
    @GetMapping("/lead")
    public TableDataInfo lead(PrsOrderMaster prsOrderMaster){
        startPage();
        List<Map<String,String>> list = prsOrderMasterService.lead(prsOrderMaster);
        return getDataTable(list);
    }

//    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:pick:import')")
    @ApiOperation("引入生产物料")
    @GetMapping("/leadInto")
    public TableDataInfo leadInto(@RequestParam Map<String,Object> map1){
        startPage();
        String invSortRoot = map1.get("invSortRoot").toString();
        String[] split = invSortRoot.split(",");
        map1.put("invSortRoot",split);

        PrsOrderMaster prsOrderMaster = new PrsOrderMaster();
        prsOrderMaster.setPrsCode(map1.get("prsOrderCode").toString());
        PrsOrderMaster prsOrderMaster1 = prsOrderMasterService.selectPrsOrderMasterById(prsOrderMaster);
        List<PrsPickSalve> list =   prsPickMasterService.selectQuantity(prsOrderMaster1.getPrsCode());
        map1.put("mpOrderCode",prsOrderMaster1.getMpOrderCode());
        List<Map<String, Object>> maps = mpMbomPortalService.selectMpMbomPortal(map1);
        Map<String, Object> maps1 = mpMbomPortalService.leadInto(map1);
        if(StringUtils.isNull(maps1)){
            return  getDataTable(new ArrayList<>());
        }
        BigDecimal bigDecimal = (BigDecimal) maps1.get("inv_quantity");
        BigDecimal invQuantity = new BigDecimal(map1.get("invQuantity").toString()) ;
        for (Map<String, Object> next : maps) {
            BigDecimal surplus1 = (BigDecimal) next.get("quantity");
            BigDecimal surplus = surplus1.multiply(invQuantity).divide(bigDecimal, 4, RoundingMode.HALF_UP);
            for (PrsPickSalve prsPickSalve : list) {
                if (prsPickSalve.getWoUniqueId().equals(next.get("unique_id"))) {
                    BigDecimal quantity = prsPickSalve.getQuantity();
                    surplus = surplus.subtract(quantity);
                }
            }
            next.put("surplus", surplus);
            next.put("work_type", MpContacts.MBOM);
            next.put("invoice_type", MpContacts.INVOICE);
        }
        Iterator<Map<String, Object>> iterator = maps.iterator();
        while(iterator.hasNext()){
            Map<String, Object> next = iterator.next();
            BigDecimal surplus = (BigDecimal) next.get("surplus");
            if(surplus.compareTo(BigDecimal.ZERO) < 1){
                iterator.remove();
            }
        }
        //return maps;
        return getDataTable(maps);
    }


    @PreAuthorize("@ss.hasPermi('prs:pick:permit')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @Log(title = "审核订单", businessType = BusinessType.PERMIT)
    @ApiOperation("审核")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody List<PrsPickMaster> list){
        PrsPickMaster prsPickMaster = prsPickMasterService.batchCheck(list);
        return AjaxResult.success(prsPickMaster);
    }


    @PreAuthorize("@ss.hasPermi('prs:pick:revoke')")
    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @Log(title = "反审核订单", businessType = BusinessType.REVOKE)
    @ApiOperation("反审核")
    @PostMapping("/antiCheck")
    public AjaxResult antiCheck(@RequestBody  List<PrsPickMaster> list){
        PrsPickMaster prsPickMaster = prsPickMasterService.batchAntiCheck(list);
        return AjaxResult.success(prsPickMaster);
    }

    @DataScope(roleAlias = "prs_oper",workAlias = "prs_pick_type")
    @PreAuthorize("@ss.hasPermi('prs:pick:next')")
    @ApiOperation("下一条")
    @GetMapping("/nextOrLast")
    public AjaxResult nextOrLast(PrsPickMaster prsPickMaster){
        List<String> codes = prsPickMasterService.selectPrsCode(prsPickMaster);
        int i = 0;
        String prsCode = prsPickMaster.getPrsCode();
        Boolean type = prsPickMaster.getType();
        for (int i1 = 0; i1 < codes.size(); i1++) {
            if(codes.get(i1).equals(prsCode)){
                i = i1;
            }
        }
        if( (i == 0 && type) || ((i ==codes.size()-1) && !type)){
            throw  new CommonException("已经是最后一条单据了");
        }else if(type){
            PrsPickMaster prsOrderMaster1 = new  PrsPickMaster();
            prsOrderMaster1.setPrsCode(codes.get(i-1));
            return  AjaxResult.success( prsPickMasterService.selectPrsPickMasterById(prsOrderMaster1));
        }
        else{
            PrsPickMaster prsOrderMaster1 = new PrsPickMaster();
            prsOrderMaster1.setPrsCode(codes.get(i+1));
            return AjaxResult.success(prsPickMasterService.selectPrsPickMasterById(prsOrderMaster1));
        }
    }


    @PreAuthorize("@ss.hasPermi('prs:pick:chain')")
    @ApiOperation("关联单据查询")
    @GetMapping("/chain")
    public TableDataInfo linkDetail(PrsPickMaster prsProductExport){
        List<Map<String,String>> list =  prsPickMasterService.chain(prsProductExport);
        return getDataTable(list);
    }

}
