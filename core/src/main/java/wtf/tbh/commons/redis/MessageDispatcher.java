package wtf.tbh.commons.redis;

public interface MessageDispatcher<T> {

    void dispatch(T message);

}
