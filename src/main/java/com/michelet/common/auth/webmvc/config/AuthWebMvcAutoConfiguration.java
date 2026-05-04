package com.michelet.common.auth.webmvc.config;

import com.michelet.common.auth.webmvc.aop.AuthorizationAspect;
import com.michelet.common.auth.webmvc.filter.InternalAuthFilter;
import com.michelet.common.auth.webmvc.interceptor.UserContextInterceptor;
import com.michelet.common.auth.webmvc.internal.InternalTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@EnableConfigurationProperties(InternalAuthProperties.class)
public class AuthWebMvcAutoConfiguration {

    private static final String INTERNAL_PREFIX = "/internal/";

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
    @ConditionalOnMissingBean
    public InternalTokenProvider internalTokenProvider(InternalAuthProperties internalAuthProperties){
        return new InternalTokenProvider(internalAuthProperties.getSecret());
    }


    @Bean
    @ConditionalOnMissingBean
    public InternalAuthFilter internalAuthFilter(
            InternalTokenProvider internalTokenProvider,
            @Value("${spring.application.name}") String applicationName
    ){
        return new InternalAuthFilter(internalTokenProvider, applicationName);
    }

    @Bean
    public FilterRegistrationBean<InternalAuthFilter> internalAuthFilterFilterRegistrationBean(
            InternalAuthFilter internalAuthFilter
    ){
        FilterRegistrationBean<InternalAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(internalAuthFilter);
        registrationBean.setOrder(Integer.MIN_VALUE);
        registrationBean.addUrlPatterns(INTERNAL_PREFIX +"*");
        return registrationBean;
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
