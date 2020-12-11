package com.jlock.zookeeper;

public class UninitializedZookeeperException extends RuntimeException{

    public UninitializedZookeeperException(String message) {
        super(message);
    }
}
