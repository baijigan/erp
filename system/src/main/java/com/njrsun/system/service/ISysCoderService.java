package com.njrsun.system.service;

import java.util.List;
import com.njrsun.system.domain.SysCoder;

/**
 * 单据编号Service接口
 * 
 * @author njrsun
 * @date 2021-09-26
 */
public interface ISysCoderService
{
    /**
     * 查询单据编号
     * 
     * @param workType 单据编号ID
     * @return 单据编号
     */
    public SysCoder selectSysCodeById(Long workType);

    /**
     * 查询单据编号列表
     * 
     * @param sysCoder 单据编号
     * @return 单据编号集合
     */
    public List<SysCoder> selectSysCodeList(SysCoder sysCoder);

    /**
     * 新增单据编号
     * 
     * @param sysCoder 单据编号
     * @return 结果
     */
    public int insertSysCode(SysCoder sysCoder);

    /**
     * 修改单据编号
     * 
     * @param sysCoder 单据编号
     * @return 结果
     */
    public int updateSysCode(SysCoder sysCoder);

    /**
     * 批量删除单据编号
     * 
     * @param codeId 需要删除的单据编号ID
     * @return 结果
     */
    public int deleteSysCodeByIds(Long[] codeId);

    /**
     * 删除单据编号信息
     * 
     * @param workType 单据编号ID
     * @return 结果
     */
    public int deleteSysCodeById(Long workType);


    /**
     * 生成唯一Id
     * @param workType
     * @param workTypeId
     * @return
     */

    public String generate(String workType,String workTypeId);
}
