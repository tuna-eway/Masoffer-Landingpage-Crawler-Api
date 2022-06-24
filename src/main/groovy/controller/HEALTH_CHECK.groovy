package controller

import app.AppConfig
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import vertx.VertxController

import java.util.concurrent.TimeUnit

@InheritConstructors
@Slf4j
class HEALTH_CHECK extends VertxController<AppConfig> {

    @Override
    void validate(RoutingContext context, HttpServerRequest request) {
    }

    @Override
    void handle(RoutingContext context, HttpServerRequest request, HttpServerResponse response) {
        ChromeOptions option = new ChromeOptions()
                .setHeadless(false)
                .setPageLoadStrategy(PageLoadStrategy.NORMAL)
                .addArguments("--silent")
                .addArguments("--log-level=3")
                .addArguments("--no-sandbox")
                .addArguments("--disable-gpu")

        RemoteWebDriver driver = null
        try {
            driver = new ChromeDriver(option)
            driver.get("https://ecom.dev.masoffer.tech/")
            driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS)

            writeSuccessResponse(response, "It's live!")
        } catch (Exception error) {
            writeErrorResponse(response, 500, "Error : ${error}")
        } finally {
            driver.close()
            driver.quit()
        }
    }
}



