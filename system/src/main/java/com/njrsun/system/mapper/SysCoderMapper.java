package com.njrsun.system.mapper;

import com.njrsun.system.domain.SysCoder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单据编号Mapper接口
 * 
 * @author njrsun
 * @date 2021-09-26
 */
public interface SysCoderMapper
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
     * 删除单据编号
     * 
     * @param workType 单据编号ID
     * @return 结果
     */
    public int deleteSysCodeById(Long codeId);

    /**
     * 批量删除单据编号
     * 
     * @param workTypes 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysCodeByIds(Long[] codeId);

    void addIncNumber(@Param("id") Long workType);

    SysCoder selectSysCodeByWorkType(@Param("workType") String workType, @Param("workTypeId") String workTypeId,@Param("typeShare") String typeShare);
}
