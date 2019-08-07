package wtf.tbh.commons.redis.gson;

import wtf.tbh.commons.redis.Async;
import wtf.tbh.commons.redis.Listener;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class BakedListener {
    private Object instance;
    private Class<?> messageType;
    private Set<BakedMethod> methods;
    private ExecutorService service = Executors.newSingleThreadExecutor();

    public BakedListener(Object instance, Class<?> messageType, Set<BakedMethod> methods) {
        this.instance = instance;
        this.methods = methods;
        this.messageType = messageType;
    }

    public void dispatch(Object message) {
        if (messageType.isAssignableFrom(message.getClass())) {
            for (BakedMethod method : methods) {
                if (method.isAsync()) {
                    service.submit(() -> method.getMethod().invoke(this.instance, message));
                } else {
                    try {
                        method.getMethod().invoke(this.instance, message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static BakedListener create(Object instance, Class<?> messageType) {
        Set<BakedMethod> methodSet = new HashSet<>();

        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Listener.class)) {
                if (method.getParameterCount() == 1) {
                    if (messageType.isAssignableFrom(method.getParameterTypes()[0])) {
                        method.setAccessible(true);
                        methodSet.add(new BakedMethod(method, method.isAnnotationPresent(Async.class)));
                    }
                }
            }
        }

        return new BakedListener(instance, messageType, methodSet);
    }

    public Class<?> getMessageType() {
        return messageType;
    }
}
