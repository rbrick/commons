package wtf.tbh.commons.redis.gson;

import java.lang.reflect.Method;

public final class BakedMethod {
    private Method method;
    private boolean async;


    public BakedMethod(Method method, boolean async) {
        this.method = method;
        this.async = async;
    }

    public boolean isAsync() {
        return async;
    }

    public Method getMethod() {
        return method;
    }
}
