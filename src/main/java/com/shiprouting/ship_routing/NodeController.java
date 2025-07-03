package com.shiprouting.ship_routing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/nodes")
public class NodeController {

    @Autowired
    private NodeRepository nodeRepository;

    @GetMapping("/new")
    public String showAddNodeForm(Model model) {
        model.addAttribute("node", new Node());
        model.addAttribute("nodes", nodeRepository.findAll());
        return "node_form";
    }

    @PostMapping("/save")
    public String saveNode(@ModelAttribute Node node) {
        nodeRepository.save(node);
        return "redirect:/nodes/new";
    }
}
