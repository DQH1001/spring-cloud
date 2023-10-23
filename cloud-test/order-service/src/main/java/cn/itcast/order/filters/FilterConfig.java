package cn.itcast.order.filters;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.Collections;

@Configuration
public class FilterConfig {
	@Bean
	public FilterRegistrationBean sentinelFilterRegistration(){
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new CommonFilter());
		// 入口资源关闭聚合
		registration.addInitParameter(CommonFilter.WEB_CONTEXT_UNIFY, "false");
		registration.setUrlPatterns(Collections.singleton("/*"));
		registration.setName("sentinelFilter");
		registration.setOrder(1);
		return registration;
	}
}
