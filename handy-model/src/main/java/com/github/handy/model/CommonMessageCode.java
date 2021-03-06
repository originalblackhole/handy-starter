package com.github.handy.model;

/**
 * <p>基础结果标记</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 15:59
 */

public enum CommonMessageCode implements MessageCode {

    RESOURCE_NOT_FOUND(404, "The resource required for this operation does not exist"),
    UNKNOWN_EXCEPTION(-1, "Unknown system error. Please try again"),
    SUCCESS(0, "OK"),
    INSERT_EXCEPTION(10, "Exception occurred when new data was added"),
    INSERT_BATCH_EXCEPTION(11, "Exception occurred when new data was batch added"),
    UPDATE_EXCEPTION(20, "Exception occurred when data was updated"),
    DELETE_EXCEPTION(30, "Exception occurred when data was deleted"),
    DISABLE_EXCEPTION(31, "Exception occurred when data was disabled"),
    ENABLE_EXCEPTION(32, "Exception occurred when data was enabled"),
    SELECT_ONE_EXCEPTION(40, "Exception occurred when single data was gotten"),
    SELECT_EXCEPTION(41, "Exception occurred when data was gotten"),
    SELECT_PAGINATION_EXCEPTION(42, "Exception occurred when paging data was gotten"),
    INVALID_ARGUMENT(50, "invalid argument {0}");


    private int code;
    private String message;

    private CommonMessageCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return Integer.toString(getCode());
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }
}
