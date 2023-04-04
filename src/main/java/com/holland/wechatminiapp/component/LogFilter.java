package com.holland.wechatminiapp.component;

import com.alibaba.fastjson2.JSON;
import com.holland.wechatminiapp.constants.SpringConstants;
import com.holland.wechatminiapp.kit.MyHttpServletRequestWrapper;
import com.holland.wechatminiapp.kit.StrKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

@Slf4j
@Order(101)
@Component
public class LogFilter extends HttpFilter {

    private Set<String>                        mustRecordUris;
    private Set<String>                        needIgnoreUris;
    private Set<Predicate<HttpServletRequest>> ignoreChains;

    @Override
    public void init() throws ServletException {
        super.init();
        if (log.isInfoEnabled()) {
            mustRecordUris = mustRecordUris();
            log.info("Loading white uris: " + mustRecordUris);

            needIgnoreUris = needIgnoreUris();
            Set<String> intersection = new LinkedHashSet<>(needIgnoreUris);
            intersection.retainAll(mustRecordUris);
            if (intersection.size() > 0) {
                log.warn("Loading ignore error, these uris must be recorded: " + intersection);
                needIgnoreUris.removeAll(intersection);
            }
            log.info("Loading ignore uris: " + needIgnoreUris);

            ignoreChains = Set.of(
                    // 过滤 OPTIONS 探针
                    req -> HttpMethod.OPTIONS.matches(req.getMethod()),
                    // 过滤 websocket 连接
                    req -> "websocket".equals(req.getHeader("upgrade")) || "Upgrade".equals(req.getHeader("connection")),
                    // 过滤文件上传api
                    req -> {
                        String contentType = req.getContentType();
                        return contentType != null && contentType.contains("multipart");
                    },
                    // 过滤指定api
                    req -> {
                        String uri = req.getRequestURI().replaceAll("/+", "/");
                        // 把不合规范的多个 / 过滤了
                        return needIgnoreUris.contains(req.getMethod() + ' ' + uri);
                    }
            );
        }
    }

    /**
     * @apiNote 优先级最高
     * @apiNote 请求白名单，只要在里面就必然会记录操作日志
     */
    private Set<String> mustRecordUris() {
        Set<String> list = new LinkedHashSet<>();
        list.add("GET /");
        list.add("GET /miniapps");
        return list;
    }

    /**
     * @apiNote 优先级最低
     * @apiNote 建议必须过滤 token验证、轮询api，静态资源
     * @apiNote 建议过滤     常用、稳定、调用量大的api
     */
    private Set<String> needIgnoreUris() {
        Set<String> list = new LinkedHashSet<>();
        list.add("GET /miniapps");
        list.add("POST /miniapps");
        return list;
    }

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!log.isInfoEnabled()) {
            filterChain.doFilter(req, servletResponse);
            return;
        }
        // 不在请求白名单，才走过滤链
        String uri = req.getRequestURI().replaceAll("/+", "/");
        boolean isWhite = mustRecordUris.contains(req.getMethod() + ' ' + uri);
        if (!isWhite) {
            for (Predicate<HttpServletRequest> ignoreChain : ignoreChains) {
                if (ignoreChain.test(req)) {
                    filterChain.doFilter(req, servletResponse);
                    return;
                }
            }
        }

        ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(servletResponse);

        String params = genParamsStrQuery(req);
        String bodyStr;
        try (ServletInputStream inputStream = req.getInputStream()) {
            bodyStr = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        HttpServletRequestWrapper reqWrapper = new MyHttpServletRequestWrapper(req, bodyStr);

        long l = System.currentTimeMillis();
        String uuid = (String) req.getAttribute(SpringConstants.KEY_REQ_UID);
        log.info("({}) uri[{} {}{}] body[{}]", uuid, req.getMethod(), uri, params, bodyStr);

        filterChain.doFilter(reqWrapper, resp);

        String content = new String(resp.getContentAsByteArray(), StandardCharsets.UTF_8);
        resp.copyBodyToResponse();
        double time = (double) (System.currentTimeMillis() - l) / 1000;

        log.info("({}) cost[{}s] status[{}] resp[{}]", uuid, time, resp.getStatus(), StrKit.trunc(content, 256));
    }

    @SuppressWarnings("unused")
    private static String genParamsStrJSON(HttpServletRequest req) {
        Enumeration<String> parameterNames = req.getParameterNames();
        if (!parameterNames.hasMoreElements())
            return "{}";

        Map<String, String> m = new HashMap<>();
        while (parameterNames.hasMoreElements()) {
            String k = parameterNames.nextElement();
            String v = req.getParameter(k);
            m.put(k, v);
        }
        return JSON.toJSONString(m);
    }

    private static String genParamsStrQuery(HttpServletRequest req) {
        Enumeration<String> parameterNames = req.getParameterNames();
        if (!parameterNames.hasMoreElements())
            return "";

        List<String> list = new ArrayList<>();
        while (parameterNames.hasMoreElements()) {
            String k = parameterNames.nextElement();
            String v = req.getParameter(k);
            list.add(k + '=' + v);
        }
        return '?' + String.join("&", list);
    }

}
