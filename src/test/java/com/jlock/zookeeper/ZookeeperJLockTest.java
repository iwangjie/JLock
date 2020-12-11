package com.jlock.zookeeper;

import org.apache.zookeeper.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class ZookeeperJLockTest {

    @Test
    void testZookeeperOk() throws IOException, InterruptedException, KeeperException {
//        ZKConnection zkConnection = new ZKConnection();
//        ZooKeeper zooKeeper = zkConnection.connect("localhost");
//        ZKManager zkManager = new ZKManagerImpl(zooKeeper);
        ZookeeperJLockProperties zookeeperJLockProperties = ZookeeperJLockProperties.Local(2181);
        ZookeeperJLock zookeeperJLock = new ZookeeperJLock(zookeeperJLockProperties, "test_key");
        zookeeperJLock.lock();
        zookeeperJLock.unlock();

    }


    @Test
    void testZookeeperLock() throws Exception {

        ZKConnection zkConnection = new ZKConnection();
        ZooKeeper zooKeeper = zkConnection.connect("localhost");
        String lockBasePath = "/MyFirstZNode";
        String lockName = "lock1";
        String lockPath = zooKeeper.create(lockBasePath + "/" + lockName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
//        final Object lock = new Object();
//        synchronized (lock) {
        while (true) {
            List<String> nodes = zooKeeper.getChildren(lockBasePath, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
//                        synchronized (lock) {
//                            lock.notifyAll();
//                        }
                }
            });

            Collections.sort(nodes);
            System.out.println(nodes);
            if (lockPath.endsWith(nodes.get(0))) {
                break;
            } else {
//                    lock.wait();
            }
        }
        zooKeeper.close();
    }


    @Test
    void testZookeeperLock2() throws Exception {

        ZKConnection zkConnection = new ZKConnection();
        ZooKeeper zooKeeper = zkConnection.connect("localhost");
        String lockBasePath = "/JLooks";
        String lockName = "myLock01";

        // 创建父节点
        if (zooKeeper.exists(lockBasePath, false) == null) {
            String rootPath = zooKeeper.create(lockBasePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            if (!lockBasePath.equals(rootPath)) {
                throw new Exception("根节点创建失败");
            }
        }

        // 创建子节点
        String lockPath = zooKeeper.create(lockBasePath + "/" + lockName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        boolean needCreateWatcher = true;
        List<String> nodes;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        while (true) {
            // 创建监听
            if (needCreateWatcher) {
                needCreateWatcher = false;
                nodes = zooKeeper.getChildren(lockBasePath, event -> {
                    // 节点发生改变 打开屏障
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                nodes = zooKeeper.getChildren(lockBasePath, false);
            }
            Collections.sort(nodes);
            System.out.println(nodes);
            //判断子节点首位是否为当前节点
            if (lockPath.endsWith(nodes.get(0))) {
                // 获取到锁返回
                break;
            } else {
                // 没获取到等待
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                cyclicBarrier.reset();
                continue;
            }
        }
        zooKeeper.close();
    }


    public void tryLock(ZooKeeper zooKeeper, String lockBasePath, String lockPath) throws KeeperException, InterruptedException {
        List<String> nodes = zooKeeper.getChildren(lockBasePath, false);
        Collections.sort(nodes);
        if (lockPath.endsWith(nodes.get(0))) {
            throw new RuntimeException();
        }
    }

}