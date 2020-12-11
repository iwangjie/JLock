package com.jlock.redis;

public class UninitializedRedisException extends RuntimeException{

    public UninitializedRedisException(String message) {
        super(message);
    }
}
