package vertx

import com.fasterxml.jackson.annotation.JsonIgnore
import io.vertx.ext.web.RoutingContext

/**
 * Created by chipn@eway.vnpn@eway.vn on 9/13/2016.
 */
class JsonRequest<T> {

    @JsonIgnore
    RoutingContext context;

    T data;

}
