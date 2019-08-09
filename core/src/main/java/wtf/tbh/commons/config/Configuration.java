package wtf.tbh.commons.config;

import wtf.tbh.commons.utils.Bytes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public interface Configuration<T> {
    T get(String path);

    <R> R getAs(String path, Class<R> type);

    void set(String path, Object value);

    void setSafely(String path, T value);

    void save(File file);

    static void saveDefault(String defaultConfigResource, String location) {
        File defaultLocation = new File(location);

        if (!defaultLocation.exists()) {
            try {
                Files.write(Paths.get(location), Bytes.readAll(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(defaultConfigResource))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
