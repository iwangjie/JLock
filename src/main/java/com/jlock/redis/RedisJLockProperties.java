package com.jlock.redis;


public class RedisJLockProperties {
    private String host = "localhost";
    private String password = "";
    private int port = 6379;



    public static RedisJLockProperties Local(int port) {
        return new RedisJLockProperties(port);
    }

    public RedisJLockProperties() {
    }

    private RedisJLockProperties(int port) {
        this.port = port;
    }

    public RedisJLockProperties(String host, String password, int port) {
        this.host = host;
        this.password = password;
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
