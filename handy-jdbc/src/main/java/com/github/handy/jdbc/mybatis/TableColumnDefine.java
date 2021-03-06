package com.github.handy.jdbc.mybatis;

import lombok.Data;

/**
 * <p>表字段定义</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 18:20
 */
@Data
public class TableColumnDefine {

    /**
     * 数据库字段名
     */
    private String column;
    /**
     * 对应该类字段
     */
    private String property;
    /**
     * 数据库类型
     */
    private String jdbcType;
}
