package com.jlock.core;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public interface JLockAdapter extends Lock {


    @Override
    default void lockInterruptibly() throws InterruptedException {

    }

    @Override
    default boolean tryLock() {
        return false;
    }

    @Override
    default boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    default Condition newCondition() {
        return null;
    }
}
