package com.github.handy.model;

import java.text.MessageFormat;

/**
 * <p>返回结果标记</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 15:59
 */
public interface MessageCode {

    String getMessage();

    int getCode();

    default boolean isSuccess() {
        return this.getCode() == 0;
    }

    /**
     * message使用了占位符（我是{0}）时可以调用该方法进行格式化
     */
    default FormattedMessageCode formatMessage(Object... args) {
        return new FormattedMessageCode(this.getCode(), MessageFormat.format(this.getMessage(), args));
    }

    class FormattedMessageCode implements MessageCode {

        private Integer code;
        private String message;

        FormattedMessageCode(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        @Override
        public int getCode() {
            return this.code;
        }
    }
}
