import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;

public class Task2 {
    private static final String IPIFY_URL = "https://api.ipify.org/?format=json";

    public static void printClientIp(WebDriver webDriver) throws Exception {
        webDriver.get(IPIFY_URL);

        String jsonText = extractJsonText(webDriver);
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(jsonText);
        String ipAddress = String.valueOf(object.get("ip"));

        System.out.println("Задание 2. IPv4-адрес клиента: " + ipAddress);
    }

    static String extractJsonText(WebDriver webDriver) {
        try {
            WebElement pre = webDriver.findElement(By.tagName("pre"));
            return pre.getText();
        } catch (NoSuchElementException e) {
            WebElement body = webDriver.findElement(By.tagName("body"));
            return body.getText();
        }
    }
}
