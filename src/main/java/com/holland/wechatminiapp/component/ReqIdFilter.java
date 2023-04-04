package com.holland.wechatminiapp.component;

import com.holland.wechatminiapp.constants.SpringConstants;
import com.holland.wechatminiapp.kit.UUID;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(100)
@Component
public class ReqIdFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setAttribute(SpringConstants.KEY_REQ_UID, UUID.generate(8));
        super.doFilter(request, response, chain);
    }
}
