package com.nthieu.productservice.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class CustomAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String username = request.getHeader("Username");

        if (StringUtils.isNotEmpty(username)) {
            // Create our Authentication and let Spring know about it
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,
                    StringUtils.EMPTY, Arrays.asList(authority));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
