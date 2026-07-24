package com.aloha.linaiagent.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Wraps request bodies so they can be read more than once.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RepeatableRequestFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (HttpMethod.GET.matches(request.getMethod())
            || HttpMethod.HEAD.matches(request.getMethod())
            || HttpMethod.OPTIONS.matches(request.getMethod())
            || HttpMethod.TRACE.matches(request.getMethod())) {
            return true;
        }
        long contentLength = request.getContentLengthLong();
        if (contentLength == 0) {
            return true;
        }
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        String normalized = contentType.toLowerCase(Locale.ROOT);
        return normalized.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)
            || normalized.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        filterChain.doFilter(new CachedBodyHttpServletRequest(request), response);
    }
}
