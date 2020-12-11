package com.jlock.zookeeper;

import org.apache.zookeeper.KeeperException;

public interface ZKManager {

    void create(String path) throws KeeperException, InterruptedException;

    boolean has(String path);

    void del(String path);
}
