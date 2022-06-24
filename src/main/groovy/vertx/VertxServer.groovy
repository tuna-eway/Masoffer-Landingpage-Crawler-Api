package vertx

import groovy.util.logging.Slf4j
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router

/**
 * Created by chipn@eway.vn on 1/25/17.
 */
@Slf4j
abstract class VertxServer<C extends VertxConfig> {

    static {
        System.setProperty "vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory"
    }

    C config

    @Delegate
    final Router router
    final HttpServer httpServer

    VertxServer(C config) {
        this.config = config
        def httpServerOptions = new HttpServerOptions(
                maxInitialLineLength: 4096 * 4,
                compressionSupported: true,
                maxHeaderSize: 8192 * 2
        )

        this.router = Router.router(config.vertx)
        this.httpServer = config.vertx.createHttpServer(httpServerOptions)

        setupRouter()
    }

    abstract setupRouter()

    void start(Handler<AsyncResult<HttpServer>> listenHandler) {
        httpServer.requestHandler(router.&accept).listen(config.httpPort, { event ->
            listenHandler?.handle(event)
            if (event.succeeded()) {
                println "RestServer started at http://127.0.0.1:" + httpServer.actualPort()
            } else {
                close()
                throw new RuntimeException("Unable to start RestServer at http://127.0.0.1:" + httpServer.actualPort(), event.cause())
            }
        })
    }

    void close() {
        httpServer?.close()
        config.vertx?.close()
    }

}