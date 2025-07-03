package com.shiprouting.ship_routing;
import com.shiprouting.ship_routing.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route,Long> {
    List<Route> findBySource(Node source);

}
