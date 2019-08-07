package wtf.tbh.commons.redis.gson;

import com.google.gson.Gson;
import redis.clients.jedis.JedisPubSub;
import wtf.tbh.commons.redis.MessageDispatcher;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class GsonMessageDispatcher<T> extends JedisPubSub implements MessageDispatcher<T> {
    private Gson gson;

    private Set<BakedListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

    GsonMessageDispatcher(Gson gson) {
        this.gson = gson;
    }


    @Override
    public void onMessage(String channel, String message) {
        String[] realMessage = message.split("\\|");
        try {
            Class<?> messagetype = Class.forName(realMessage[0]);
            this.dispatch(gson.fromJson(realMessage[1], (Type) messagetype));
        } catch (Exception ex) {
        }

    }

    @Override
    public void dispatch(T message) {
        for (BakedListener listener : listeners) {
            if (listener.getMessageType().isAssignableFrom(message.getClass())) {
                listener.dispatch(message);
            }
        }
    }

    void addListener(Object listener, Class<?> type) {
        this.listeners.add(BakedListener.create(listener, type));
    }
}
