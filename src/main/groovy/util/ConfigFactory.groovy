package util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.apache.commons.lang3.Validate

class ConfigFactory {
    private static final MAPPER = new ObjectMapper(new YAMLFactory())
            .with {
        setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    }

    static <T> T getConfig(String resourceName, Class<T> configClass) throws IOException {
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)
        Validate.notNull(resourceAsStream, "Resource stream must not be null")
        return getConfig(resourceAsStream, configClass)
    }

    static <T> T getConfig(File file, Class<T> configClass) throws IOException {
        Validate.isTrue(file?.exists(), "Config file must be exists: " + file?.getAbsolutePath())
        return getConfig(new FileInputStream(file), configClass)
    }

    static <T> T getConfig(InputStream inputStream, Class<T> configClass) throws IOException {
        Validate.notNull(inputStream, "Config input stream must not be null")
        return MAPPER.readValue(inputStream, configClass)
    }
}
