//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.spectra.sports.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Map<String, String[]> allowedUrls = new HashMap();
    @Autowired
    private AuthTokenFilter authTokenFilter;

    static {
        allowedUrls.put(HttpMethod.POST.name(), new String[]{
                "/user/signup", "/user/sign-in", "/user/upload", "/user/validate-otp", "/user/verification-email/**", "/user/reset-password"
        });
        allowedUrls.put(HttpMethod.GET.name(), new String[]{
                "/user/verify/**", "/user/email-otp/**", "/favicon.ico", "/user/download"
        });
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, allowedUrls.get(HttpMethod.POST.name()))
            .permitAll()
            .antMatchers(HttpMethod.GET, allowedUrls.get(HttpMethod.GET.name()))
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .httpBasic()
            .disable()
            .addFilterAfter(this.authTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
