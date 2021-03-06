package com.github.handy.jdbc.mybatis;

import org.springframework.util.StringUtils;

/**
 * <p>数据库主键生成方式</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 18:19
 */
public enum KeyGenerationMode {

    /**
     * 数据库自增
     **/
    IDENTITY("IDENTITY"),

    /**
     * 程序生成UUID
     */
    UUID("UUID"),

    /**
     * DB
     */
    DB_UUID("DB_UUID"),


    /**
     * MYCAT 序列自增
     */
    MYCAT("MYCAT"),


    /**
     * 用户自定义
     */
    CUSTOM("CUSTOM");

    private String code;

    private String value;

    KeyGenerationMode(String code) {
        this.code = code;
    }


    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public static KeyGenerationMode parse(String code) {
        KeyGenerationMode result = null;
        code = StringUtils.trimWhitespace(code);

        for (KeyGenerationMode keyGenerationMode : values()) {
            if (keyGenerationMode.getCode().equals(code)) {
                result = keyGenerationMode;
            }
        }
        //默认数据库自增
        if (result == null) {
            result = KeyGenerationMode.IDENTITY;
        }

        return result;
    }
}
