package com.michelet.common.auth.webmvc.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "internal.auth")
public class InternalAuthProperties {
    private String secret;
    private String audience;

    @PostConstruct
    public void validate(){
        if(secret== null || secret.isBlank())
            throw new IllegalArgumentException("internal.auth.secret must not be blank.");

        if(audience == null || audience.isBlank())
            throw new IllegalArgumentException("internal.auth.audience must not be blank.");
    }

    public String getSecret(){
        return secret;
    }
    public void setSecret(String secret){
        this.secret = secret;
    }
    public String getAudience(){
        return audience;
    }
    public void setAudience(String audience){
        this.audience = audience;
    }
}
