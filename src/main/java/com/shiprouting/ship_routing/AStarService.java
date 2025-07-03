package com.shiprouting.ship_routing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AStarService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private weatherService weatherService;

    // Weather impact multipliers
    private final Map<String, Double> weatherCostMap = Map.of(
            "Clear", 1.0,
            "Clouds", 1.2,
            "Rain", 1.5,
            "Thunderstorm", 2.0,
            "Drizzle", 1.3,
            "Mist", 1.4,
            "Fog", 1.6,
            "Snow", 1.7
    );

    public List<Node> findShortestPath(Node start, Node goal) {
        Map<Node, Node> cameFrom = new HashMap<>();
        Map<Node, Double> gScore = new HashMap<>();
        Map<Node, Double> fScore = new HashMap<>();
        Map<Node, String> weatherCache = new HashMap<>(); // 🌤️ Caching

        Comparator<Node> comparator = Comparator.comparingDouble(fScore::get);
        PriorityQueue<Node> openSet = new PriorityQueue<>(comparator);
        Set<Node> closedSet = new HashSet<>();

        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, goal));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current);

            for (Route route : routeRepository.findBySource(current)) {
                Node neighbor = route.getDestination();
                if (closedSet.contains(neighbor)) continue;

                // 🌦️ Get weather condition (cached)
                String condition = weatherCache.computeIfAbsent(
                        neighbor,
                        n -> weatherService.getMainCondition(n.getLatitude(), n.getLongitude())
                );

                // 🌧️ Adjust cost based on weather
                double weatherMultiplier = weatherCostMap.getOrDefault(condition, 1.0);
                double adjustedDistance = route.getDistance() * weatherMultiplier;

                double tentativeG = gScore.getOrDefault(current, Double.MAX_VALUE) + adjustedDistance;

                if (tentativeG < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);
                    fScore.put(neighbor, tentativeG + heuristic(neighbor, goal));
                    if (!openSet.contains(neighbor)) openSet.add(neighbor);
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    private List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    // Haversine heuristic (straight-line distance)
    private double heuristic(Node a, Node b) {
        double lat1 = Math.toRadians(a.getLatitude());
        double lon1 = Math.toRadians(a.getLongitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double lon2 = Math.toRadians(b.getLongitude());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double aHarv = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(aHarv));
        return 6371 * c; // Distance in km
    }
}
