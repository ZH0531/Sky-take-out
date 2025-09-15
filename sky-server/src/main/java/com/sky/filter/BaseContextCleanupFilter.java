package com.sky.filter;

import com.sky.context.BaseContext;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@Component
@WebFilter("/*")
public class BaseContextCleanupFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // 继续执行请求
            chain.doFilter(request, response);
        } finally {
            // 确保在请求结束后清理ThreadLocal
            BaseContext.removeCurrentId();
        }
    }
}
