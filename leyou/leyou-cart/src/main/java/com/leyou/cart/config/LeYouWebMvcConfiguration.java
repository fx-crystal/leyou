package com.leyou.cart.config;

import com.leyou.cart.interceptor.LgoinInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LeYouWebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private LgoinInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //"/**"拦截所有路径
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    }
}
