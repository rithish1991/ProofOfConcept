package com.ford.ibis.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ford.ibis.model.util.AccessLog;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;

@Slf4j
@Component
public class LogRequestFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID = "requestId";
    public static final String X_REQUEST_ID = "X-Request-Id";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String USER_AGENT = "User-Agent";
    public static final String TRUE_CLIENT_IP = "True-Client-IP";
    public static final String REFERER = "Referer";
    public static final String CONTENT_LENGTH = "Content-Length";


    @Override
    public final void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {

        setMdcValues(request);
        long start = System.currentTimeMillis();

        filterChain.doFilter(request, response);
        AccessLog accessLog = AccessLog.builder()
                .srcIp(request.getRemoteAddr())
                .xForwardedFor(request.getHeader(X_FORWARDED_FOR))
                .endUserIp(request.getHeader(TRUE_CLIENT_IP))
                .httpMethod(request.getMethod())
                .uri(request.getRequestURI())
                .protocol(request.getProtocol())
                .status(response.getStatus())
                .responseTime(System.currentTimeMillis() - start)
                .userAgent(request.getHeader(USER_AGENT))
                .contentType(request.getContentType())
                .referer(request.getHeader(REFERER)).build();
        log.info(Markers.appendFields(accessLog), null);

    }

    private void setMdcValues(final HttpServletRequest request) throws UnsupportedEncodingException {
        final Optional<String> uuid = Optional.ofNullable(request.getHeader(X_REQUEST_ID));
        MDC.put(REQUEST_ID, uuid.orElse(UUID.randomUUID().toString()));
    }
}
