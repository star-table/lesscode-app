package com.polaris.lesscode.app.interceptor;

import org.springframework.stereotype.Component;

import com.polaris.lesscode.interceptor.RequestContextInterceptor;

import feign.RequestInterceptor;

@Component
public class AppRequestContextInterceptor extends RequestContextInterceptor implements RequestInterceptor{

}
