package com.nodecollege.cloud.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 *
 * @author LC
 * @date 19:16 2019/12/2
 **/
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * set 默认有效期30分钟
     *
     * @param redisKey
     * @param object
     */
    public void set(String redisKey, Object object) {
        // 默认有效期30分钟
        set(redisKey, object, 24 * 60 * 60);
    }

    /**
     * set
     *
     * @param redisKey key
     * @param object   value
     * @param seconds  超时时间
     */
    public void set(String redisKey, Object object, long seconds) {
        if (object != null) {
            String objects = JSONObject.toJSONString(object);
            if (seconds != -1) {
                redisTemplate.opsForValue().set(redisKey, objects, seconds, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(redisKey, objects);
            }
        }
    }

    /**
     * get方法
     *
     * @param redisKey redisKey
     * @param tClass   class
     */
    public <T> T get(String redisKey, Class<T> tClass) {
        String object = get(redisKey);
        if (object != null) {
            return JSON.parseObject(object, tClass);
        }
        return null;
    }

    /**
     * 获取列表
     */
    public <T> List getList(String redisKey, Class<T> tClass) {
        String object = get(redisKey);
        if (object != null) {
            List list = JSON.parseObject(object, List.class);
            if (list != null && !list.isEmpty()) {
                List<T> result = new ArrayList<>(list.size());
                for (int i = 0; i < list.size(); i++) {
                    T item = JSON.parseObject(JSONObject.toJSONString(list.get(i)), tClass);
                    result.add(item);
                }
                return result;
            }
        }
        return null;
    }

    /**
     * get方法
     *
     * @param redisKey
     */
    public String get(String redisKey) {
        Object object = redisTemplate.opsForValue().get(redisKey);
        if (object != null) {
            String objects = (String) object;
            return objects;
        }
        return null;
    }

    /**
     * 累加数据
     * @param redisKey
     * @param v2
     * @return
     */
    public Long increment(String redisKey, long v2){
        return redisTemplate.opsForValue().increment(redisKey, v2);
    }

    /**
     * 获取剩余有效时间
     *
     * @param key
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 模糊查询
     *
     * @param keys
     * @return
     */
    public Set<String> keys(String keys) {
        return redisTemplate.keys(keys);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除缓存
     *
     * @param keys
     */
    public void delete(Set<String> keys) {
        redisTemplate.delete(keys);
    }
}
