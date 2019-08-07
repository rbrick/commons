package wtf.tbh.commons.redis.gson;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import wtf.tbh.commons.redis.Channel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class GsonChannel<T> implements Channel<T> {
    private Gson gson;
    private String name;

    private GsonMessageDispatcher<T> dispatcher;
    private JedisPool callPool, subscriberPool;
    private Thread subscribeThread;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public GsonChannel(Gson gson, String name, String host) {
        this.gson = gson;
        this.name = name;

        this.callPool = new JedisPool(host);
        this.subscriberPool = new JedisPool(host);

        this.dispatcher = new GsonMessageDispatcher<>(this.gson);

        this.subscribeThread = new Thread() {
            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    try (Jedis jedis = subscriberPool.getResource()) {
                        jedis.subscribe(dispatcher, name);
                    }
                }
            }
        };

        this.subscribeThread.start();
    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public void send(T message) {
        executorService.submit(() -> {
            try (Jedis jedis = callPool.getResource()) {
                jedis.publish(this.name, message.getClass().getName() + "|" + this.gson.toJson(message));
            }
        });
    }

    @Override
    public void register(Class<?> messageType, Object listener) {
        this.dispatcher.addListener(listener, messageType);
    }

    @Override
    public void close() {
        this.executorService.shutdown();

        this.subscribeThread.interrupt();
        this.dispatcher.unsubscribe();

        this.subscriberPool.close();
        this.callPool.close();
    }
}
