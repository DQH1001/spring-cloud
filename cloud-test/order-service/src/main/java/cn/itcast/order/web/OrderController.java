package cn.itcast.order.web;

import cn.itcast.order.pojo.Order;
import cn.itcast.order.service.OrderService;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@SentinelResource("/order/id")
	@GetMapping("{orderId}")
	public Order queryOrderByUserId(@PathVariable("orderId") Long orderId) {
		// 根据id查询订单并返回
		return orderService.queryOrderById(orderId);
	}

	@GetMapping("query")
	public String queryOrder(HttpServletRequest request) {
		orderService.queryGoods();
		System.out.println("origin:" + request.getHeader("origin"));
		return "查询订单成功";
	}

	@GetMapping("update")
	public String updateOrder() {
		return "更新订单成功";
	}

	@GetMapping("save")
	public String saveOrder() {
		orderService.queryGoods();
		return "新增订单成功";
	}
}
