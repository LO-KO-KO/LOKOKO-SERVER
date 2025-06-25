package com.lokoko.global.auth.line;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "line")
@Getter
@Setter
public class LineProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String baseUrl;
    private String scope;
}
