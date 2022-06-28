package com.spectra.sports.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private AuthTokenFilter authTokenFilter;

    private static final Map<String, String[]> allowedUrls = new HashMap();

    static {
        allowedUrls.put(POST.name(), new String[]{
                "/user/signup", "/user/sign-in", "/user/upload", "/user/validate-otp", "/user/verification-email/**", "/user/reset-password"
        });
        allowedUrls.put(GET.name(), new String[]{
                "/user/verify/**", "/user/email-otp/**", "/favicon.ico", "/user/download"
        });
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable()
            .authorizeHttpRequests(auth ->
                auth.antMatchers(POST, allowedUrls.get(POST.name())).permitAll()
                    .antMatchers(GET, allowedUrls.get(GET.name())).permitAll())
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
            .accessDeniedHandler((req, res, exception) -> res.sendError(UNAUTHORIZED.value(), exception.getMessage()))
            .and()
            .httpBasic()
            .disable()
            .addFilterAfter(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web
                .ignoring()
                .antMatchers(POST, allowedUrls.get(POST.name()))
                .antMatchers(GET, allowedUrls.get(GET.name()));
    }
}
