package org.shelter.app.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.shelter.app.filter.ApiKeyFilter;
import org.shelter.app.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableAsync
@Configuration
@EnableScheduling
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
                                "/api/pet/available",
                                "/api/pet/image",
                                "/api/user/image",
                                "/api/user/find/*",
                                "/api/pet/id/*",
                                "/error").permitAll()

                        .requestMatchers(
                                "/api/user/settings/account/resend-activation-code",
                                "/api/user/delete-account",
                                "/api/user/resend-activation",
                                "/api/user/update",
                                "/api/notification/all-by-user",
                                "/api/adoption-request/all",
                                "/api/user/upload-image",
                                "/api/user/reset-password").authenticated()

                        .requestMatchers(
                                "/api/adoption-request/respond/*",
                                "/api/notification/all",
                                "/api/adoption-request/id/*",
                                "/api/user/set-vetRole").hasRole("ADMIN")

                        .requestMatchers(
                                "/api/species/add",
                                "/api/species/delete/*",
                                "/api/species/dynamic-search",

                                "/api/pet/addNew",
                                "/api/pet/add-to-adoption-list/*",

                                "/api/medical-record/all/*",
                                "/api/pet/change-status/*",
                                "/api/pet/upload-image/*",
                                "/api/pet/all-statuses").hasAnyRole("ADMIN", "VET")

                        .requestMatchers(
                                "/api/medical-record/create").hasAnyRole( "VET")

                        .requestMatchers(
                                "/api/adoption-request/send").hasAnyRole("VERIFIED_USER", "VET", "ADMIN")
                )
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);

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
}
