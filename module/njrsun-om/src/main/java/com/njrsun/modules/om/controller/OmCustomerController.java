package com.njrsun.modules.om.controller;

import java.util.List;

import com.njrsun.modules.om.domain.OmCustomer;
import com.njrsun.modules.om.service.IOmCustomerService;
import com.njrsun.modules.om.service.impl.OmCustomerServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.njrsun.common.annotation.Log;
import com.njrsun.common.core.controller.BaseController;
import com.njrsun.common.core.domain.AjaxResult;
import com.njrsun.common.enums.BusinessType;

import com.njrsun.common.utils.poi.ExcelUtil;
import com.njrsun.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 客户列Controller
 * 
 * @author njrsun
 * @date 2021-06-24
 */
@RestController
@RequestMapping("/om/customer")
@Api(value = "客户档案",tags = "客户档案")
public class OmCustomerController extends BaseController
{
    @Autowired
    private IOmCustomerService omCustomerService;

    @Autowired
    private OmCustomerServiceImpl customerService;



    /**
     * 查询客户列表
     */
    @PreAuthorize("@ss.hasPermi('om:customer:list')")
    @ApiOperation("查询客户列表")
    @GetMapping("/list")
    public TableDataInfo list(OmCustomer omCustomer)
    {
        startPage();
        List<OmCustomer> list = omCustomerService.selectOmCustomerList(omCustomer);
        return getDataTable(list);
    }


    /**
     * 导出客户列表
     */
    @PreAuthorize("@ss.hasPermi('om:customer:export')")
    @ApiOperation("导出客户列表")
    @Log(title = "客户列", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(OmCustomer omCustomer)
    {
        List<OmCustomer> list = omCustomerService.selectOmCustomerList(omCustomer);
        ExcelUtil<OmCustomer> util = new ExcelUtil<OmCustomer>(OmCustomer.class);
        return util.exportExcel(list, "客户信息");
    }


    @PreAuthorize("@ss.hasPermi('om:customer:import')")
    @ApiOperation("导入客户列表")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<OmCustomer> util = new ExcelUtil<>(OmCustomer.class);
        List<OmCustomer> omCustomerList = util.importExcel(file.getInputStream());
        String message = omCustomerService.importData(omCustomerList, updateSupport);
        return AjaxResult.success(message);
    }


    /**
     * 获取客户详细信息
     */
    @PreAuthorize("@ss.hasPermi('om:customer:view')")
    @ApiOperation("获取客户详细信息")
    @GetMapping(value = "/{shortName}")
    public AjaxResult getInfo(@PathVariable("shortName") String shortName)
    {
        return AjaxResult.success(omCustomerService.selectOmCustomerById(shortName));
    }


    /**
     * 新增客户
     */
    @PreAuthorize("@ss.hasPermi('om:customer:add')")
    @ApiOperation("新增客户")
    @Log(title = "客户列", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OmCustomer omCustomer)
    {
        Integer exist = customerService.isExist(omCustomer.getName(),omCustomer.getShortName());
        if(exist > 0){
            return AjaxResult.error(omCustomer.getShortName() + " " + omCustomer.getName() + "已存在!");
        }
        return toAjax(omCustomerService.insertOmCustomer(omCustomer));
    }


    /**
     * 修改客户
     */
    @PreAuthorize("@ss.hasPermi('om:customer:edit')")
    @ApiOperation("修改客户")
    @Log(title = "客户列", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OmCustomer omCustomer)
    {
        Integer update = customerService.isUpdate(omCustomer);
        if(update > 0){
            return AjaxResult.error(omCustomer.getShortName() + " " + omCustomer.getName() + "已存在!");
        }
        return toAjax(omCustomerService.updateOmCustomer(omCustomer));
    }


    @PreAuthorize("@ss.hasPermi('om:customer:dump')")
    @ApiOperation("清空")
    @GetMapping("/dump")
    public AjaxResult dump(){
        return toAjax(omCustomerService.dumpOmCustomer());
    }



    /**
     * 删除客户
     */
    @PreAuthorize("@ss.hasPermi('om:customer:remove')")
    @ApiOperation("删除客户")
    @Log(title = "客户列", businessType = BusinessType.DELETE)
	@DeleteMapping("/{Name}")
    public AjaxResult remove(@PathVariable String Name)
    {
        return toAjax(omCustomerService.deleteOmCustomerById(Name));
    }
}
