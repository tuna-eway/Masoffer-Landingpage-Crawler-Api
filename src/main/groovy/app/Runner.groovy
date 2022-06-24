package app

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.openqa.selenium.chrome.ChromeDriverService

class Runner {
    static {
        TimeZone.setDefault(TimeZone.getTimeZone('Asia/Bangkok'))
        System.setProperty("webdriver.chrome.driver", "conf/chromedriver")
        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true")
        new ObjectMapper().with {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    static void main(String[] args) {
        def appConfigFile = new File(System.getProperty('user.dir'), 'conf/application.yml')

        def config = AppConfig.newInstance(appConfigFile)

        def server = new RestServer(config)

        server.start({ event -> })
    }
}
