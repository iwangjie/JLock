package com.jlock.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZKManagerImpl implements ZKManager {

    private ZookeeperJLockProperties zookeeperJLockProperties;
    private ZKConnection zkConnection;
    private ZooKeeper zk;

//    public ZKManagerImpl(ZookeeperJLockProperties zookeeperJLockProperties) throws IOException, InterruptedException {
//        this.zookeeperJLockProperties = zookeeperJLockProperties;
//        ZKConnection zkConnection = new ZKConnection();
//        ZooKeeper zk = zkConnection.connect(zookeeperJLockProperties.getHost());
//        this.zk = zk;
//        this.zkConnection = zkConnection;
//    }

    public ZKManagerImpl(ZooKeeper zk) {
        this.zk = zk;
    }

    @Override
    public void create(String path) throws KeeperException, InterruptedException {
        zk.create(
                path,
                new byte[]{},
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
    }

    @Override
    public boolean has(String path) {
        return false;
    }

    @Override
    public void del(String path) {
        try {
            zk.delete(path,1);
        } catch (InterruptedException e) {
           throw new RuntimeException(e);
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        }
    }
}
