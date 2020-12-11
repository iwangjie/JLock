import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class TestZookeeper {

    private ZooKeeper zoo;
    CountDownLatch connectionLatch = new CountDownLatch(1);

    // ...

    public ZooKeeper connect(String host)
            throws IOException,
            InterruptedException {
        zoo = new ZooKeeper(host, 2000, new Watcher() {
            public void process(WatchedEvent we) {
                if (we.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    connectionLatch.countDown();
                }
            }
        });

        connectionLatch.await();
        return zoo;
    }

    public void close() throws InterruptedException {
        zoo.close();
    }

    @Test
    void testConnZookeeper() throws IOException, InterruptedException {
        ZooKeeper connect = connect("localhost");
        
    }
}
