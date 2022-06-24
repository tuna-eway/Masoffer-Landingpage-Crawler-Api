package controller

import app.AppConfig
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext
import vertx.VertxController

/**
 * Created by chipn@eway.vn on 2/4/17.
 */
@CompileStatic
@InheritConstructors
class NOT_FOUND extends VertxController<AppConfig> {

    @Override
    void validate(RoutingContext context, HttpServerRequest request) {

    }

    @Override
    void handle(RoutingContext context, HttpServerRequest request, HttpServerResponse response) {
        def resultMap = [
            meta       : [:],
            status_code: 404,
            error      : [
                message: "resource not found"
            ]
        ]

        writeJson(response, 404, resultMap)
    }

}
