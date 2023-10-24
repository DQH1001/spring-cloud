package cn.itcast.order.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class HeaderOriginParser implements RequestOriginParser {
	@Override
	public String parseOrigin(HttpServletRequest httpServletRequest) {
		String origin = httpServletRequest.getHeader("origin");
		if (StringUtils.isEmpty(origin)) {
			return "blank";
		}

		return origin;
	}
}
