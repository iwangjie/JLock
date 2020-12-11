import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class TestConnRedis {


    @Test
    public void testConnRedis() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        Redisson.create(config);
    }

    @Test
    public void lock() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        RedissonClient client = Redisson.create(config);
        RLock myLock = client.getFairLock("myLock");
        myLock.lock();

        myLock.unlock();
    }


}
