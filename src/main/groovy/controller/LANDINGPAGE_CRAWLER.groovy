package controller

import app.AppConfig
import groovy.transform.InheritConstructors
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext
import org.apache.commons.lang3.Validate
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import util.Constants
import vertx.VertxController

@InheritConstructors
class LANDINGPAGE_CRAWLER extends VertxController<AppConfig> {
    @Override
    void validate(RoutingContext context, HttpServerRequest request) {
        String url = context.request().getParam("url") ?: ""
        String id = context.request().getParam("id") ?: "root"

        Validate.isTrue(url ==~ Constants.URL_REGEX, "Input URL is not valid")

        context.put("id", id)
        context.put("url", url)
    }

    @Override
    void handle(RoutingContext context, HttpServerRequest request, HttpServerResponse response) {
        String id = context.get("id")
        String url = context.get("url")

        ChromeOptions options = new ChromeOptions()
                .setHeadless(true)
                .setPageLoadStrategy(PageLoadStrategy.NORMAL)
                .addArguments("–-disable-extensions", "--mute-audio")
                .addArguments("--silent")
                .addArguments("--start-maximized")
                .addArguments("--log-level=3")
                .addArguments("--enable-automation")
                .addArguments("--no-sandbox")
                .addArguments("--disable-dev-shm-usage")
                .addArguments("--disable-gpu")
                .addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")

        RemoteWebDriver browserDriver
        try {
            browserDriver = new ChromeDriver(options)
            browserDriver.get(url)

            WebElement rootElement = browserDriver.findElementById(id)
            String crawledHtml = rootElement.getAttribute('innerHTML') ?: ""

            writeSuccessResponse(context.response(), crawledHtml)
        } catch (e) {
            writeSuccessResponse(context.response(), e.message)
            throw new RuntimeException(e.message)
        } finally {
            browserDriver?.close()
            browserDriver?.quit()
        }
    }
}
