package vertx

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.Vertx

class VertxConfig {

    @JsonProperty("http.port")
    int httpPort

    @JsonProperty("users.properties")
    String usersPropertiesPath

    Vertx vertx = Vertx.vertx()
}
