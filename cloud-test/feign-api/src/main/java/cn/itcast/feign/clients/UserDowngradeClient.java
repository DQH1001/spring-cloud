package cn.itcast.feign.clients;

import cn.itcast.feign.pojo.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDowngradeClient implements UserClient{
	@Override
	public User findById(Long id) {
		log.error("降级返回空User对象");
		return new User();
	}
}
