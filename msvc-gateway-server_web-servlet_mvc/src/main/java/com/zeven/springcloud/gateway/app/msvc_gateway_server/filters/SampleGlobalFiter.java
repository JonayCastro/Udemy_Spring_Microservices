package com.zeven.springcloud.gateway.app.msvc_gateway_server.filters;

import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SampleGlobalFiter implements Filter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFiter.class);


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        logger.info("LLamada filtro SampleGlobalFiter::doFilter");

        chain.doFilter(request, response);
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
