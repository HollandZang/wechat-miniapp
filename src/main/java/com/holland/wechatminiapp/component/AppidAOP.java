package com.holland.wechatminiapp.component;

import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Aspect
@Component
@Order(1000)
public class AppidAOP {
    @Resource
    private HttpServletRequest request;

    @Around("execution(public * com.holland.wechatminiapp.biz.*.*Api.*(..))")
    public Object setThreadAppid(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Map<?, ?> pathVariables = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String appid = (String) pathVariables.get("appid");

        if (null == appid) return proceedingJoinPoint.proceed();

        WxMaConfigHolder.set(appid);
        try {
            return proceedingJoinPoint.proceed();
        } finally {
            WxMaConfigHolder.remove();
        }
    }
}
