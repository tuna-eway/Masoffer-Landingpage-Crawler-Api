package vertx

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext
import org.apache.commons.lang3.Validate

/**
 * Created by chipn@eway.vn on 2/4/17.
 */
@CompileStatic
abstract class VertxController<C extends VertxConfig> implements Handler<RoutingContext> {

    protected C config

    VertxController(C config) {
        this.config = config
    }

    protected static final ObjectMapper mapper = new ObjectMapper().with {
        configure(JsonParser.Feature.ALLOW_COMMENTS, true)
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)

        //=> Không serialize các map.*value == nul
        configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)

        //=> Không báo lỗi khi json data có các thuộc tính mà bean không có
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        //=> Quy định tên thuộc tính sẽ là viết thường và gạch dưới _
        setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    }

    protected static final JsonSlurper jsonSlurper = new JsonSlurper()

    @Override
    final void handle(RoutingContext context) {
        this.validate(context, context.request())
        this.handle(context, context.request(), context.response())
    }

    /**
     * Validate Routing Context
     * @param context
     */
    abstract void validate(RoutingContext context, HttpServerRequest request)

    /**
     * Handle Request and Response
     * @param context
     * @param request
     * @param response
     */
    abstract void handle(RoutingContext context, HttpServerRequest request, HttpServerResponse response)

    Map<String, Object> parseJson(RoutingContext context) {
        return jsonSlurper.parse(context.body?.bytes) as Map
    }

    /**
     * Parse context.body -> model by Jackson
     * @param context
     * @param genericClazz
     * @return
     */
    static <T> JsonRequest<T> parseJson(RoutingContext context, Class<T> genericClazz) {
        try {
            JsonRequest<T> jsonRequest
            if (context.request().method() in [HttpMethod.POST, HttpMethod.PUT]) {
                JavaType jsonRequestType = mapper.typeFactory.constructParametricType(JsonRequest.class, genericClazz)

                jsonRequest = mapper.readValue(context.body?.bytes, jsonRequestType) as JsonRequest<T>

            } else {
                jsonRequest = new JsonRequest<T>()
            }

            jsonRequest.context = context
            return jsonRequest
        } catch (IOException e) {
            throw new RuntimeException(e)
        }
    }

    static writeJson(HttpServerResponse response, int statusCode, Object object) {
        Validate.notNull(object, "object must be not null")
        response.setStatusCode(statusCode)
                .putHeader("Content-Type", "application/json;charset=UTF-8")

        if (object instanceof String) {
            response.end(object as String)
            return
        }
        if (object instanceof JsonResponse) {
            object.statusCode = statusCode
        }

        if (object instanceof Map) {
            if (object.status_code) {
                object.status_code = statusCode
            }
        }

        def bytes = mapper.writeValueAsBytes(object)
        response.end(Buffer.buffer(bytes))
    }

    static writeErrorResponse(HttpServerResponse response, int code, String message) {
        response.setStatusCode(code)
                .putHeader("Content-Type", "application/json;charset=UTF-8")

        def bytes = mapper.writeValueAsBytes([
                status : 0,
                message: message
        ])
        response.end(Buffer.buffer(bytes))
    }

    static writeSuccessResponse(HttpServerResponse response, Object object) {
        Validate.notNull(object, "object must be not null")

        if (object instanceof String) {
            response.end(object as String)
            return
        }

        response.setStatusCode(200).putHeader("Content-Type", "application/json;charset=UTF-8")

        if (object instanceof JsonResponse) {
            object.statusCode = 200
        }

        if (object instanceof Map) {
            if (object.status_code) {
                object.status_code = 200
            }
        }

        def bytes = mapper.writeValueAsBytes([
                status: 1,
                data  : object
        ])
        response.end(Buffer.buffer(bytes))
    }
}
