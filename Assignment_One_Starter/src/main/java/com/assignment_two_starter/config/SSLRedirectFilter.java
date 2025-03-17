package com.assignment_two_starter.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class SSLRedirectFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (!req.isSecure()) {
            String httpsUrl = "https://" + req.getServerName() + ":8070" + req.getRequestURI();
            if (req.getQueryString() != null) {
                httpsUrl += "?" + req.getQueryString();
            }
            res.sendRedirect(httpsUrl);
            return;
        }

        chain.doFilter(request, response);
    }
}
