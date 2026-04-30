package com.michelet.common.auth.webmvc.config;

import com.michelet.common.auth.webmvc.aop.AuthorizationAspect;
import com.michelet.common.auth.webmvc.web.UserContextInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
public class AuthWebMvcAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public UserContextInterceptor userContextInterceptor(){
        return new UserContextInterceptor();
    }
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationAspect authorizationAspect(){
        return new AuthorizationAspect();
    }

    @Bean
    public WebMvcConfigurer authWebMvcConfigurer(UserContextInterceptor userContextInterceptor){
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(userContextInterceptor)
                        .order(Integer.MIN_VALUE);
            }
        };
    }
}
