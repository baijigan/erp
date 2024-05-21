package com.njrsun.common.enums;

/**
 * 业务操作类型
 * 
 * @author njrsun
 */
public enum BusinessType
{
    /**
     * 其它
     */
    OTHER,

    /**
     * 新增
     */
    INSERT,

    /**
     * 修改
     */
    UPDATE,

    /**
     * 删除
     */
    DELETE,

    /**
     * 授权
     */
    GRANT,

    /**
     * 导出
     */
    EXPORT,

    /**
     * 导入
     */
    IMPORT,

    /**
     * 强退
     */
    FORCE,

    /**
     * 生成代码
     */
    GENCODE,
    
    /**
     * 清空数据
     */
    CLEAN,
    /**
     * 查询数据
     */
    SELECT,
    /**
     * 审核
     */
    PERMIT,
    /**
     * 反审核
     */
    REVOKE,
    /**
     * 挂起
     */
    HANG
}
