import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.WebDriver;

public class Task3 {
    private static final String FORECAST_URL =
            "https://api.open-meteo.com/v1/forecast?latitude=56&longitude=44"
            + "&hourly=temperature_2m,rain&current=cloud_cover"
            + "&timezone=Europe%2FMoscow&forecast_days=1&wind_speed_unit=ms";

    public static void printForecastAndSave(WebDriver webDriver) throws Exception {
        webDriver.get(FORECAST_URL);

        String jsonText = Task2.extractJsonText(webDriver);
        JSONParser parser = new JSONParser();
        JSONObject root = (JSONObject) parser.parse(jsonText);
        JSONObject hourly = (JSONObject) root.get("hourly");

        JSONArray times = (JSONArray) hourly.get("time");
        JSONArray temperatures = (JSONArray) hourly.get("temperature_2m");
        JSONArray rains = (JSONArray) hourly.get("rain");

        String table = buildForecastTable(times, temperatures, rains);
        System.out.println("Задание 3. Прогноз погоды на сутки для Нижнего Новгорода");
        System.out.println(table);
        saveForecast(table);
    }

    private static String buildForecastTable(JSONArray times, JSONArray temperatures, JSONArray rains) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%-3s %-18s %-14s %-12s%n",
                "№", "Дата/время", "Температура", "Осадки (мм)"));

        for (int i = 0; i < times.size(); i++) {
            String time = String.valueOf(times.get(i)).replace('T', ' ');
            String temperature = formatNumber(temperatures.get(i));
            String rain = formatNumber(rains.get(i));

            builder.append(String.format(Locale.US, "%-3d %-18s %-14s %-12s%n",
                    i + 1, time, temperature, rain));
        }

        return builder.toString();
    }

    private static String formatNumber(Object value) {
        if (value instanceof Number) {
            return String.format(Locale.US, "%.2f", ((Number) value).doubleValue());
        }
        return String.valueOf(value);
    }

    private static void saveForecast(String table) throws IOException {
        Path resultPath = Paths.get("result", "forecast.txt");
        Files.createDirectories(resultPath.getParent());
        Files.write(resultPath, table.getBytes(StandardCharsets.UTF_8));
    }
}
