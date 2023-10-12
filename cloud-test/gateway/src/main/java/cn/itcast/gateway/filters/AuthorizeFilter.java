package cn.itcast.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(-1)
@Component
public class AuthorizeFilter implements GlobalFilter {
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// 1.获取请求参数
		ServerHttpRequest request = exchange.getRequest();
		MultiValueMap<String, String> queryParams = request.getQueryParams();

		// 2.获取authorization参数
		String authorization = queryParams.getFirst("authorization");

		// 3.判断是否等于admin
		if ("admin".equals(authorization)) {
			return chain.filter(exchange);
		}

		// 4.设置状态码
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

		// 5.重置状态返回
		return exchange.getResponse().setComplete();
	}
}
