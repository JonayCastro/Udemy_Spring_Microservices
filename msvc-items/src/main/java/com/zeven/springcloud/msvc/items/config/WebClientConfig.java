package com.zeven.springcloud.msvc.items.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {


    /*@Bean
    @LoadBalanced
    WebClient.Builder webClient() {
        return WebClient.builder().baseUrl(baseUrl);
    }*/

    @Bean
    WebClient webClient(WebClient.Builder webClientBuilder,
                        ReactorLoadBalancerExchangeFilterFunction lbFunction,
                        @Value("${config.base-url.endpoint.msvc-products}") String baseUrl) {
        return webClientBuilder.baseUrl(baseUrl)
                .filter(lbFunction)
                .build();
    }
}
