package com.jlock.core;

import com.jlock.jdk.JdkJLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class JLock implements Lock {

    private JLockAdapter adapter;

    public JLock(JLockAdapter adapter) {
        this.adapter = adapter;
    }

    private JLock() {
    }

    public static JLock Default() {
        JLock jLock = new JLock();
        jLock.setLock(new JdkJLock());
        return jLock;
    }

    public static JLock ByAdapter(JLockAdapter jLockAdapter) {
        JLock jLock = new JLock();
        jLock.setLock(jLockAdapter);
        return jLock;
    }


    private void setLock(JLockAdapter jLockAdapter) {
        this.adapter = jLockAdapter;
    }

    @Override
    public void lock() {
        adapter.lock();
    }

    public void lock(String key) {
        this.adapter.lock();
    }

    public void unlock(String key) {
        this.adapter.unlock();
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        adapter.unlock();
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
