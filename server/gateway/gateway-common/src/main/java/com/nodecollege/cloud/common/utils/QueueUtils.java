package com.nodecollege.cloud.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 队列工具类
 * redis 列表实现
 * 后期支持 其他mq
 *
 * @author LC
 * @date 2020/9/25 20:09
 */
@Component
public class QueueUtils {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加到队列
     */
    public void pushTask(String key, Object object) {
        if (object != null) {
            String objects = JSONObject.toJSONString(object);
            redisTemplate.opsForList().leftPush(key, objects);
        }
    }

    /**
     * 查询队列大小
     */
    public Long size(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 从队列中移出
     */
    public <T> T popTask(String key, Class<T> tClass) {
        Object object = redisTemplate.opsForList().rightPop(key);
        if (object != null) {
            return JSON.parseObject((String) object, tClass);
        } else {
            return null;
        }
    }
}
