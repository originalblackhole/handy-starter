package com.github.handy.web.handler;

import com.github.handy.core.exception.BusinessException;
import com.github.handy.model.CommonMessageCode;
import com.github.handy.model.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p></p>
 *
 * @author rui.zhou
 * @date 2018/12/4 18:40
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //业务异常统一处理
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)//200
    public ResponseData businessExceptionHandler(BusinessException e) {
        log.error( "An error occurred while processing your request : Cause by "+ e,e);
        return new ResponseData(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)//200
    public ResponseData otherExceptionHandler(Exception e) {
        log.error( "An error occurred while processing your request : Cause by "+ e,e);
        return new ResponseData(CommonMessageCode.UNKNOWN_EXCEPTION);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)//200
    public ResponseData methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.debug("Exception occurred while processing your request : Cause by " + e, e);
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String field = error.getField();
        String code = error.getDefaultMessage();
        String message = String.format("%s:%s", field, code);
        return new ResponseData(CommonMessageCode.INVALID_ARGUMENT.formatMessage(message));
    }


    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)//200
    public ResponseData bindExceptionHandler(BindException e) {
        log.debug("Exception occurred while processing your request : Cause by " + e, e);
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String field = error.getField();
        String code = error.getDefaultMessage();
        String message = String.format("%s:%s", field, code);
        return new ResponseData(CommonMessageCode.INVALID_ARGUMENT.formatMessage(message));
    }
}
