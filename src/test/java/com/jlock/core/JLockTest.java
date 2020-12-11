package com.jlock.core;

import com.jlock.redis.RedisJLock;
import com.jlock.redis.RedisJLockProperties;
import com.jlock.zookeeper.ZookeeperJLock;
import com.jlock.zookeeper.ZookeeperJLockProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Lock;

class JLockTest {


    private static int sum = 100;

    public void reloadSum() {
        sum = 100;
    }

    public static void subtractAndPrint() {
        System.out.println(--sum);
    }

    @Test
    void lock() {
        Lock lock = JLock.Default();
        for (int i = 0; i < 100; i++) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    lock.lock();
                    subtractAndPrint();
                    lock.unlock();
                }
            });
            thread.start();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(0, sum);
        reloadSum();
    }


    @Test
    public void testRedisJLock() {
        RedisJLockProperties redisJLockProperties = new RedisJLockProperties();
        JLock lock = JLock.ByAdapter(new RedisJLock(redisJLockProperties, "test_key"));
        for (int i = 0; i < 100; i++) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    lock.lock();
                    subtractAndPrint();
                    lock.lock();
                }
            });
            thread.start();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(0, sum);
        reloadSum();
    }

    @Test
    public void testRedisJLockByKey() {
        RedisJLockProperties redisJLockProperties = new RedisJLockProperties();
        JLock lock = JLock.ByAdapter(new RedisJLock(redisJLockProperties, "test_key"));
        for (int i = 0; i < 100; i++) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    lock.lock();
                    subtractAndPrint();
                    lock.unlock();
                }
            });
            thread.start();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(0, sum);
        reloadSum();
    }

    @Test
    void testJLock() {
        // JDK 锁
        JLock jdkLock = JLock.Default();
        jdkLock.lock();
        jdkLock.unlock();

        // Redis 锁
        JLock redisLock = JLock.ByAdapter(new RedisJLock(
                RedisJLockProperties.Local(6379),
                "redis_key"));
        redisLock.lock();
        redisLock.unlock();

        // Zookeeper 锁
        JLock zookeeperLock = JLock.ByAdapter(new ZookeeperJLock(
                ZookeeperJLockProperties.Local(2181),
                "zookeeper_key"));
        zookeeperLock.lock();
        zookeeperLock.unlock();

    }
}