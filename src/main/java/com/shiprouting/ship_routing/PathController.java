package com.shiprouting.ship_routing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/path")
public class PathController {

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private AStarService aStarService;

    @Autowired
    private weatherService weatherService;

    @GetMapping("/find")
    public String showPathForm(Model model) {
        model.addAttribute("nodes", nodeRepository.findAll());
        return "path_form";
    }

    @PostMapping("/result")
    public String findPath(@RequestParam("sourceId") Long sourceId,
                           @RequestParam("destinationId") Long destinationId,
                           Model model) {

        Node source = nodeRepository.findById(sourceId).orElse(null);
        Node destination = nodeRepository.findById(destinationId).orElse(null);

        if (source == null || destination == null) {
            model.addAttribute("error", "⚠️ Source or Destination not found.");
            model.addAttribute("nodes", nodeRepository.findAll());
            return "path_form";
        }

        var path = aStarService.findShortestPath(source, destination);

        if (path == null || path.isEmpty()) {
            model.addAttribute("error", "⚠️ No route found between selected ports.");
            model.addAttribute("nodes", nodeRepository.findAll());
            return "path_form";
        }

        String sourceWeather = weatherService.getWeatherDescription(source.getLatitude(), source.getLongitude());
        String destinationWeather = weatherService.getWeatherDescription(destination.getLatitude(), destination.getLongitude());

        model.addAttribute("path", path);
        model.addAttribute("source", source);
        model.addAttribute("destination", destination);
        model.addAttribute("sourceWeather", sourceWeather);
        model.addAttribute("destinationWeather", destinationWeather);

        return "path_result";
    }
}
