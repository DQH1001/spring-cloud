package com.heima.item.canal;

import com.github.benmanes.caffeine.cache.Cache;
import com.heima.item.config.RedisHandler;
import com.heima.item.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@CanalTable("tb_item")
@Component
public class ItemHandler implements EntryHandler<Item> {
    @Autowired
    RedisHandler redisHandler;

    @Autowired
    Cache<Long, Item> itemCache;

    @Override
    public void insert(Item item) {
        // 插入redis缓存
        redisHandler.saveItem(item);
        // 插入jvm缓存
        itemCache.put(item.getId(), item);
    }

    @Override
    public void update(Item before, Item after) {
        // 更新redis缓存
        redisHandler.saveItem(after);
        // 更新jvm缓存
        itemCache.put(after.getId(), after);
    }

    @Override
    public void delete(Item item) {
        // 删除缓存
        redisHandler.deleteItem(item.getId());
        // 删除jvm缓存
        itemCache.invalidate(item.getId());
    }
}
