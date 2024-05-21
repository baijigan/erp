package com.cingsoft.cloud.helper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Eastern unbeaten
 * @email chenshiyun2011@163.com
 * @data 2019-06-22
 */
@Data
@TableName("Demo")
public class Demo {

    @TableId(type = IdType.UUID)
    private String id;
    private String name;
    private String password;
    private String nickName;
}
