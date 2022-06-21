package com.spectra.sports.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private AuthTokenFilter authTokenFilter;

    private static final Map<String, String[]> allowedUrls = new HashMap();

    static {
        allowedUrls.put(HttpMethod.POST.name(), new String[]{
                "/user/signup", "/user/sign-in", "/user/upload", "/user/validate-otp", "/user/verification-email/**", "/user/reset-password"
        });
        allowedUrls.put(HttpMethod.GET.name(), new String[]{
                "/user/verify/**", "/user/email-otp/**", "/favicon.ico", "/user/download"
        });
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth
                    .antMatchers(HttpMethod.POST, allowedUrls.get(HttpMethod.POST.name())).permitAll()
                    .antMatchers(HttpMethod.GET, allowedUrls.get(HttpMethod.GET.name())).permitAll())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
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
                .antMatchers(HttpMethod.POST, allowedUrls.get(HttpMethod.POST.name()))
                .antMatchers(HttpMethod.GET, allowedUrls.get(HttpMethod.GET.name()));
    }
}
