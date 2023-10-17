package cn.itcast.hotel.controller;

import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/hotel")
public class HotelController {
	@Autowired
	private IHotelService iHotelService;

	@PostMapping("/list")
	PageResult search(@RequestBody RequestParams params){
		return iHotelService.search(params);
	}

	@PostMapping("/filters")
	Map<String, List<String>> filters(@RequestBody RequestParams params){
		return iHotelService.filters(params);
	}

	@GetMapping("/suggestion")
	List<String> getSuggestions(@RequestParam String key){
		return iHotelService.getSuggestions(key);
	}
}
