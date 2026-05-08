import java.io.File;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class App {
    private static final String PASSWORD_GENERATOR_URL =
            "https://www.calculator.net/password-generator.html";

    public static void main(String[] args) {
        String driverPath = resolveChromeDriverPath();
        System.setProperty("webdriver.chrome.driver", driverPath);

        WebDriver webDriver = new ChromeDriver();
        try {
            runTask1(webDriver);
            Task2.printClientIp(webDriver);
            Task3.printForecastAndSave(webDriver);
        } catch (Exception e) {
            System.out.println("Error");
            System.out.println(e.toString());
        } finally {
            webDriver.quit();
        }
    }

    private static void runTask1(WebDriver webDriver) {
        webDriver.get(PASSWORD_GENERATOR_URL);

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        String password = extractGeneratedPassword(webDriver);
        System.out.println("Задание 1. Сгенерированный пароль: " + password);
    }

    private static String extractGeneratedPassword(WebDriver webDriver) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;

        String password = (String) jsExecutor.executeScript(
                "var elements = Array.from(document.querySelectorAll('input, textarea, td, span, div'));" +
                "var candidates = elements" +
                "  .map(function (element) {" +
                "    var raw = '';" +
                "    if (typeof element.value === 'string' && element.value.trim().length > 0) {" +
                "      raw = element.value.trim();" +
                "    } else if (typeof element.textContent === 'string' && element.textContent.trim().length > 0) {" +
                "      raw = element.textContent.trim();" +
                "    }" +
                "    return raw;" +
                "  })" +
                "  .filter(function (value) {" +
                "    return value.length >= 8" +
                "      && /[a-z]/.test(value)" +
                "      && /[A-Z]/.test(value)" +
                "      && /\\d/.test(value)" +
                "      && /[^A-Za-z0-9\\s]/.test(value);" +
                "  })" +
                "  .sort(function (left, right) { return right.length - left.length; });" +
                "return candidates.length > 0 ? candidates[0] : null;");

        if (password != null && !password.isEmpty()) {
            return password;
        }

        List<WebElement> textInputs = webDriver.findElements(By.cssSelector("input[type='text'], input:not([type])"));
        for (WebElement element : textInputs) {
            String value = element.getAttribute("value");
            if (value != null && value.length() >= 8) {
                return value;
            }
        }

        throw new IllegalStateException("Password was not found on the page.");
    }

    private static String resolveChromeDriverPath() {
        String systemPropertyPath = System.getProperty("chrome.driver.path");
        if (systemPropertyPath != null && !systemPropertyPath.trim().isEmpty()) {
            return systemPropertyPath.trim();
        }

        String envPath = System.getenv("CHROME_DRIVER_PATH");
        if (envPath != null && !envPath.trim().isEmpty()) {
            return envPath.trim();
        }

        File localDriver = new File("drivers/chromedriver.exe");
        if (localDriver.exists()) {
            return localDriver.getAbsolutePath();
        }

        throw new IllegalStateException(
                "ChromeDriver path was not configured. "
                + "Set -Dchrome.driver.path=... or CHROME_DRIVER_PATH, "
                + "or put chromedriver.exe into the drivers directory.");
    }
}
