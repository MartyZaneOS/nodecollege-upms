package com.nodecollege.cloud.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * ClassName RedisLock <br />
 * Function 基于Redis实现公平的、超时控制的、自旋锁 <br />
 * Reason 可选 <br />
 * date 2019-07-18 18:12 <br />
 *
 * @author gujun
 * @version 1.0
 * @since JDK 1.8
 */
@Service
public class RedisLock {
    /**
     * 日志记录器
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Redis连接器
     */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取锁
     *
     * @param key            锁信息
     * @param timeoutSeconds 超时时间,单位 秒
     * @return 是否获取到锁
     */
    public Boolean tryLock(String key, long timeoutSeconds) {
        logger.info("RedisLock tryLock input {} {}", key, timeoutSeconds);
        long timeout = timeoutSeconds * 1000;
        Boolean result = false;
        try {
            result = (Boolean) redisTemplate.execute((RedisCallback<Object>) connection -> {
                // 进入方法时间；用于计时器
                long start = System.currentTimeMillis();
                // 当前元素：当前元素值的第1段为信息量(即毫秒数)；第2段为识别码(即UUID)
                String signal = start + "_" + UUID.randomUUID().toString();
                // 加入队列，并返回队列长度
                long len = connection.rPush(key.getBytes(), signal.getBytes());
                connection.expire(key.getBytes(), timeoutSeconds);
                // 队列中只有自己，得到锁
                if (len == 1) {
                    logger.info("RedisLock tryLock success {} {} {} {}", key, timeoutSeconds, signal, "lb:len");
                    return true;
                }
                // 队首
                String first;
                do {
                    // 如果超时，获取锁失败
                    // 此处保证，一定能跳出while
                    if (System.currentTimeMillis() - start > timeout) {
                        logger.info("RedisLock tryLock fail {} {} {} {}", key, timeoutSeconds, signal, "lb:expire");
                        return false;
                    }
                    // 队首
                    first = new String(defaultEmpty(connection.lIndex(key.getBytes(), 0)));
                    logger.info("RedisLock tryLock loop {} {} {} waiting {}", key, timeoutSeconds, signal, first);
                    try {
                        // 休眠80毫秒
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        // 处理休眠失败的异常
                        logger.error("RedisLock tryLock error {} {} {}", key, timeoutSeconds, signal, e);
                        // 停止当前线程，返回false
                        Thread.currentThread().interrupt();
                    }
                } while (!(first.equals(signal))); // 如果队首不是自己，说明排队还没到自己；继续循环
                logger.info("RedisLock tryLock success {} {} {} {}", key, timeoutSeconds, signal, "lb:end");
                return true;
            });

        } catch (Exception e) {
            logger.error("RedisLock tryLock error {} {}", key, timeoutSeconds, e);
        }

        return result;
    }

    /**
     * 释放锁
     *
     * @param key            锁信息
     * @param timeoutSeconds 超时时间,单位 秒
     * @return 释放锁是否成功
     */
    public Boolean releaseLock(String key, long timeoutSeconds) {
        logger.info("RedisLock releaseLock input {} {}", key, timeoutSeconds);
        long timeout = timeoutSeconds * 1000;
        Boolean result = false;
        try {
            result = (Boolean) redisTemplate.execute((RedisCallback<Object>) connection -> {
                // 进入方法时间；用于计时器
                long start = System.currentTimeMillis();
                String first;
                long firstMs;
                do {
                    // 清理队首
                    connection.lPop(key.getBytes());
                    //region 清理当前过期元素
                    // 队首：队首值的第1段为信息量(即毫秒数)；第2段为识别码(即UUID)
                    first = new String(defaultEmpty(connection.lIndex(key.getBytes(), 0)));
                    // 队首值的第1段为信息量(即毫秒数)
                    firstMs = Long.parseLong(first.split("_")[0]);
                } while (firstMs > 0 && start - firstMs > timeout); // 第1次清理队首后：如果队列不为空，且队首已超时，那么清理队首；此外保证，一定能跳出while
                //endregion
                logger.info("RedisLock releaseLock success {} {}", key, timeoutSeconds);
                return true;
            });

        } catch (Exception e) {
            logger.error("RedisLock releaseLock error{} {}", key, timeoutSeconds, e);
        }

        return result;
    }

    /**
     * 字节数组空值处理
     *
     * @param bytes 字节数组
     * @return 如果字节数组为空，则将字符串"0"转为数组返回
     */
    private byte[] defaultEmpty(byte[] bytes) {
        // 如果数组为空指针或元素数为0
        return bytes == null || bytes.length == 0
                // 字符串"0"转为数组返回
                ? "0".getBytes()
                // 否则返回原数组
                : bytes;
    }
}
