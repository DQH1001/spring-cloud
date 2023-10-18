package cn.itcast.hotel.listener;

import cn.itcast.hotel.constants.MqConstants;
import cn.itcast.hotel.service.IHotelService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HotelListener {
	@Autowired
	private IHotelService iHotelService;

	@RabbitListener(bindings = @QueueBinding(
			exchange = @Exchange(value = MqConstants.HOTEL_EXCHANGE,
					type = ExchangeTypes.DIRECT),
			value = @Queue(name = MqConstants.HOTEL_INSERT_QUEUE),
			key = MqConstants.HOTEL_INSERT_KEY)
	)
	public void insertListener(Long id) {
		System.out.println("监听到新增消息：" + id);
		iHotelService.insertById(id);
	}

	@RabbitListener(bindings = @QueueBinding(
			exchange = @Exchange(value = MqConstants.HOTEL_EXCHANGE,
					type = ExchangeTypes.DIRECT),
			value = @Queue(name = MqConstants.HOTEL_DELETE_QUEUE),
			key = MqConstants.HOTEL_DELETE_KEY)
	)
	public void deleteListener(Long id) {
		System.out.println("监听到删除消息：" + id);
		iHotelService.deleteById(id);
	}
}
