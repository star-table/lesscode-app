package com.polaris.lesscode.app.filter;

import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.polaris.lesscode.filter.RequestContextFilter;

@Component
@WebFilter(filterName = "appRequestContextFilter", urlPatterns = "/*")
@Order(1)
public class AppRequestContextFilter extends RequestContextFilter implements Filter{

}
