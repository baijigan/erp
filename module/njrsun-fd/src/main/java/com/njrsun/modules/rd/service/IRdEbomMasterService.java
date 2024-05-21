package com.njrsun.modules.rd.service;

import com.njrsun.modules.rd.domain.RdEbomExport;
import com.njrsun.modules.rd.domain.RdEbomMaster;
import com.njrsun.modules.rd.domain.RdEbomSalve;
import com.njrsun.modules.rd.domain.RdTree;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 配方Service接口
 * 
 * @author njrsun
 * @date 2021-11-23
 */
public interface IRdEbomMasterService
{
    /**
     * 查询配方
     * 
     * @param uniqueId 配方ID
     * @return 配方
     */
    public RdEbomMaster selectRdRecipeMasterById(RdEbomMaster rdEbomMaster);

    /**
     * 查询配方列表
     * 
     * @param rdEbomMaster 配方
     * @return 配方集合
     */
    public List<RdEbomMaster> selectRdRecipeMasterList(RdEbomMaster rdEbomMaster);

    /**
     * 新增配方
     * 
     * @param rdEbomMaster 配方
     * @return 结果
     */
    public int insertRdRecipeMaster(RdEbomMaster rdEbomMaster);

    /**
     * 修改配方
     * 
     * @param rdEbomMaster 配方
     * @return 结果
     */
    public int updateRdRecipeMaster(RdEbomMaster rdEbomMaster);

    /**
     * 批量删除配方
     * 
     * @param uniqueIds 需要删除的配方ID
     * @return 结果
     */
    public int deleteRdRecipeMasterByIds(List<RdEbomMaster> list);

    /**
     * 删除配方信息
     * 
     * @param uniqueId 配方ID
     * @return 结果
     */
    public int deleteRdRecipeMasterById(Long uniqueId);

    RdEbomMaster getNextOrLast(RdEbomMaster rdEbomMaster);

    RdEbomMaster batchCheck(List<RdEbomMaster> list);

    RdEbomMaster batchAntiCheck(List<RdEbomMaster> list);

    void changeWorkStatus(RdEbomMaster rdEbomMaster);

    List<RdEbomExport> detail(RdEbomExport rdRecipeExport);

    void check(String rdCode, ArrayList<String> list);

   void importExcelEbom(String username, InputStream is) throws Exception;

    void exportLink(String rdCode, ArrayList<RdEbomSalve> list);

    List<RdEbomMaster> associate(RdEbomMaster rdEbomMaster);

    RdTree tree(String invCode,RdTree rdTree) throws UnsupportedEncodingException;

    void clear(String workType);

//    List<RdEbomMaster> selectRdRecipeMasterListNew(RdEbomMaster rdEbomMaster);

    List<RdEbomMaster> lead(RdEbomMaster rdEbomMaster);

    List<RdEbomExport> leadInto(RdEbomExport rdEbomSalve);
}


