package vertx

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.Canonical

/**
 * Created by chipn@eway.vn@eway.vn on 7/13/2016.
 */

@Canonical
class JsonResponse<T> {

    Integer statusCode = 200

    @JsonInclude(JsonInclude.Include.NON_NULL)
    JsonMeta meta = new JsonMeta()

    @JsonInclude(JsonInclude.Include.NON_NULL)
    T data

    JsonError error

}
