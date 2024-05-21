package com.njrsun.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.njrsun.common.core.domain.entity.SysModule;
import com.njrsun.system.mapper.SysDictDataMapper;
import com.njrsun.system.mapper.SysDictTypeMapper;
import com.njrsun.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.core.domain.entity.SysDictData;
import com.njrsun.common.core.domain.entity.SysDictType;
import com.njrsun.common.exception.CustomException;
import com.njrsun.common.utils.DictUtils;
import com.njrsun.common.utils.StringUtils;

/**
 * 字典 业务层处理
 * 
 * @author njrsun
 */
@Primary
@Service
public class SysDictTypeServiceImpl implements ISysDictTypeService
{
    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Autowired
    private SysDictTypeServiceImpl sysDictTypeService;

    @Value("${njrsun.tenant}")
    private boolean isTenant;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init()
    {
        // 租户模式不自启动缓存
        if(isTenant==true)return;

        List<SysDictType> dictTypeList = dictTypeMapper.selectDictTypeAll();
        for (SysDictType dictType : dictTypeList)
        {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictType.getDictType());
            DictUtils.setDictCache(dictType.getDictType(), dictDatas);
        }
    }


  public HashMap<String,String> transform(String type){
        HashMap<String, String> hashMap = new HashMap<>();
    List<SysDictData> inv_in_type = sysDictTypeService.selectDictDataByType(type);
    if(StringUtils.isNotNull(inv_in_type)){
        for (SysDictData sysDictData : inv_in_type) {
            hashMap.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
        }
    }
    return hashMap;
  }





    /**
     * 根据条件分页查询字典类型
     * 
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType)
    {
        return dictTypeMapper.selectDictTypeList(dictType);
    }

    /**
     * 根据所有字典类型
     * 
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeAll()
    {
        return dictTypeMapper.selectDictTypeAll();
    }

    /**
     * 根据字典类型查询字典数据
     * 
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType)
    {
        List<SysDictData> dictDatas = DictUtils.getDictCache(dictType);
        if (StringUtils.isNotEmpty(dictDatas))
        {
            return dictDatas;
        }
        dictDatas = dictDataMapper.selectDictDataByType(dictType);
        if (StringUtils.isNotEmpty(dictDatas))
        {
            DictUtils.setDictCache(dictType, dictDatas);
            return dictDatas;
        }
        return null;
    }

    /**
     * 根据字典类型ID查询信息
     * 
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId)
    {
        return dictTypeMapper.selectDictTypeById(dictId);
    }

    /**
     * 根据字典类型查询信息
     * 
     * @param dictType 字典类型
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType)
    {
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    /**
     * 批量删除字典类型信息
     * 
     * @param dictIds 需要删除的字典ID
     * @return 结果
     */
    @Override
    public int deleteDictTypeByIds(Long[] dictIds)
    {
        for (Long dictId : dictIds)
        {
            SysDictType dictType = selectDictTypeById(dictId);
            if (dictDataMapper.countDictDataByType(dictType.getDictType()) > 0)
            {
                throw new CustomException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
        }
        int count = dictTypeMapper.deleteDictTypeByIds(dictIds);
        if (count > 0)
        {
            DictUtils.clearDictCache();
        }
        return count;
    }

    /**
     * 清空缓存数据
     */
    @Override
    public void clearCache()
    {
        DictUtils.clearDictCache();
    }

    /**
     * 新增保存字典类型信息
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    public int insertDictType(SysDictType dictType)
    {
        int row = dictTypeMapper.insertDictType(dictType);
        if (row > 0)
        {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 修改保存字典类型信息
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDictType(SysDictType dictType)
    {
        SysDictType oldDict = dictTypeMapper.selectDictTypeById(dictType.getDictId());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dictType.getDictType());
        int row = dictTypeMapper.updateDictType(dictType);
        if (row > 0)
        {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 校验字典类型称是否唯一
     * 
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public String checkDictTypeUnique(SysDictType dict)
    {
        Long dictId = StringUtils.isNull(dict.getDictId()) ? -1L : dict.getDictId();
        SysDictType dictType = dictTypeMapper.checkDictTypeUnique(dict.getDictType());
        if (StringUtils.isNotNull(dictType) && dictType.getDictId().longValue() != dictId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }



    @Override
    public List<SysModule> selectModuleList() {

        return dictTypeMapper.selectModuleList();
    }


    @Override
    public JSONArray getModuleDict1() {

        List<SysModule> list1 = dictTypeMapper.selectModuleList();
        JSONArray objects = new JSONArray();
        for (SysModule sysModule : list1) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",sysModule.getCode());
            jsonObject.put("name",sysModule.getName());
            JSONArray objects1 = new JSONArray();
            String code = sysModule.getCode();

          List<SysDictType>   list2 = dictTypeMapper.selectDictByMoudleId(code);
            for (SysDictType sysDictType : list2) {
                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("id",sysDictType.getDictId());
                jsonObject2.put("dictType",sysDictType.getDictType());
                jsonObject2.put("dictName",sysDictType.getDictName());
                jsonObject2.put("remark",sysDictType.getRemark());
                JSONArray objects2 = new JSONArray();
                String dictId = sysDictType.getDictType();
                List<SysDictData> sysDictData = sysDictTypeService.selectDictDataByType(dictId);
                for (SysDictData sysDictDatum : sysDictData) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("id",sysDictDatum.getDictCode()+10000);
                    jsonObject1.put("dictLabel",sysDictDatum.getDictLabel());
                    jsonObject1.put("dictValue",sysDictDatum.getDictValue());
                    jsonObject1.put("type",sysDictType.getDictType());
                    objects2.add(jsonObject1);
                }
                 jsonObject2.put("children",objects2);
                objects1.add(jsonObject2);
            }
            if(objects1.size() !=0){
                jsonObject.put("children",objects1);
                objects.add(jsonObject);
            }
        }
         return objects;
    }

    @Override
    public List<Map<String, String>> transInvoiceWorkType(List<Map<String, String>> list) {
        for (Map<String, String> stringStringMap : list) {
            String type = stringStringMap.get("type");
            String type_id = stringStringMap.get("type_id");
            String invoice = stringStringMap.get("invoice");
            String invoice_id = stringStringMap.get("invoice_id");
            List<SysDictData> om_type = selectDictDataByType(type);
            if(om_type==null)continue;

            List<SysDictData> om_invoice_type = selectDictDataByType(invoice);
            if(om_invoice_type==null)continue;

            HashMap<String, String> hashMap1 = new HashMap<>();
            for (SysDictData sysDictData : om_type) {
                hashMap1.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
            }

            HashMap<String, String> hashMap2 = new HashMap<>();
            for (SysDictData sysDictData : om_invoice_type) {
                hashMap2.put(sysDictData.getDictValue(),sysDictData.getDictLabel());
            }

            stringStringMap.put("type_name",hashMap1.get(type_id));
            stringStringMap.put("invoice_name",hashMap2.get(invoice_id));
        }

        return list;
    }

}
