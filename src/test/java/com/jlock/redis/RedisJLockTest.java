package com.jlock.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class RedisJLockTest {


    RedisJLock createDefault() {
        RedisJLockProperties redisJLockProperties = new RedisJLockProperties();
        redisJLockProperties.setHost("localhost");
        redisJLockProperties.setPort(6379);
        return new RedisJLock(redisJLockProperties, "test_key");
    }


    @Test
    void testRedisJLockCreate() {
        RedisJLock redisJLock = createDefault();
        redisJLock.lock();
        redisJLock.unlock();
    }

    private static int sum = 100;

    void concurrentOperation() {
        System.out.println(--sum);
    }

    void concurrentDataRecovery() {
        sum = 100;
    }

    /**
     * 单系统测试
     *
     * @throws InterruptedException
     */
    @Test
    void testRedisJLockLockAndUnLock() throws InterruptedException {
        RedisJLock redisJLock = createDefault();
        CountDownLatch cd = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                redisJLock.lock();
                concurrentOperation();
                redisJLock.unlock();
                cd.countDown();
            }).start();
        }
        cd.await(4, TimeUnit.SECONDS);
        Assertions.assertEquals(0, sum);
        concurrentDataRecovery();

    }

    /**
     * 多系统测试
     *
     * @throws InterruptedException
     */
    @Test
    void testMultipleSystemsLockAndUnLock() throws InterruptedException {
        RedisJLock redisJLock1 = createDefault();
        RedisJLock redisJLock2 = createDefault();
        CountDownLatch cd = new CountDownLatch(100);
        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                redisJLock1.lock();
                concurrentOperation();
                redisJLock1.unlock();
                cd.countDown();
            }).start();
            new Thread(() -> {
                redisJLock2.lock();
                concurrentOperation();
                redisJLock2.unlock();
                cd.countDown();
            }).start();

        }
        cd.await(6, TimeUnit.SECONDS);
        Assertions.assertEquals(0, sum);
        concurrentDataRecovery();
    }


}