package com.michelet.common.auth.webmvc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "internal.auth")
public class InternalAuthProperties {
    private String secret;
    private String audience;

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
