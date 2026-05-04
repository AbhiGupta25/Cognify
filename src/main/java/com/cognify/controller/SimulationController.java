package com.cognify.controller;

import com.cognify.model.dto.SimulationRequest;
import com.cognify.model.dto.SimulationResponse;
import com.cognify.service.SimulationService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulations")
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/analyze")
    public SimulationResponse analyzeScenario(@RequestBody SimulationRequest request) {
        return simulationService.analyzeScenario(request);
    }
}
