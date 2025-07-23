package com.zeven.springcloud.msvc.oauth.services;


import com.zeven.springcloud.msvc.oauth.models.Role;
import com.zeven.springcloud.msvc.oauth.models.User;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private WebClient client;

    @Autowired
    private Tracer tracer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Ingresando al proceso de login UserService: loadUserByUsername con {}", username);
        Map<String, String> params = new HashMap<>();
        params.put("username", username);


        try {
            User user = client.get().uri("/username/{username}", params)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();

            List<GrantedAuthority> roles = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());

            logger.info("Ha iniciado sesión con exito: {}", user);

            Objects.requireNonNull(tracer.currentSpan()).tag("success.login.message", "Ha iniciado sesión con exito para el usuario: " + user.getUserName());


            return new org.springframework.security.core.userdetails.User(
                    user.getUserName(), user.getPassword(), user.isEnabled(), true, true, true, roles
            );
        } catch (WebClientResponseException e){
            String error = "Error en login, no existe el user " + username + " en el sistema";
            logger.error(error);
            Objects.requireNonNull(tracer.currentSpan()).tag("error.login.message", error + " : " + e.getMessage());
            throw new UsernameNotFoundException(error);
        }
    }
}
