package com.expensetracker.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();

        // Try to get user from Spring Security context
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;

        String userInfo = "Anonymous";
        if (principal != null && !principal.toString().equals("anonymousUser")) {
            userInfo = principal instanceof org.springframework.security.core.userdetails.UserDetails
                    ? ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername()
                    : principal.toString();
        }

        logger.info("Incoming Request → [{}] {} | User: {}", method, uri, userInfo);

        chain.doFilter(request, response);
    }
}
