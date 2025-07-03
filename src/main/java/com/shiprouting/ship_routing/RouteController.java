package com.shiprouting.ship_routing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/routes")
public class RouteController {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("route", new Route()); // For form binding
        model.addAttribute("routes", routeRepository.findAll()); // Existing routes
        model.addAttribute("nodes", nodeRepository.findAll()); // Source/destination dropdowns
        return "route_form"; // template: route_form.html
    }

    @PostMapping("/save")
    public String saveRoute(@RequestParam("sourceId") Long sourceId,
                            @RequestParam("destinationId") Long destinationId,
                            @RequestParam("distance") double distance) {

        Node source = nodeRepository.findById(sourceId).orElse(null);
        Node destination = nodeRepository.findById(destinationId).orElse(null);

        if (source == null || destination == null) {
            return "redirect:/routes/new?error=true";
        }

        Route route = new Route();
        route.setSource(source);
        route.setDestination(destination);
        route.setDistance(distance);

        routeRepository.save(route);
        return "redirect:/routes/new";
    }
}
