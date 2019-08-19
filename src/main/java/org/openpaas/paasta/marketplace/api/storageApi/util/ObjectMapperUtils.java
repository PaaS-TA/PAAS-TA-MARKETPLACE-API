package org.openpaas.paasta.marketplace.api.storageApi.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertNotNull;
import java.io.IOException;

public class ObjectMapperUtils {
    public static <T> T parseObject(String string, Class<T> clazz) throws IOException {
        assertNotNull(string, clazz);
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(string.getBytes(), clazz);
    }
    
    public static <T> String writeValueAsString(T object) throws IOException {
        assertNotNull(object);
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString( object );
    }
}
