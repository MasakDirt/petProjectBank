package com.pet.project.filter;

import com.pet.project.service.CustomerService;
import com.pet.project.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
@AllArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomerService customerService;

    @Override
    public void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain) throws ServletException, IOException {
        if (hasAuthorizationBearer(httpRequest)) {
            String token = getAccessToken(httpRequest);
            if (jwtUtils.validateJwtToken(token)) {
                setAuthenticationContext(token, httpRequest);
            }
        }
        filterChain.doFilter(httpRequest, httpResponse);
    }

    private boolean hasAuthorizationBearer(HttpServletRequest httpRequest) {
        String header = httpRequest.getHeader("Authorization");
        return Objects.nonNull(header) && header.startsWith("Bearer ");
    }

    private String getAccessToken(HttpServletRequest httpRequest) {
        String header = httpRequest.getHeader("Authorization");
        return header.substring(7);
    }

    private void setAuthenticationContext(String token, HttpServletRequest httpRequest) {
        UsernamePasswordAuthenticationToken
                authentication = geUsernamePasswordAuthenticationToken(token);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UsernamePasswordAuthenticationToken geUsernamePasswordAuthenticationToken(String token) {
        var userDetails = getUserDetails(token);
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                null, userDetails.getAuthorities());
    }

    private UserDetails getUserDetails(String token) {
        return customerService.loadUserByUsername(jwtUtils.getSubject(token));
    }
}
