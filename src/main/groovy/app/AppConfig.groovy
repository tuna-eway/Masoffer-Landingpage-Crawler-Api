package app

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.Validate
import vertx.VertxConfig

@Slf4j
class AppConfig extends VertxConfig {
    static AppConfig newInstance(File appConfigFile) throws IOException {
        Validate.isTrue(appConfigFile.exists(), "AppConfigFile not exists: ${appConfigFile.getAbsolutePath()}")
        log.debug('@Loading app.AppConfig')

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory()).with {
            propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            disable DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
        }

        AppConfig appConfig = objectMapper.readValue(appConfigFile, AppConfig.class)

        //=> Overide http.port properties from System Properties
        String httpPort = System.properties.getProperty('http.port', Integer.toString(appConfig.httpPort))
        appConfig.httpPort = Integer.parseInt(httpPort)
        return appConfig
    }
}
