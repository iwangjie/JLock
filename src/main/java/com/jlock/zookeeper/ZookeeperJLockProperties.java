package com.jlock.zookeeper;


public class ZookeeperJLockProperties {
    private String host = "localhost";
    private String rootPath = "/JLocks";
    private int port = 2181;

    public static ZookeeperJLockProperties Local(int port) {
        return new ZookeeperJLockProperties(port);
    }

    private ZookeeperJLockProperties(int port) {
        this.port = port;
    }


    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
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
