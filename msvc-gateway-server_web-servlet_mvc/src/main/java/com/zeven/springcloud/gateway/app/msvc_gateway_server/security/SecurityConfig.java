package com.zeven.springcloud.gateway.app.msvc_gateway_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    // Configuración para reactiva con WebFlux
    @Bean
    SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests( authz ->
                        authz
                                .requestMatchers("/authorized", "/logout").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/items", "/api/products", "/api/users").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}")
                                .hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/api/items/**", "/api/products/**", "/api/users/**").hasRole("ADMIN")
                                // Las tres líneas siguientes son equivalentes solo la anterior
                                /*.requestMatchers(HttpMethod.PUT, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/items", "/api/products", "/api/users").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")*/
                                .anyRequest().authenticated())
                .cors(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(login -> login.loginPage("/oauth2/authorization/client-app"))
                .oauth2Client(withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> {
                            jwt.jwtAuthenticationConverter(new Converter<Jwt, AbstractAuthenticationToken>() {

                                @Override
                                public AbstractAuthenticationToken convert(Jwt source) {
                                    Collection<String> roles = source.getClaimAsStringList("roles");
                                    Collection<GrantedAuthority> authorities = roles.stream()
                                            .map(SimpleGrantedAuthority::new)
                                            .collect(Collectors.toList());
                                    return new JwtAuthenticationToken(source, authorities);
                                }
                            });
                        }
                ))
                .build();
    }

    // Configuración para no reactiva
    /*@Bean
    SecurityFilterChain securityWebClientFilterChain(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests( authz ->
                        authz
                                .requestMatchers("/authorized", "/logout").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/items", "/api/products", "/api/users").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}")
                                .hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/api/items/**", "/api/products/**", "/api/users/**").hasRole("ADMIN")
                                // Las tres líneas siguientes son equivalentes solo la anterior
                                *//*.requestMatchers(HttpMethod.PUT, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/items", "/api/products", "/api/users").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")*//*
                                .anyRequest().authenticated())
                .cors(AbstractHttpConfigurer::disable)
                .oauth2Login(withDefaults())
                .oauth2Client(withDefaults())
                .oauth2ResourceServer(withDefaults())
                .build();
    }*/
}
