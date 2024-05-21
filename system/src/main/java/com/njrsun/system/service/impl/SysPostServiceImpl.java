package com.njrsun.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.njrsun.common.core.domain.entity.SysPost;
import com.njrsun.system.mapper.SysPostMapper;
import com.njrsun.system.mapper.SysUserPostMapper;
import com.njrsun.system.service.ISysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.njrsun.common.constant.UserConstants;
import com.njrsun.common.exception.CustomException;
import com.njrsun.common.utils.StringUtils;

import org.springframework.transaction.annotation.Transactional;

/**
 * 岗位信息 服务层处理
 * 
 * @author njrsun
 */
@Primary
@Service
public class SysPostServiceImpl implements ISysPostService
{
    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    /**
     * 查询岗位信息集合
     * 
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post)
    {
        return postMapper.selectPostList(post);
    }

    /**
     * 查询所有岗位
     * 
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll()
    {
        return postMapper.selectPostAll();
    }


    /**
     * 通过岗位ID查询岗位信息
     * 
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(Long postId)
    {
        SysPost sysPost = postMapper.selectPostById(postId);
        List<JSONObject> workType =  new ArrayList<>();
        ArrayList<Map<String,String>>  list =  postMapper.selectWorkType(postId);

        for (Map<String, String> stringStringMap : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("workKey",stringStringMap.get("work_key"));
            jsonObject.put("workType",stringStringMap.get("work_type"));
            jsonObject.put("dictId",stringStringMap.get("dict_id"));
            workType.add(jsonObject);
        }
        sysPost.setWorkType(workType);
        return sysPost;

    }

    /**
     * 根据用户ID获取岗位选择框列表
     * 
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<Integer> selectPostListByUserId(Long userId)
    {
        return postMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public String checkPostNameUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostNameUnique(post.getPostName());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验岗位编码是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public String checkPostCodeUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostCodeUnique(post.getPostCode());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int countUserPostById(Long postId)
    {
        return userPostMapper.countUserPostById(postId);
    }

    /**
     * 删除岗位信息
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int deletePostById(Long postId)
    {
        return postMapper.deletePostById(postId);
    }

    /**
     * 批量删除岗位信息
     * 
     * @param postIds 需要删除的岗位ID
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deletePostByIds(Long[] postIds)
    {
        for (Long postId : postIds)
        {
            SysPost post = selectPostById(postId);
            if (countUserPostById(postId) > 0)
            {
                throw new CustomException(String.format("%1$s已分配,不能删除", post.getPostName()));
            }
        }
        postMapper.deletePostDatas(postIds);
        return postMapper.deletePostByIds(postIds);
    }

    /**
     * 新增保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPost(SysPost post)
    {

        List<JSONObject> workType = post.getWorkType();

        int i = postMapper.insertPost(post);
        if (workType.size() != 0){
            postMapper.insertPostData(workType,post.getPostId());
        }
    return i;
    }

    /**
     * 修改保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePost(SysPost post)
    {
        List<JSONObject> list = post.getWorkType();
       postMapper.deletePostData(post.getPostId());
       if(list.size() !=0){
           postMapper.insertPostData(list, post.getPostId());
       }

        return postMapper.updatePost(post);
    }
}
