package com.github.handy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * <p>统一返回结果对象</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 16:55
 */
@Slf4j
public class ResponseData<DTO> {

    /**
     * 错误或者成功代码
     */
    private int code;
    /**
     * 错误描述
     */
    private String message;
    /**
     * 响应结果
     */
    private DTO data;

    /**
     * 总数
     */
    private Integer count;

    /**
     * 构造函数
     */
    public ResponseData() {
    }

    /**
     * 构造函数
     *
     * @param code    错误或者成功代码
     * @param message 错误描述
     */
    public ResponseData(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param code    错误或者成功代码
     * @param message 错误描述
     * @param data    响应结果
     */
    public ResponseData(int code, String message, DTO data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造函数
     */
    public ResponseData(MessageCode messageCode) {
        this.code = messageCode.getCode();
        this.message = messageCode.getMessage();
    }

    /**
     * 构造函数
     */
    public ResponseData(MessageCode messageCode, DTO data) {
        this.code = messageCode.getCode();
        this.message = messageCode.getMessage();
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return CommonMessageCode.SUCCESS.getCode() == this.code;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the data
     */
    public DTO getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(DTO data) {
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
