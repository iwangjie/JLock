package com.jlock.zookeeper;

import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ZKConnectionTest {

    @Test
    void testZkConnection() throws IOException, InterruptedException {
        ZKConnection zkConnection = new ZKConnection();
        ZooKeeper zk = zkConnection.connect("localhost");
        Assertions.assertEquals(zk.getState(), ZooKeeper.States.CONNECTED);
        zkConnection.close();
        Assertions.assertEquals(zk.getState(), ZooKeeper.States.CLOSED);
    }
}