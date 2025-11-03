package com.unsl.sgeu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class webconfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Autowired
    private Environment env;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login", "/css/**", "/js/**", "/images/**", "/webjars/**", "/qr-codes/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String qrStoragePath = env.getProperty("qr.storage.path", "/opt/sgeu/qr-codes");
        
        if (!qrStoragePath.endsWith("/")) {
            qrStoragePath = qrStoragePath + "/";
        } 
        registry.addResourceHandler("/qr-codes/**")
                .addResourceLocations("file:" + qrStoragePath);
    }
}
