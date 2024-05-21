package com.njrsun.common.core.domain.entity;


import com.alibaba.fastjson.JSONObject;
import com.njrsun.common.annotation.Excel;
import com.njrsun.common.core.domain.BaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 岗位表 sys_post
 * 
 * @author njrsun
 */
public class SysPost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 岗位序号 */
    @Excel(name = "岗位序号", cellType = Excel.ColumnType.NUMERIC)
    private Long postId;

    /** 岗位编码 */
    @Excel(name = "岗位编码")
    private String postCode;

    /** 岗位名称 */
    @Excel(name = "岗位名称")
    private String postName;

    /** 岗位排序 */
    @Excel(name = "岗位排序")
    private String postSort;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String enableAcl;

    private List<JSONObject> workType;

    public List<JSONObject> getWorkType() {
        return workType;
    }

    public void setWorkType(List<JSONObject> list) {
        this.workType = list;
    }

    /** 用户是否存在此岗位标识 默认不存在 */
    private boolean flag = false;

    public Long getPostId()
    {
        return postId;
    }

    public void setPostId(Long postId)
    {
        this.postId = postId;
    }

    @NotBlank(message = "岗位编码不能为空")
    @Size(min = 0, max = 64, message = "岗位编码长度不能超过64个字符")
    public String getPostCode()
    {
        return postCode;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    @NotBlank(message = "岗位名称不能为空")
    @Size(min = 0, max = 50, message = "岗位名称长度不能超过50个字符")
    public String getPostName()
    {
        return postName;
    }

    public void setPostName(String postName)
    {
        this.postName = postName;
    }

    @NotBlank(message = "显示顺序不能为空")
    public String getPostSort()
    {
        return postSort;
    }

    public void setPostSort(String postSort)
    {
        this.postSort = postSort;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public boolean isFlag()
    {
        return flag;
    }

    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }

    public String getEnableAcl() {
        return enableAcl;
    }

    public void setEnableAcl(String enableAcl) {
        this.enableAcl = enableAcl;
    }

    @Override
    public String toString() {
        return "SysPost{" +
                "postId=" + postId +
                ", postCode='" + postCode + '\'' +
                ", postName='" + postName + '\'' +
                ", postSort='" + postSort + '\'' +
                ", status='" + status + '\'' +
                ", enableAcl='" + enableAcl + '\'' +
                ", list=" + workType +
                ", flag=" + flag +
                '}';
    }

    public boolean hasWorkType(String key, String type){
        for (JSONObject jsonObject : workType) {
             if(jsonObject.get("workKey").equals(key) && jsonObject.get("workType").equals(type)) {
                 return true;
             }
        }
        return  false;
    }

     public String[]  getWorkTypeByKey(String key){
         ArrayList<String> strings = new ArrayList<>();
         for (JSONObject jsonObject : workType) {
             if(jsonObject.get("workKey").equals(key)) {
                 strings.add(jsonObject.get("workType").toString());
             }
         }
         String[] array = strings.toArray(new String[strings.size()]);
         return array;
     }

}
