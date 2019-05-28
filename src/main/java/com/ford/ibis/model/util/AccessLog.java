package com.ford.ibis.model.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccessLog {
    private String srcIp;
    private String xForwardedFor;
    private String endUserIp;
    private String httpMethod;
    private String uri;
    private String protocol;
    private Integer status;
    private Long responseTime;
    private String userAgent;
    private String contentType;
    private String referer;
    private String geoLocation;
}
