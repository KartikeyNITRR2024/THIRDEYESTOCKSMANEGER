package com.thirdeye.stocksmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.thirdeye.stocksmanager.interceptors.AuthorizationInterceptor;
import com.thirdeye.stocksmanager.utils.AllMicroservicesData;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;
    
    @Autowired
	AllMicroservicesData allMicroservicesData;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/api/stocksbatch/"+allMicroservicesData.current.getMicroserviceUniqueId()+"/allstocks","/api/stockslists/**");
    }
}

