package com.haihun.filter.config;

import com.haihun.filter.ParameterFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kaiser·von·d
 * @version 2018/5/4
 */
@Configuration
public class FilterConfig {
    // 解决拦截器无法依赖注入
    @Bean
    public ParameterFilter parameterFilter() {
        return new ParameterFilter();
    }

    /**
     * 注册拦截器
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean();

        bean.setFilter(parameterFilter());
        bean.addUrlPatterns("/*");//设置过滤路径，/*所有路径
        //bean.addInitParameter("name", "alue");//添加默认参数
        bean.setName("paramFilter");//名称
        bean.setOrder(1);//设置优先级
        return bean;
    }
}
