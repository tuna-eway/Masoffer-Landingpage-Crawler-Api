package controller

import app.AppConfig
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import groovy.transform.InheritConstructors
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.Validate
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import util.Constants
import vertx.VertxController

import java.util.concurrent.TimeUnit

@InheritConstructors
class LANDINGPAGE_CRAWLER extends VertxController<AppConfig> {
    LoadingCache<List<String>, String> htmlContentCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            // Ex: ["https://beacons.ai/covi", "root", "id"] -> <div>HTML content</div>
            .build { queryParams ->
                crawlHtmlContent(queryParams[0], queryParams[1], queryParams[2])
            }

    @Override
    void validate(RoutingContext context, HttpServerRequest request) {
        String url = context.request().getParam("url") ?: ""
        String element = context.request().getParam("element") ?: ""
        String queryOption = context.request().getParam("query_option") ?: ""

        Validate.isTrue(url ==~ Constants.URL_REGEX, "Input URL is not valid")
        Validate.isTrue(!StringUtils.isEmpty(element), "Input element is not valid")
        Validate.isTrue(!StringUtils.isEmpty(queryOption), "Input query_option is not valid")

        context.put("url", url)
        context.put("element", element)
        context.put("query_option", queryOption)
    }

    @Override
    void handle(RoutingContext context, HttpServerRequest request, HttpServerResponse response) {
        String url = context.get("url")
        String element = context.get("element")
        String queryOption = context.get("query_option")

        String crawledHtml = htmlContentCache.get(["$url", "$element", "$queryOption"])
        writeSuccessResponse(context.response(), crawledHtml)
    }

    String crawlHtmlContent(String url, String element, String queryOption) {
        ChromeOptions options = new ChromeOptions()
                .setHeadless(true)
                .setPageLoadStrategy(PageLoadStrategy.EAGER)
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
            browserDriver.manage().timeouts().implicitlyWait(1, TimeUnit.MINUTES)

            browserDriver.get(url)

            WebElement rootElement
            if (StringUtils.equals(queryOption, "id")) {
                rootElement = browserDriver.findElementById(element)
            } else if (StringUtils.equals(queryOption, "class")) {
                rootElement = browserDriver.findElementByClassName(element)
            } else {
                return ""
            }
            String crawledHtml = rootElement.getAttribute('innerHTML') ?: ""
            return crawledHtml
        } catch (e) {
            e.printStackTrace()
            return ""
        } finally {
            browserDriver?.close()
            browserDriver?.quit()
        }
    }
}
