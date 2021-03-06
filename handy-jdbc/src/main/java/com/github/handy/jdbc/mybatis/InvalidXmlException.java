package com.github.handy.jdbc.mybatis;

/**
 * <p></p>
 *
 * @author rui.zhou
 * @date 2018/12/4 18:13
 */
public class InvalidXmlException extends  RuntimeException {

    private static final long serialVersionUID = -4537976472398734323L;

    public InvalidXmlException(String message) {
        super(message);
    }

    public InvalidXmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
