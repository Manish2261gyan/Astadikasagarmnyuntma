package com.shiprouting.ship_routing;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TideService {

    private final String API_KEY = "your_api_key_here";

    public String getTideData(double lat, double lon) {
        String url = "https://www.worldtides.info/api/v3?extremes&lat=" + lat +
                "&lon=" + lon + "&key=" + API_KEY;

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("extremes")) {
            return "No tide data available";
        }

        var tides = (java.util.List<Map<String, Object>>) response.get("extremes");
        if (tides.isEmpty()) return "No tide events found";

        var nextTide = tides.get(0); // Show next tide
        String type = (String) nextTide.get("type");
        String date = (String) nextTide.get("date");

        return type + " tide expected at " + date;
    }
}
