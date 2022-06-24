package app

import controller.*
import groovy.util.logging.Slf4j
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Route
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.LoggerFormat
import io.vertx.ext.web.handler.LoggerHandler
import vertx.VertxServer

import static io.netty.handler.codec.http.HttpMethod.*

/**
 * Created by chipn@eway.vn on 1/25/17.
 */
@Slf4j
class RestServer extends VertxServer<AppConfig> {

    RestServer(AppConfig config) {
        super(config)
    }

    @Override
    setupRouter() {
        Route.metaClass.rightShift = { Class clazz ->
            return delegate.blockingHandler(clazz.newInstance(config), false)
        }
        route().handler(BodyHandler.create())
        route().handler(LoggerHandler.create(true, LoggerFormat.DEFAULT))

        route().last().handler(new NOT_FOUND(config))
        route().failureHandler(new InternalServerError(config))

        route("/*").handler(CorsHandler.create("*").with {
            allowedMethods([OPTIONS, GET, POST, PUT, DELETE, PATCH] as Set<HttpMethod>)
            allowCredentials(true)
            allowedHeaders(['Authorization', 'Access-Control-Allow-Method', 'Access-Control-Allow-Headers', 'Content-Type'] as Set)
        })

      get('/health.json') >> HEALTH_CHECK
      get('/landingpage/crawler') >> LANDINGPAGE_CRAWLER
    }

}
