package com.zeven.springcloud.gateway.app.msvc_gateway_server.filters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class SampleGlobalFiter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFiter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String headerTokenName = "token";
        String headerTokenValue = "abcdefg";

        logger.info("Ejecutando filtro PRE request");

        ServerHttpRequest mutateRequest = exchange.getRequest().mutate().headers(header ->
            header.add(headerTokenName, headerTokenValue)).build();

        ServerWebExchange mutatedExchanged = exchange.mutate().request(mutateRequest).build();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            logger.info("Ejecutando filtro POST");
            //String token = mutatedExchanged.getRequest().getHeaders().getFirst(headerTokenName);

            /*if (token != null) {
                String msg = "Token: " + token;
                exchange.getResponse().getHeaders().add(headerTokenName, token);
                logger.info(msg);
            }*/

            Optional.ofNullable(mutatedExchanged.getRequest().getHeaders().getFirst(headerTokenName)).ifPresent(value -> {
                String msg = "Token: " + value;
                        logger.info(msg);
                        exchange.getResponse().getHeaders().add(headerTokenName, value);
            });

            exchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "red").build());
            //exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        }));
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
