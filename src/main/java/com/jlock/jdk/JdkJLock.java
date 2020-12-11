package com.jlock.jdk;

import com.jlock.core.JLockAdapter;

import java.util.concurrent.locks.ReentrantLock;

public class JdkJLock implements JLockAdapter {

    private ReentrantLock lock;

    public JdkJLock() {
        this.lock = new ReentrantLock();
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

}
