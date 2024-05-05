package io.goji.tg.notifier;

import redis.clients.jedis.Jedis;
import java.util.UUID;

public class Lock {
    public static String acquire(Jedis redis, String key, int timeout, boolean blocking) {
        String id = UUID.randomUUID().toString();
        String lockKey = key + ".lock";

        if (blocking) {
            while (!"OK".equals(redis.set(lockKey, id))) {
                // Busy-wait loop. Consider adding a sleep here to avoid CPU spinning.
            }
            return id;
        } else {
            return "OK".equals(redis.set(lockKey, id)) ? id : null;
        }
    }

    public static void release(Jedis redis, String key, String id) {
        String lockKey = key + ".lock";
        String currentId = redis.get(lockKey);

        if (id.equals(currentId)) {
            redis.del(lockKey);
        } else {
            throw new IllegalStateException("Lock not acquired or already released");
        }
    }
}
