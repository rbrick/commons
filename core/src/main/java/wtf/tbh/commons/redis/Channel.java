package wtf.tbh.commons.redis;

import com.google.gson.Gson;
import wtf.tbh.commons.redis.gson.GsonChannel;

public interface Channel<T> {
    class Builder<S> {
        private final String host;
        private final String channelName;
        private Gson gson = new Gson();

        Builder(String name, String host) {
            this.channelName = name;
            this.host = host;
        }

        public Builder<S> withGson(Gson gson) {
            this.gson = gson;
            return this;
        }


        public Channel<S> build() {
            return new GsonChannel<>(this.gson, this.channelName, this.host);
        }
    }

    static <S> Builder<S> create(String name, String host) {
        return new Builder<S>(name, host);
    }

    String name();

    void send(T message);


    void register(Class<?> messageType, Object listener);

    void close();

}
