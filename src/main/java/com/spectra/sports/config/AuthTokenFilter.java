package com.spectra.sports.config;

import com.spectra.sports.helper.JwtHelper;
import com.spectra.sports.helper.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@Configuration
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtHelper jwtHelper;

    private static Map<String, List<String>> whiteListedUrls;

    static {
        whiteListedUrls = Map.of(
            RequestMethod.POST.name(), List.of("signup", "sign-in", "upload", "/validate-otp", "/user/verification-email", "/reset-password"),
            RequestMethod.GET.name(), List.of("/user/verify", "/email-otp", "/favicon.ico", "download")
        );
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = request.getHeader("Authorization");
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not Authenticated");
        }

        try {
            var userDto = this.jwtHelper.parseToken(token);
            UserContextHolder.setCurrentUser(userDto);
            var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDto.email(), null, List.of());
            usernamePasswordAuthenticationToken.setDetails((new WebAuthenticationDetailsSource()).buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } catch (Exception var7) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, var7.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    protected boolean shouldNotFilter(HttpServletRequest request) {
        var pathInfo = request.getRequestURI();
        if (nonNull(pathInfo)) {
            var requestMethod = request.getMethod();
            var urls = whiteListedUrls.get(requestMethod);
            return nonNull(urls) && urls.stream().anyMatch(pathInfo::contains);
        }

        return false;
    }
}
