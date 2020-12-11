package com.jlock.redis;

import com.jlock.core.JLockAdapter;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisJLock implements JLockAdapter {

    private static Logger log = LoggerFactory.getLogger(RedisJLock.class);

    private RedissonClient redissonClient;
    private RedisJLockProperties properties;
    private RLock lock;
    private String lockKey;

    private void loadConfiguration() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://" + properties.getHost() + ":" + properties.getPort());
        if (properties.getPassword() != null && !"".equals(properties.getPassword())) {
            singleServerConfig.setPassword(properties.getPassword());
        }
        this.redissonClient = Redisson.create(config);
    }


    public RedisJLock(RedisJLockProperties properties, String lockKey) {
        this.properties = properties;
        loadConfiguration();
        this.lockKey = lockKey;
        this.lock = redissonClient.getFairLock(lockKey);
    }

    @Override
    public void lock() {
        lock.lock();
        log.debug("redis locked:{}", lockKey);
    }

    @Override
    public void unlock() {
        lock.unlock();
        log.debug("redis unlocked:{}", lockKey);
    }

}
