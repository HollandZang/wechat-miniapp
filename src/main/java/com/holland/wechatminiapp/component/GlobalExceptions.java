package com.holland.wechatminiapp.component;

import com.holland.wechatminiapp.constants.SpringConstants;
import com.holland.wechatminiapp.kit.BizException;
import com.holland.wechatminiapp.kit.Res;
import com.holland.wechatminiapp.kit.ThrowableKit;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptions {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BizException.class)
    public Res<?> bizExceptionHandle(BizException bizException) {
        return new Res<>(1, bizException.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({BindException.class})
    public Res<?> validExceptionHandle(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        return new Res<>(1, null != fieldError ? fieldError.getDefaultMessage() : e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({ValidationException.class})
    public Res<?> validExceptionHandle(ValidationException e) {
        return new Res<>(1, e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(WxErrorException.class)
    public Res<?> wxErrorExceptionHandle(WxErrorException wxErrorException) {
        WxError error = wxErrorException.getError();
        return new Res<>(error.getErrorCode(), error.getErrorMsg(), error.getJson());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Throwable.class)
    public Res<?> defaultExceptionHandle(Throwable throwable, HttpServletRequest request) {
        log.error("{} 未捕获的请求异常", request.getAttribute(SpringConstants.KEY_REQ_UID), throwable);
        return new Res<>(500, ThrowableKit.getMsg(throwable), null);
    }

}
