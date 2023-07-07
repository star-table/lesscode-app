package com.polaris.lesscode.app.filter;

import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.polaris.lesscode.filter.RequestLoggerFilter;

@Component
@WebFilter(filterName = "appRequestLoggerFilter", urlPatterns = "/*")
@Order(2)
public class AppRequestLoggerFilter extends RequestLoggerFilter implements Filter{

}
