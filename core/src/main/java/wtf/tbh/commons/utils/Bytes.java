package wtf.tbh.commons.utils;

import java.io.IOException;
import java.io.InputStream;

public final class Bytes {


    public static byte[] readAll(InputStream stream) throws IOException {
        byte[] array = new byte[stream.available()];
        int read = stream.read(array);
        return array;
    }


}
