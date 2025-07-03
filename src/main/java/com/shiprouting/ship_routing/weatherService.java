package com.shiprouting.ship_routing;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class weatherService { // ✅ Capitalized to match Java class conventions

    private final String API_KEY = "your_api_key_here"; // 🔁 Replace with your actual API key

    // ✅ Used in path_result.html to show weather info
    public String getWeatherDescription(double lat, double lon) {
        try {
            String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat +
                    "&lon=" + lon + "&appid=" + API_KEY + "&units=metric";

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("main") || !response.containsKey("weather")) {
                return "No weather data found";
            }

            Map<String, Object> main = (Map<String, Object>) response.get("main");
            List<Map<String, Object>> weatherList = (List<Map<String, Object>>) response.get("weather");
            Map<String, Object> weather = weatherList.get(0);

            double temp = (main.get("temp") instanceof Integer)
                    ? (Integer) main.get("temp")
                    : (Double) main.get("temp");

            String mainWeather = (String) weather.get("main");
            String description = (String) weather.get("description");

            return mainWeather + " (" + description + "), " + temp + "°C";
        } catch (Exception e) {
            return "Weather fetch failed";
        }
    }

    // ✅ Used in A* algorithm for weather-aware cost adjustment
    public String getMainCondition(double lat, double lon) {
        try {
            String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat +
                    "&lon=" + lon + "&appid=" + API_KEY + "&units=metric";

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("weather")) return "Clear";

            List<Map<String, Object>> weatherList = (List<Map<String, Object>>) response.get("weather");
            return (String) weatherList.get(0).get("main"); // e.g., "Clear", "Rain"
        } catch (Exception e) {
            return "Clear"; // fallback default
        }
    }
}
