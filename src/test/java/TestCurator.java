import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestCurator {

    private static String zookeeperConnectionString = "localhost:2181";


    @Test
    void testCuratorCreateClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();
    }

    @Test
    void testCuratorCreatePath() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();
        String path = client.create().forPath("/Create", null);
        System.out.println(path);
        client.close();
    }


    @Test
    void testCuratorCreateMutex() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();
        String lockPath = "/Create" + "/" + "lock1";
        InterProcessMutex lock = new InterProcessMutex(client, lockPath);
        InterProcessMutex lock2 = new InterProcessMutex(client, lockPath);
        InterProcessMutex lock3 = new InterProcessMutex(client, lockPath);
        InterProcessMutex lock4 = new InterProcessMutex(client, lockPath);
        InterProcessMutex lock5 = new InterProcessMutex(client, lockPath);


        int count = 20;
        CountDownLatch cd = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock.acquire();
                        try {
                            resourceCompetition();
                            cd.countDown();
                        } finally {
                            lock.release();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        CountDownLatch cd2 = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock2.acquire();
                        try {
                            resourceCompetition();
                            cd2.countDown();
                        } finally {
                            lock2.release();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        CountDownLatch cd3 = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock3.acquire();
                        try {
                            resourceCompetition();
                            cd3.countDown();
                        } finally {
                            lock3.release();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        CountDownLatch cd4 = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock4.acquire();
                        try {
                            resourceCompetition();
                            cd4.countDown();
                        } finally {
                            lock4.release();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        CountDownLatch cd5 = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock5.acquire();
                        try {
                            resourceCompetition();
                            cd5.countDown();
                        } finally {
                            lock5.release();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        cd.await();
        cd2.await();
        cd3.await();
        cd4.await();
        cd5.await();
        assert sum == 0;

    }


    private static int sum = 100;

    public static void resourceCompetition() {
        --sum;
    }


    @Test
    void testMux() throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();
        String lockPath = "/Create" + "/" + "lock1";
        InterProcessMutex lock = new InterProcessMutex(client, lockPath);
        if (lock.acquire(1, TimeUnit.SECONDS)) {
            try {
                System.out.println(111);
            } finally {
                lock.release();
            }
        }
    }


    @Test
    void testLeaderSelectorListenerAdapter() {

        LeaderSelectorListenerAdapter listener = new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
                System.out.println("被选中领导者");

            }
        };

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();

        LeaderSelector selector = new LeaderSelector(client, "/Create", listener);
        selector.autoRequeue();  // not required, but this is behavior that you will probably expect
        selector.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    class InstanceDetails {
        private String info;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public InstanceDetails(String info) {
            this.info = info;
        }
    }

    @Test
    void testCuratorDiscovery() throws Exception {
        String PATH = "/discovery";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();

        JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);
        ServiceDiscovery<InstanceDetails> serviceDiscovery = ServiceDiscoveryBuilder
                .builder(InstanceDetails.class)
                .client(client)
                .basePath(PATH)
                .serializer(serializer)
                .build();
        serviceDiscovery.start();

        UriSpec uriSpec = new UriSpec("{scheme}://127.0.0.1:{port}");
        ServiceInstance<InstanceDetails> instance = ServiceInstance.<InstanceDetails>builder()
                .name("test_service")
                .payload(new InstanceDetails("test description"))
                .port((int) (65535 * Math.random())) // in a real application, you'd use a common port
                .uriSpec(uriSpec)
                .build();

        serviceDiscovery.registerService(instance);

        Thread.sleep(2000000);

    }


}
