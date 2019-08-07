package wtf.tbh.commons.config.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import wtf.tbh.commons.config.Configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class JsonConfiguration implements Configuration<JsonElement> {

    private JsonElement parent;
    private Gson gson;

    public JsonConfiguration(JsonElement parent, Gson gson) {
        this.parent = parent;
        this.gson = gson;
    }


    @Override
    public JsonElement get(String path) {
        JsonElement found = parent;

        if (found.isJsonArray())
            throw new UnsupportedOperationException();

        for (String key : path.toLowerCase().split("\\.")) {
            if (found == null) break;

            if (!found.isJsonObject()) {
                break;
            }

            found = found.getAsJsonObject().get(key);
        }
        return found;
    }

    @Override
    public <R> R getAs(String path, Class<R> type) {
        JsonElement element = this.get(path);

        if (element != null)
            return gson.fromJson(element, type);

        return null;
    }

    @Override
    public void set(String path, Object value) {
        this.setSafely(path, gson.toJsonTree(value));
    }

    @Override
    public void setSafely(String path, JsonElement value) {
        JsonElement element = parent;


        String[] array = path.split("\\.");
        for (int i = 0; i < array.length - 1; i++) {
            JsonElement nextElement = element.getAsJsonObject().get(array[i]);

            if (nextElement == null) {
                nextElement = new JsonObject();

                element.getAsJsonObject().add(array[i], nextElement);

                element = nextElement;
            } else if (nextElement.isJsonObject()) {
                element = nextElement;
            } else {
                throw new UnsupportedOperationException();
            }

        }

        element.getAsJsonObject().add(array[array.length - 1], value);
    }


    @Override
    public void save(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            this.gson.toJson(this.parent, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
