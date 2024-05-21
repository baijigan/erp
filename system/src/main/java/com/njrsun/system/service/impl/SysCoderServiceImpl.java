package com.njrsun.system.service.impl;

import java.util.List;

import com.njrsun.common.constant.Constants;
import com.njrsun.common.core.domain.entity.SysDictType;
import com.njrsun.common.exception.CommonException;
import com.njrsun.common.utils.DateUtils;
import com.njrsun.common.utils.SecurityUtils;
import com.njrsun.common.utils.StringUtils;
import com.njrsun.system.service.ISysDictDataService;
import com.njrsun.system.service.ISysDictTypeService;
import com.njrsun.system.domain.SysCoder;
import com.njrsun.system.mapper.SysCoderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.njrsun.system.service.ISysCoderService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 单据编号Service业务层处理
 * 
 * @author njrsun
 * @date 2021-09-26
 */
@Primary
@Service
public class SysCoderServiceImpl implements ISysCoderService
{
    @Autowired
    private SysCoderMapper sysCoderMapper;
    @Autowired
    private ISysDictDataService iSysDictDataService;
    @Autowired
    private ISysDictTypeService iSysDictTypeService;

    /**
     * 查询单据编号
     * 
     * @param codeId 单据编号ID
     * @return 单据编号
     */
    @Override
    public SysCoder selectSysCodeById(Long codeId)
    {
        return sysCoderMapper.selectSysCodeById(codeId);
    }

    /**
     * 查询单据编号列表
     * 
     * @param sysCoder 单据编号
     * @return 单据编号
     */
    @Override
    public List<SysCoder> selectSysCodeList(SysCoder sysCoder)
    {
        List<SysCoder> sysCoders = sysCoderMapper.selectSysCodeList(sysCoder);
        for (SysCoder code : sysCoders) {
            String s = iSysDictDataService.selectDictLabel(code.getWorkType(), code.getWorkTypeId());
            code.setWorkTypeName(s);
            SysDictType sysDictType = iSysDictTypeService.selectDictTypeByType(code.getWorkType());
            if(StringUtils.isNotNull(sysDictType)){
                code.setRemark(sysDictType.getRemark());
            }
        }
        return sysCoders;
    }

    /**
     * 新增单据编号
     * 
     * @param sysCoder 单据编号
     * @return 结果
     */
    @Override
    public int insertSysCode(SysCoder sysCoder)
    {
        Boolean check = check(sysCoder);
        if(!check){
            throw  new CommonException("重复添加");
        }
        sysCoder.setCreateTime(DateUtils.getNowDate());
        sysCoder.setCreateBy(SecurityUtils.getUsername());
        return sysCoderMapper.insertSysCode(sysCoder);
    }

    private Boolean check(SysCoder sysCoder) {
        List<SysCoder> sysCoders = sysCoderMapper.selectSysCodeList(sysCoder);
        if(sysCoders.size() != 0){
            SysCoder sysCoder1 = sysCoders.get(0);
            if(StringUtils.isNotNull(sysCoder.getCoderId())){
                return sysCoder1.getCoderId().equals(sysCoder.getCoderId());
            }
            return false;
        }

        return true;
    }

    /**
     * 修改单据编号
     * 
     * @param sysCoder 单据编号
     * @return 结果
     */
    @Override
    public int updateSysCode(SysCoder sysCoder)
    {

        Boolean check = check(sysCoder);
        if(!check){
        throw  new CommonException("已存在");
    }
        sysCoder.setUpdateTime(DateUtils.getNowDate());
        sysCoder.setUpdateBy(SecurityUtils.getUsername());
        return sysCoderMapper.updateSysCode(sysCoder);
    }

    /**
     * 批量删除单据编号
     * 
     * @param workTypes 需要删除的单据编号ID
     * @return 结果
     */
    @Override
    public int deleteSysCodeByIds(Long[] workTypes)
    {
        return sysCoderMapper.deleteSysCodeByIds(workTypes);
    }

    /**
     * 删除单据编号信息
     * 
     * @param workType 单据编号ID
     * @return 结果
     */
    @Override
    public int deleteSysCodeById(Long workType)
    {
        return sysCoderMapper.deleteSysCodeById(workType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generate(String workType, String workTypeId) {
        SysCoder sysCoder= sysCoderMapper.selectSysCodeByWorkType(workType,null,"1");
        if(StringUtils.isNotNull(sysCoder)){
            Long incLen = sysCoder.getIncLen();
            String exactDate = sysCoder.getExactDate();
            Long incNumber = sysCoder.getIncNumber();
            String prefix = sysCoder.getPrefix();
            String s = "%0"+incLen+"d";
            String format = String.format(s, incNumber);
            sysCoderMapper.addIncNumber(sysCoder.getCoderId());
            return "1".equals(exactDate)? prefix + DateUtils.dateTime() + format : prefix + format;
            }
        SysCoder sysCoder1= sysCoderMapper.selectSysCodeByWorkType(workType,workTypeId,null);
        if(StringUtils.isNull(sysCoder1)){
            return Constants.snowflake.nextIdStr();
        }
        /**  自增宽度 **/
        Long incLen = sysCoder1.getIncLen();
        /**  日期 **/
        String exactDate = sysCoder1.getExactDate();
        /**  自增值 **/
        Long incNumber = sysCoder1.getIncNumber();
        /**  前缀 **/
        String prefix = sysCoder1.getPrefix();
        String s = "%0"+incLen+"d";
        String format = String.format(s, incNumber);
        sysCoderMapper.addIncNumber(sysCoder1.getCoderId());
         return "1".equals(exactDate)? prefix + DateUtils.dateTime() + format : prefix + format;

    }
}
