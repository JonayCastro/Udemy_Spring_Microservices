package com.zeven.springcloud.gateway.app.msvc_gateway_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@SpringBootApplication
public class MsvcGatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcGatewayServerApplication.class, args);
	}

	@Bean
	RouterFunction<ServerResponse> routerConfig(){
		return route("msvc-products")
				.route(path("/api/products/**"), http())
				.filter((request, next) -> {
					ServerRequest requestModified = ServerRequest.from(request)
							.header("message-request", "algún mensaje")
							.build();
					ServerResponse response = next.handle(requestModified);
					response.headers().add("message-response", "algún mensaje para la respuesta");
					return response;
				})
				.filter(lb("msvc-products"))
				.filter(
						circuitBreaker(config ->
								config
								.setId("products")
								.setStatusCodes("500")
								.setFallbackPath("forward:/api/items/5")))
				.before(stripPrefix(2))
				.build();

	}

}
