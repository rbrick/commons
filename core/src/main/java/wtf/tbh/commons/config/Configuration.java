package wtf.tbh.commons.config;

import java.io.File;

public interface Configuration<T> {
    T get(String path);

    <R> R getAs(String path, Class<R> type);

    void set(String path, Object value);

    void setSafely(String path, T value);

    void save(File file);
}
