package com.shiprouting.ship_routing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRepository extends JpaRepository<Node, Long> {
    // No custom methods needed for now
}
