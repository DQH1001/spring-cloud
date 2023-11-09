package com.heima.item.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.item.pojo.Item;
import com.heima.item.pojo.ItemStock;
import com.heima.item.service.impl.ItemService;
import com.heima.item.service.impl.ItemStockService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisHandler implements InitializingBean {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemStockService itemStockService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Item> itemList = itemService.list();
        for (Item item : itemList) {
            String json = MAPPER.writeValueAsString(item);
            redisTemplate.opsForValue().set("item:id:" + item.getId(), json);
        }

        List<ItemStock> itemStocks = itemStockService.list();
        for (ItemStock stock : itemStocks) {
            String json = MAPPER.writeValueAsString(stock);
            redisTemplate.opsForValue().set("item:stock:id:" + stock.getId(), json);
        }

    }

    public void saveItem(Item item) {
        String json = null;
        try {
            json = MAPPER.writeValueAsString(item);
            redisTemplate.opsForValue().set("item:id:" + item.getId(), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteItem(Long itemId) {
        redisTemplate.delete("item:id:" + itemId);
    }
}
