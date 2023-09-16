package com.spectra.sports.config;

import com.spectra.sports.helper.JwtHelper;
import com.spectra.sports.helper.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Configuration
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private UserDetailsService userDetailsService;

    private static final Map<String, List<String>> whiteListedUrls;

    static {
        whiteListedUrls = Map.of(
            RequestMethod.POST.name(), List.of("signup", "sign-in", "upload", "/validate-otp", "/user/verification-email", "/reset-password"),
            RequestMethod.GET.name(), List.of("/user/verify", "/email-otp", "/favicon.ico", "download")
        );
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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var token = request.getHeader("Authorization");
        var userNotAuthenticated = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not Authenticated");
        if (token == null) {
            throw userNotAuthenticated;
        }

        try {
            var userDto = this.jwtHelper.parseToken(token);
            var userDetails = userDetailsService.loadUserByUsername(userDto.email());
            if (Objects.isNull(userDetails)) {
                throw userNotAuthenticated;
            }
            UserContextHolder.setCurrentUser(userDto);
            var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails((new WebAuthenticationDetailsSource()).buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } catch (Exception e) {
            throw userNotAuthenticated;
        }

        filterChain.doFilter(request, response);
    }
}
