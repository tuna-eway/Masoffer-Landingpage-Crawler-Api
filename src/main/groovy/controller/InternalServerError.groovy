package controller

import app.AppConfig
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext
import org.apache.commons.lang3.exception.ExceptionUtils
import vertx.VertxController

/**
 * Created by chipn@eway.vn on 2/4/17.
 */
@Slf4j
@InheritConstructors
@CompileStatic
class InternalServerError extends VertxController<AppConfig> {

    @Override
    void validate(RoutingContext context, HttpServerRequest request) {
    }

    @Override
    void handle(RoutingContext context, HttpServerRequest request, HttpServerResponse response) {
        if (context.failure() == null) {
            context++
            return
        }
        def debug = request.getParam("debug")?.equalsIgnoreCase("true")
        def resultMap = [
            meta       : [:],
            status_code: 500,
            error      : [
                message           : context.failure().getMessage(),
                root_cause_message: debug ? ExceptionUtils.getRootCauseMessage(context.failure()) : null,
                detail            : debug ? ExceptionUtils.getStackTrace(context.failure()) : null
            ]
        ]

        log.error "Failure", context.failure()
        if (response.bytesWritten() <= 0) {
            writeJson(response, 500, resultMap)
        }
    }

}
