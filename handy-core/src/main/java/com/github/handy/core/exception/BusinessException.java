package com.github.handy.core.exception;

import com.github.handy.model.MessageCode;

/**
 * <p> 业务异常
 * 为保证原始异常可见，请尽量使用带有Throwable 参数的构造
 * </p>
 * @author rui.zhou
 * @date 2018/12/4 18:30
 */
public class BusinessException extends RuntimeException{

    private static final long serialVersionUID = 2340803442917949909L;

    private int code;

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(String msg, Throwable cause) {
        super(msg, cause);
    }


    /**
     *
     * @param messageCode
     */
    public BusinessException(MessageCode messageCode) {
        this(messageCode.getMessage());
        this.code = messageCode.getCode();
    }

    /**
     *
     * @param messageCode
     * @param cause  异常堆栈
     */
    public BusinessException(MessageCode messageCode, Throwable cause) {
        this(messageCode.getMessage(),cause);
        this.code = messageCode.getCode();
    }

    /**
     * 构造函数
     * @param messageCode
     * @param msgArgs   格式化消息参数 {@link String String.format}
     */
    public BusinessException(MessageCode messageCode, Object... msgArgs) {
        this(String.format(messageCode.getMessage(), msgArgs));
        this.code = messageCode.getCode();
    }

    /**
     * 支持个格式化输出
     * @param messageCode
     * @param cause  异常堆栈
     * @param msgArgs 格式化消息参数 {@link String String.format}
     */
    public BusinessException(MessageCode messageCode, Throwable cause, Object... msgArgs) {
        this(String.format(messageCode.getMessage(), msgArgs),cause);
        this.code = messageCode.getCode();
    }

    public BusinessException(int code, String message) {
        this(message);
        this.code = code;
    }

    public BusinessException(int code, String message, Throwable cause) {
        this(message, cause);
        this.code = code;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
