package com.github.handy.jdbc.mybatis;

import com.github.handy.core.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;

/**
 * <p>这里是生成通用方法对应的xml片段,这里后续可以改成通过模版引擎</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 18:09
 */
@Slf4j
public class GenericMethodXmlGenerator {

    public static void process(Writer writer, TableDefine table, String idClassType, HashSet<String> existMethodSet, KeyGenerationMode keyGenerationMode) throws IOException {

        //这里是生成默认的方法的xml片段，如果自己有在xml中定义同id的主句，那里这就不再生成

        log.info("GenericMethodXmlGenerator | {}", "process");

        if (table.getBaseColumns() == null) {
            writer.write(addBaseColumnsSQL(table));
        }

        if (!existMethodSet.contains("insert")) {
            writer.write(addInsertAndReturnId(table, idClassType, keyGenerationMode));
        }

        if (!existMethodSet.contains("insertSelective")) {
            writer.write(addInsertSelectiveAndReturnId(table, idClassType, keyGenerationMode));
        }

        if (!existMethodSet.contains("insertBatch")) {
            writer.write(addInsertBatch(table, idClassType, keyGenerationMode));
        }

        if (!existMethodSet.contains("update")) {
            writer.write(addUpdateByPrimaryKey(table));
        }

        if (!existMethodSet.contains("updateSelective")) {
            writer.write(addUpdateByPrimaryKeySelective(table));
        }

        if (!existMethodSet.contains("delete")) {
            writer.write(addDeleteByPrimaryKey(table, idClassType));
        }

        if (!existMethodSet.contains("disable")) {
            writer.write(addDisableByPrimaryKey(table, idClassType));
        }

        if (!existMethodSet.contains("enable")) {
            writer.write(addEnableByPrimaryKey(table, idClassType));
        }

        if (!existMethodSet.contains("get")) {
            writer.write(addSelectByPrimaryKey(table, idClassType));
        }

        if (!existMethodSet.contains("getByIds")) {
            writer.write(addSelectByPrimaryKeys(table, idClassType));
        }

        if (!existMethodSet.contains("selectAll")) {
            writer.write(addSelectByModel(table, idClassType));
        }

        if (!existMethodSet.contains("search")) {
            writer.write(addSearch(table, idClassType));
        }
    }


    private static String addBaseColumnsSQL(TableDefine table) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<sql id=\"")
                .append(table.getBaseColumnsId())
                .append("\" >\n")
                .append(getColumns(table))
                .append("\n</sql>\n");
        return sb.toString();
    }

    private static String addInsert(TableDefine table) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<insert id=\"insert\"  parameterType=\"")
                .append(table.getClassType())
                .append("\" >")
                .append("\n insert into ").append(table.getTable())
                .append("(")
                .append(getColumns(table))
                .append(")")
                .append("\n    values ")
                .append(getValues(table.getAllColumnList()))
                .append("\n</insert>\n");

        return sb.toString();
    }

    private static String addInsertSelective(TableDefine table) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n <insert id=\"insertSelective\" parameterType=\"")
                .append(table.getClassType())
                .append("\"  >")
                .append("\n insert into ")
                .append(table.getTable())
                .append(getIfColumns(table))
                .append("\n    values ")
                .append(getIfValues(table.getAllColumnList()))
                .append("\n</insert>\n");
        return sb.toString();
    }

    private static String addInsertAndReturnId(TableDefine table, String idProertyClassType, KeyGenerationMode keyGenerationMode) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildIdGenerationSql(table, idProertyClassType, "insert", table.getClassType(), keyGenerationMode))
                .append("\n insert into ")
                .append(table.getTable())
                .append("(").append(getColumns(table)).append(")")
                .append("\n    values ").append(getValues(table.getAllColumnList()))
                .append("\n</insert>\n");
        return sb.toString();
    }

    private static String addInsertSelectiveAndReturnId(TableDefine table, String idProertyClassType, KeyGenerationMode keyGenerationMode) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildIdGenerationSql(table, idProertyClassType, "insertSelective", table.getClassType(), keyGenerationMode))
                .append("\n insert into ").append(table.getTable()).append(getIfColumns(table))
                .append("\n    values ").append(getIfValues(table.getAllColumnList()))
                .append("\n</insert>\n");
        return sb.toString();
    }

    private static String addInsertBatch(TableDefine table, String idProertyClassType, KeyGenerationMode keyGenerationMode) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildIdGenerationSql(table, idProertyClassType, "insertBatch", "java.util.List", keyGenerationMode))
                .append("\n insert into ").append(table.getTable())
                .append("(").append(getColumns(table)).append(")")
                .append("\n    values ").append(getBatchValues(table.getAllColumnList()))
                .append("\n</insert>\n");
        return sb.toString();
    }

    private static String addUpdateByPrimaryKey(TableDefine table) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n <update id=\"update\" parameterType=\"").append(table.getClassType()).append("\" >")
                .append("\n update ").append(table.getTable())
                .append(getConditions4Update(table.getColumnList(), false))
                .append("\n where ").append(getKeyCondition(table.getIdColumn()))
                .append("\n</update>\n");
        return sb.toString();
    }

    private static String addUpdateByPrimaryKeySelective(TableDefine table) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n <update id=\"updateSelective\" parameterType=\"").append(table.getClassType()).append("\" >")
                .append("\n update ").append(table.getTable())
                .append(getConditions4Update(table.getColumnList(), true))
                .append("\n where ").append(getKeyCondition(table.getIdColumn()))
                .append("\n</update>\n");
        return sb.toString();
    }

    private static String addDeleteByPrimaryKey(TableDefine table, String idProertyClassType) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<delete id=\"delete\"  parameterType=\"").append(idProertyClassType).append("\" >")
                .append("\n delete  from ").append(table.getTable())
                .append("\n    where ").append(table.getIdColumn().getColumn()).append(" in ")
                .append("\n    <foreach collection=\"array\" item=\"id\" open=\"(\" separator=\",\" close=\")\">")
                .append("\n        #{id}")
                .append("\n    </foreach>")
                .append("\n</delete>\n");
        return sb.toString();
    }

    private static String addDisableByPrimaryKey(TableDefine table, String idProertyClassType) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<update id=\"disable\"  parameterType=\"").append(idProertyClassType).append("\" >")
                .append("\n update ").append(table.getTable()).append(" set is_deleted = 'Y' ")
                .append("\n    where ").append(table.getIdColumn().getColumn()).append(" in ")
                .append("\n    <foreach collection=\"array\" item=\"id\" open=\"(\" separator=\",\" close=\")\">")
                .append("\n        #{id}")
                .append("\n    </foreach>")
                .append("\n</update>\n");
        return sb.toString();
    }

    private static String addEnableByPrimaryKey(TableDefine table, String idProertyClassType) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<update id=\"enable\"  parameterType=\"").append(idProertyClassType).append("\" >")
                .append("\n update ").append(table.getTable()).append(" set is_deleted = 'N' ")
                .append("\n    where ").append(table.getIdColumn().getColumn()).append(" in ")
                .append("\n    <foreach collection=\"array\" item=\"id\" open=\"(\" separator=\",\" close=\")\">")
                .append("\n        #{id}")
                .append("\n    </foreach>")
                .append("\n</update>\n");
        return sb.toString();
    }

    private static String addSelectByPrimaryKey(TableDefine table, String idProertyClassType) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<select id=\"get\" resultMap=\"").append(table.getBaseResultMap()).append("\" parameterType=\"").append(idProertyClassType).append("\" >")
                .append("\n select ")
                .append(getColumns(table))
                .append("\n    from ").append(table.getTable())
                .append("\n    where ").append(getKeyCondition(table.getIdColumn())).append(" and is_deleted = 'N'")
                .append("\n</select>\n");
        return sb.toString();
    }

    private static String addSelectByPrimaryKeys(TableDefine table, String idProertyClassType) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<select id=\"getByIds\" resultMap=\"").append(table.getBaseResultMap()).append("\" parameterType=\"").append(idProertyClassType).append("\" >")
                .append("\n select ").append(getColumns(table))
                .append("\n    from ").append(table.getTable())
                .append("\n    where ").append(table.getIdColumn().getColumn()).append(" in ")
                .append("\n    <foreach collection=\"array\" item=\"id\" open=\"(\" separator=\",\" close=\")\">")
                .append("\n        #{id}")
                .append("\n    </foreach>")
                .append("\n    and is_deleted = 'N'")
                .append("\n</select>\n");
        return sb.toString();
    }

    private static String addSearch(TableDefine table, String idProertyClassType) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<select id=\"search\" resultMap=\"").append(table.getBaseResultMap()).append("\" >")
                .append("\n select ")
                .append(getColumns(table))
                .append("\n    from ").append(table.getTable())
//                .append("\n    <if test=\"pageCondition != null \" >")
//                .append(getConditions4Query(table.getAllColumnList(), null, "pageCondition."))
//                .append("\n    </if>")
                .append("\n</select>\n");

        return sb.toString();
    }

    private static String addSelectByModel(TableDefine table, String idProertyClassType) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<select id=\"selectAll\" resultMap=\"").append(table.getBaseResultMap()).append("\" parameterType=\"").append(table.getClassType()).append("\" >");
        sb.append("\n select ");
        sb.append(getColumns(table));
        sb.append("\n    from ").append(table.getTable());
        sb.append(getConditions4Query(table.getAllColumnList(), null, null));
        sb.append("\n</select>\n");
        return sb.toString();
    }


    //构建id生成Sql
    private static String buildIdGenerationSql(TableDefine table, String idProertyClassType, String method, String parameterType, KeyGenerationMode globalKeyGenerationMode) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<insert id=\"").append(method).append("\" parameterType=\"").append(parameterType).append("\" ");

        KeyGenerationMode keyGenerationMode = table.getKeyGenerationMode();
        if (keyGenerationMode == null) {
            keyGenerationMode = globalKeyGenerationMode;
        }

        if (keyGenerationMode != null) {
            if (KeyGenerationMode.CUSTOM == keyGenerationMode) {
                sb.append(">");
            } else {
                if (KeyGenerationMode.DB_UUID == keyGenerationMode) {
                    sb.append(">");
                    sb.append("\n <selectKey keyProperty=\"").append(table.getIdColumn().getProperty()).append("\" resultType=\"").append(idProertyClassType).append("\" order=\"BEFORE\"> \n");
                    sb.append(setDefault(keyGenerationMode.getValue(), "select replace(uuid(),'-','') from dual"));
                    sb.append("\n </selectKey>");
                } else if (KeyGenerationMode.MYCAT == keyGenerationMode) {
                    sb.append(">");
                    sb.append("\n <selectKey keyProperty=\"").append(table.getIdColumn().getProperty()).append("\" resultType=\"").append(idProertyClassType).append("\" order=\"BEFORE\"> \n");
                    sb.append(setDefault(keyGenerationMode.getValue(), "select next value for MYCATSEQ_" + table.getTable().toUpperCase()));
                    sb.append("\n </selectKey>");
                } else if (KeyGenerationMode.IDENTITY == keyGenerationMode) {
                    sb.append("useGeneratedKeys=\"true\" keyProperty=\"").append(table.getIdColumn().getProperty()).append("\">");
                } else if (KeyGenerationMode.UUID == keyGenerationMode) {
                    sb.append(">");
                    sb.append("\n <selectKey keyProperty=\"").append(table.getIdColumn().getProperty()).append("\" resultType=\"").append(idProertyClassType).append("\" order=\"BEFORE\"> \n");
                    sb.append("       <bind name=\"").append("temp").append("\" value='").append(setDefault(keyGenerationMode.getValue(), "@java.util.UUID@randomUUID().toString().replace(\"-\", \"\")")).append("'/> \n");
                    sb.append("       "+setDefault(keyGenerationMode.getValue(), "select #{temp} from dual"));
                    sb.append("\n </selectKey>");
                }
            }
        }

        return sb.toString();
    }

    private static String setDefault(String str, String defaultStr) {
        if (str != null && StringUtils.trimWhitespace(str).length() != 0) {
            return str;
        } else {
            return defaultStr;
        }

    }

    private static String getColumns(TableDefine table) {
        StringBuilder sb = new StringBuilder();
        for (TableColumnDefine tableColumn : table.getAllColumnList()) {
            sb.append(tableColumn.getColumn());
            sb.append(",");
        }
        if (sb.indexOf(",") != -1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static String getIfColumns(TableDefine table) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n   <trim prefix=\"(\" suffix=\")\"   suffixOverrides=\",\" >");
        for (TableColumnDefine tableColumn : table.getAllColumnList()) {
            sb.append("\n    <if test=\"").append(tableColumn.getProperty()).append(" != null\" >")
                    .append(tableColumn.getColumn())
                    .append(",")
                    .append("\n    </if>");
        }
        sb.append("\n   </trim>");
        return sb.toString();
    }

    private static String getKeyCondition(TableColumnDefine idColumn) {
        if (idColumn.getJdbcType() != null) {
            return idColumn.getColumn() + " = #{" + idColumn.getProperty() + ",jdbcType=" + idColumn.getJdbcType() + "}";
        } else {
            return idColumn.getColumn() + " = #{" + idColumn.getProperty() + "}";
        }

    }

    private static String getConditions4Update(List<TableColumnDefine> columnList, boolean needif) {
        String prefix = "SET";
        String stuffix = ",";

        StringBuilder sb = new StringBuilder();
        sb.append("\n   <trim prefix=\"").append(prefix).append("\"  suffixOverrides=\"").append(stuffix).append("\" >");
        for (TableColumnDefine column : columnList) {
            if (isDefault(column)) {
                sb.append("\n    <if test=\"").append(column.getProperty()).append(" != null\" >");
                sb.append("\n   	  ").append(column.getColumn()).append(" = #{").append(column.getProperty());
                if (column.getJdbcType() != null) {
                    sb.append(",jdbcType=").append(column.getJdbcType());
                }
                sb.append("}").append(stuffix);
                sb.append("\n    </if>");
            } else {
                if (needif) {
                    sb.append("\n    <if test=\"").append(column.getProperty()).append(" != null\" >");
                }
                sb.append("\n   	  ").append(column.getColumn()).append(" = #{").append(column.getProperty());
                if (column.getJdbcType() != null) {
                    sb.append(",jdbcType=").append(column.getJdbcType());
                }
                sb.append("}").append(stuffix);
                if (needif) {
                    sb.append("\n    </if>");
                }
            }
        }
        sb.append("\n   </trim>");
        return sb.toString();
    }

    private static boolean isDefault(TableColumnDefine column) {
        if (Constant.GMT_CREATED.equals(column.getColumn())) {
            return true;
        }

        if (Constant.CREATOR.equals(column.getColumn())) {
            return true;
        }

        if (Constant.GMT_MODIFIED.equals(column.getColumn())) {
            return true;
        }

        if (Constant.MODIFIER.equals(column.getColumn())) {
            return true;
        }

        if (Constant.IS_DELETED.equals(column.getColumn())) {
            return true;
        }

        if (Constant.ID.equals(column.getColumn())) {
            return true;
        }
        return false;

    }

    private static String getConditions4Query(List<TableColumnDefine> columnList, String prefixSQL, String var) {


        String prefix = "WHERE";
        String prefixOverrides = "AND | OR";

        if (var == null) {
            var = "";
        }
        var = var.trim();

        StringBuilder sb = new StringBuilder();
        sb.append("\n   <trim prefix=\"").append(prefix).append("\"  prefixOverrides=\"").append(prefixOverrides).append("\" >");
        if (prefixSQL != null) {
            sb.append("\n   	  ").append(prefixSQL);
        }
        for (TableColumnDefine column : columnList) {
            sb.append("\n    <if test=\"").append(var).append(column.getProperty()).append(" != null\" >");
            sb.append("\n   	  AND ").append(column.getColumn()).append(" = #{").append(var).append(column.getProperty());
            if (column.getJdbcType() != null) {
                sb.append(", jdbcType=").append(column.getJdbcType());
            }
            sb.append("}");
            sb.append("\n    </if>");
        }
        sb.append("\n   </trim>");
        return sb.toString();
    }

    private static String getValues(List<TableColumnDefine> columnList) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (TableColumnDefine column : columnList) {
            sb.append("#{").append(column.getProperty());
            if (column.getJdbcType() != null) {
                sb.append(", jdbcType=").append(column.getJdbcType());
            }
            sb.append("},");
        }
        if (sb.indexOf(",") != -1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }

    private static String getIfValues(List<TableColumnDefine> columnList) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n   <trim prefix=\"(\" suffix=\")\"  suffixOverrides=\",\" >");
        for (TableColumnDefine column : columnList) {
            sb.append("\n    <if test=\"").append(column.getProperty()).append(" != null\" >");
            sb.append("#{").append(column.getProperty());
            if (column.getJdbcType() != null) {
                sb.append(", jdbcType=").append(column.getJdbcType());
            }
            sb.append("},");
            sb.append("\n    </if>");
        }
        sb.append("\n   </trim>");
        return sb.toString();
    }


    private static String getBatchValues(List<TableColumnDefine> columnList) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n   <trim prefix=\"\"  suffixOverrides=\",\" >");
        sb.append("\n <foreach item=\"model\" index=\"index\" collection=\"list\"> ");
        sb.append("\n (");
        for (TableColumnDefine column : columnList) {
            sb.append("#{model.").append(column.getProperty());
            if (column.getJdbcType() != null){
                sb.append(",jdbcType=").append(column.getJdbcType());
            }
            sb.append("},");
        }
        if (sb.indexOf(",") != -1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(" ),");
        sb.append("\n </foreach>");
        sb.append("\n   </trim>");
        return sb.toString();
    }
}
