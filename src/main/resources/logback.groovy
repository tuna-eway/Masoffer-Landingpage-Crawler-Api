import ch.qos.logback.classic.PatternLayout

import static ch.qos.logback.classic.Level.*

statusListener(NopStatusListener)
appender("STDOUT", ConsoleAppender) {
    layout(PatternLayout) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{100} - %msg%n"
    }
}
logger("io.netty", WARN)
logger("io.vertx", WARN)
logger("org.apache.shiro", WARN)
logger("org.mongodb.driver", WARN)
logger("org.elasticsearch", WARN)
logger("org.apache.activemq", WARN)
logger("org.apache.http", WARN)
logger("io.vertx.ext.web.handler.impl.LoggerHandlerImpl", ALL, ["Async"], true)
logger("org.redisson" , ERROR)
logger("org.openqa.selenium", OFF)

root(DEBUG, ["STDOUT"])
