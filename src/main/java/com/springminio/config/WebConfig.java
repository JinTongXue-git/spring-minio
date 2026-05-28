package com.springminio.config;

import com.springminio.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册自建拦截器{@link com.springminio.interceptor.LogInterceptor}
 * @author 刘老爷
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器，拦截所有请求
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .addPathPatterns("/**");

    }
}