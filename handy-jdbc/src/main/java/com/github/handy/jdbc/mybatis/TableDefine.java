package com.github.handy.jdbc.mybatis;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>表定义类</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 18:20
 */
@Data
public class TableDefine {

    /**
     * 数据库表名
     */
    private String table;
    /**
     * 表对应类
     */
    private String classType;
    /**
     * 主键
     */
    private TableColumnDefine idColumn;
    /**
     * 除主键外的其他字段
     */
    private List<TableColumnDefine> columnList;

    //返回结果映射id
    private String baseResultMap;

    //主键生成方式
    private KeyGenerationMode keyGenerationMode;

    private String baseColumnsId;

    private String baseColumns;

    //命名空间
    private String namespace;


    public List<TableColumnDefine> getAllColumnList() {
        List<TableColumnDefine> allColumnList = new ArrayList<>();
        allColumnList.add(idColumn);
        allColumnList.addAll(columnList);
        return allColumnList;
    }
}
