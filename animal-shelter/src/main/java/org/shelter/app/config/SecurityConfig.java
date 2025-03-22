package org.shelter.app.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.shelter.app.dto.UserCreateDto;
import org.shelter.app.filter.ApiKeyFilter;
import org.shelter.app.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.Set;

import static org.shelter.app.database.entity.enums.Role.VERIFIED_USER;

@EnableAsync
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final ApiKeyFilter apiKeyFilter;

    @Bean
    @SneakyThrows
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(url -> url
                        .requestMatchers(
                                "/actuator",
                                "/actuator/**",
                                "/api/user/login",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/api/user/create",
                                "/api/user/activate",
                                "/error").permitAll()
                        .requestMatchers(
                                "/api/user/settings/account/resend-activation-code",
                                "/api/user/delete-account",
                                "/api/user/resend-activation",
                                "/api/user/update"
                        ).authenticated()
                )
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2Login -> oauth2Login
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .oidcUserService(oidcUserService())
                        )
                );

        return httpSecurity.build();
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<?, ?> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        return template;
    }

    @Bean
    @SneakyThrows
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService(){
        return userRequest -> {
            String email = userRequest.getIdToken().getClaim("email");

            if (userService.findByUsername(email).isEmpty()) {
                userService.create(UserCreateDto.createNewUser(email, "User", "User", VERIFIED_USER,  true,null, LocalDateTime.now()));
            }
            UserDetails userDetails = userService.loadUserByUsername(email);
            DefaultOidcUser defaultOidcUser = new DefaultOidcUser(userDetails.getAuthorities(), userRequest.getIdToken());
            Set<Method> methods = Set.of(UserDetails.class.getMethods());
            return (OidcUser) Proxy.newProxyInstance(
                    SecurityConfig.class.getClassLoader(),
                    new Class[]{UserDetails.class, OidcUser.class},
                    (proxy, method, args) -> methods.contains(method)
                            ? method.invoke(userDetails,args)
                            : method.invoke(defaultOidcUser,args));
        };
    }

}
