package com.zeven.springcloud.gateway.app.msvc_gateway_server.filters.factory;


public class SampleCookieGatewayFilterFactory {

}

// FILTROS REACTIVOS


/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class SampleCookieGatewayFilterFactory extends AbstractGatewayFilterFactory<SampleCookieGatewayFilterFactory.ConfigurationCookie> {

    private final Logger logger = LoggerFactory.getLogger(SampleCookieGatewayFilterFactory.class);

    public SampleCookieGatewayFilterFactory() {
        super(ConfigurationCookie.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("message", "name", "value");
    }

    @Override
    public GatewayFilter apply(ConfigurationCookie configurationCookie) {

        return new OrderedGatewayFilter((exchange, chain) -> {
            String messagePre = "Ejecutando Pre gateway filter factory: " + configurationCookie.message;
            logger.info(messagePre);

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {

                Optional.ofNullable(configurationCookie.value).ifPresent(cookie -> {
                    exchange.getResponse().addCookie(ResponseCookie.from(configurationCookie.name, cookie).build());
                });

                String messagePost = "Ejecutando Post gateway filter factory: " + configurationCookie.message;
                logger.info(messagePost);
            }));
        }, 100);
    }

    public static class ConfigurationCookie {
        private String name;
        private String value;
        private String message;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}*/
