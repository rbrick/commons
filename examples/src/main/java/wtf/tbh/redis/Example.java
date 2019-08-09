package wtf.tbh.redis;

import com.google.gson.JsonObject;
import wtf.tbh.commons.redis.Channel;
import wtf.tbh.commons.redis.Listener;

public class Example {

    static class MyListener {
        // when we receive a message that is a string
        @Listener
        public void onStringMessage(String message) {

            System.out.println("I am a string message: " + message);
        }


        // When we receive a message that is json
        @Listener
        public void onJsonMessage(JsonObject object) {
            System.out.println("I am a json message: " + object.toString());
        }
    }


    public static void main(String[] args) {
        final String redisHost = "redis://localhost:6379";
        final MyListener listenerInstance = new MyListener();

        final Channel<String> stringChannel = Channel.<String>create("my-channel", redisHost).build();
        final Channel<JsonObject> jsonChannel = Channel.<JsonObject>create("my-channel", redisHost).build();


        stringChannel.register(String.class, listenerInstance);
        jsonChannel.register(JsonObject.class, listenerInstance);


        // now we can send things to the channel.

        stringChannel.send("Hello, World");

        JsonObject object = new JsonObject();
        {
            object.addProperty("hello", "world");
        }
        jsonChannel.send(object);
    }
}
