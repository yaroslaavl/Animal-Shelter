package org.shelter.app.config;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.shelter.app.dto.UserCreateDto;
import org.shelter.app.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.Set;

import static org.shelter.app.database.entity.enums.Role.ADMIN;
import static org.shelter.app.database.entity.enums.Role.AUTHORISED_USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Resource
    UserDetailsService userDetailsService;

    @Bean
    @SneakyThrows
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .successHandler(new CustomAuthenticationSuccessHandler())
                )

                .requestCache(requestCache -> requestCache
                        .requestCache(new HttpSessionRequestCache())
                )

                .authorizeHttpRequests(url -> url
                        .requestMatchers("/login", "/users/registration", "/v3/api-docs/**", "/swagger-ui/**", "/firstPage", "/pets", "/company-info", "/blog", "/api/pets", "/api/users/**").permitAll()
                        .requestMatchers("/user/settings/**", "/pet/booking/**", "/pet/bookings/**", "/user/notifications", "/api/user/**").authenticated()
                        .requestMatchers("/admin/**").hasAuthority(ADMIN.getAuthority())
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                )

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            accessDeniedException.printStackTrace();
                            response.sendRedirect("/forbidden-error");
                        })
                )

                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login")
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .oidcUserService(oidcUserService())
                        )
                );

        return httpSecurity.build();
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService(){
        return userRequest -> {
            String email = userRequest.getIdToken().getClaim("email");

            if (userService.findByUsername(email).isEmpty()) {
                userService.create(UserCreateDto.createNewUser(email, "User", "User", AUTHORISED_USER,  true,null, LocalDateTime.now()));
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
