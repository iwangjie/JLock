package com.jlock.zookeeper;

import com.jlock.core.JLockAdapter;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ZookeeperJLock implements JLockAdapter {

    private ZooKeeper zooKeeper;
    private ZookeeperJLockProperties properties;
    private String lockKey;
    private String lockPath;


    public ZookeeperJLock(ZookeeperJLockProperties properties, String lockKey) {
        this.properties = properties;
        this.lockKey = lockKey;
        initZookeeperJLock();
    }

    private void initZookeeperJLock() {
        ZKConnection zkConnection = new ZKConnection();
        try {
            this.zooKeeper = zkConnection.connect("localhost");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void lock() {
        try {
            // 创建父节点
            if (zooKeeper.exists(this.properties.getRootPath(), false) == null) {
                String rootPath = zooKeeper.create(properties.getRootPath(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                if (!properties.getRootPath().equals(rootPath)) {
                    throw new Exception("根节点创建失败");
                }
            }

            // 创建子节点
            this.lockPath = zooKeeper.create(properties.getRootPath() + "/" + lockKey, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            boolean needCreateWatcher = true;
            List<String> nodes;
            CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
            while (true) {
                // 创建监听
                if (needCreateWatcher) {
                    needCreateWatcher = false;
                    nodes = zooKeeper.getChildren(properties.getRootPath(), event -> {
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
                    nodes = zooKeeper.getChildren(properties.getRootPath(), false);
                }
                Collections.sort(nodes);
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unlock() {
        try {
            zooKeeper.delete(this.lockPath, -1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
