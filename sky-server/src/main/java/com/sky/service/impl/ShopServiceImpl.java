package com.sky.service.impl;

import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {
    // Redis key 营业状态
    private static final String KEY = "SHOP_STATUS";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置营业状态
     */
    @Override
    public void setStatus(Integer status) {
        redisTemplate.opsForValue().set(KEY, status);
        log.info("已设置营业状态：{}", status == 1 ? "营业中" : "打烊中");
    }

    /**
     * 获取营业状态
     */
    @Override
    public Integer getStatus() {
        return (Integer) redisTemplate.opsForValue().get(KEY);
    }
}
