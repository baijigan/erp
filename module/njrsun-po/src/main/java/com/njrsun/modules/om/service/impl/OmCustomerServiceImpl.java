package com.njrsun.modules.om.service.impl;

import java.util.List;
import java.util.Map;

import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.modules.om.domain.OmCustomer;
import com.njrsun.modules.om.mapper.OmCustomerMapper;
import com.njrsun.modules.om.service.IOmCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 客户列Service业务层处理
 * 
 * @author njrsun
 * @date 2021-06-24
 */
@Service
public class OmCustomerServiceImpl implements IOmCustomerService
{
    @Autowired
    private OmCustomerMapper omCustomerMapper;

    /**
     * 查询客户列
     * 
     * @param uniqueId 客户列ID
     * @return 客户列
     */
    @Override
    public OmCustomer selectOmCustomerById(String shortName)
    {
        return omCustomerMapper.selectOmCustomerById(shortName);
    }

    /**
     * 查询客户列列表
     * 
     * @param omCustomer 客户列
     * @return 客户列
     */
    @Override
    public List<OmCustomer> selectOmCustomerList(OmCustomer omCustomer)
    {
        return omCustomerMapper.selectOmCustomerList(omCustomer);
    }

    /**
     * 新增客户列
     * 
     * @param omCustomer 客户列
     * @return 结果
     */
    @Override
    public Integer insertOmCustomer(OmCustomer omCustomer)
    {
      omCustomer.setName(omCustomer.getName().replaceAll(" ",""));
        omCustomer.setCreateBy(SecurityUtils.getUsername());
       return      omCustomerMapper.insertOmCustomer(omCustomer);

    }


    /**
     * 修改客户列
     * 
     * @param omCustomer 客户列
     * @return 结果
     */
    @Override
    public Integer updateOmCustomer(OmCustomer omCustomer)
    {

            omCustomer.setUpdateBy(SecurityUtils.getUsername());
           return  omCustomerMapper.updateOmCustomer(omCustomer);

    }

    /**
     * 批量删除客户列
     * 
     * @param uniqueIds 需要删除的客户列ID
     * @return 结果
     */
    @Override
    public int deleteOmCustomerByIds(Long[] uniqueIds)
    {
        return omCustomerMapper.deleteOmCustomerByIds(uniqueIds);
    }

    /**
     * 删除客户列信息
     * 
     * @param shortName 客户列ID
     * @return 结果
     */
    @Override
    public int deleteOmCustomerById(String shortName)
    {
        Integer i =  omCustomerMapper.selectMinVersion(shortName);
        try{
            if(StringUtils.isNull(i)){
                return omCustomerMapper.deleteOmCustomerById(shortName,-1);
            }
            return omCustomerMapper.deleteOmCustomerById(shortName,i-1);
        }catch (Exception e){
            throw  new CommonException("删除失败");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importData(List<OmCustomer> omCustomerList, boolean updateSupport) {
        if(omCustomerList.size() == 0){
            throw  new CommonException("导入列表为空");
        }
        StringBuilder failMsg = new StringBuilder();
        Integer success = 0;
        Integer fail = 0;
        for (OmCustomer omCustomer : omCustomerList) {
            if(StringUtils.isNotNull(omCustomer.getName()) &&  (!"".equals(omCustomer.getName()))){
                try{
                    if(updateSupport){
                        if("".equals(omCustomer.getStatus())){
                            omCustomer.setStatus("0");
                        }
                        Integer i =  updateImportOmCustomer(omCustomer);
                        if(i==0){
                            insertOmCustomer(omCustomer);
                            success++;
                        }
                        else{
                            success++;
                        }
                    }
                    else {
                        Integer ex = isExist(omCustomer.getName().replaceAll(" ",""), omCustomer.getShortName().replaceAll(" ",""));
                        if(ex == 0){
                            if("".equals(omCustomer.getStatus())){
                                omCustomer.setStatus("0");
                            }
                            insertOmCustomer(omCustomer);
                            success++;
                        }
                        else{
                            fail++;
                            failMsg.append("</br>" + fail+"、" + omCustomer.getName() + " " +omCustomer.getShortName() + "已存在");
                        }
                    }
                }catch (Exception e){
                    fail++;
                    failMsg.append("<br/> 导入失败-" + e.getMessage());
                }
            }
        }
        if(fail > 0){
            failMsg.insert(0,"很抱歉，导入失败！ 共 " +fail + " 条数据格式不正确,错误如下: ");
            throw  new CommonException(failMsg.toString());
        }else{
            return "恭喜您，数据全部导入成功,共"+success+"条!";
        }

    }

    @Override
    public int dumpOmCustomer() {

       return  omCustomerMapper.dump();

    }

    @Override
    public List<Map<String, String>> selectCustomerMapList() {
        return omCustomerMapper.selectCustomerMapList();
    }

    private Integer updateImportOmCustomer(OmCustomer omCustomer) {
        return omCustomerMapper.updateImportOmCustomer(omCustomer);
    }

    public Integer isUpdate(OmCustomer omCustomer) {
        return  omCustomerMapper.isUpdate(omCustomer);

    }

    public Integer isExist(String name,String shortName) {

        return omCustomerMapper.isExist(name,shortName);

    }
}
