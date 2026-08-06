package com.transactionmonitoring.backend.controller;

import com.transactionmonitoring.backend.service.TransactionSimulationService;
import com.transactionmonitoring.backend.simulation.SimulatorSchedular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/simulator")
public class TransactionSimulationController {

    private static final Logger log = LoggerFactory.getLogger(TransactionSimulationController.class);
    private static final String MESSAGE_KEY = "message";

    private final TransactionSimulationService transactionSimulationService;
    private final SimulatorSchedular simulatorSchedular;

    public TransactionSimulationController(
            TransactionSimulationService transactionSimulationService,
            SimulatorSchedular simulatorSchedular) {
        this.transactionSimulationService = transactionSimulationService;
        this.simulatorSchedular = simulatorSchedular;
    }

    /**
     * POST /api/simulator/start
     * Starts the continuous transaction simulation (one transaction every 5 s).
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> start() {
        if (simulatorSchedular.isRunning()) {
            log.warn("POST /api/simulator/start — simulation already running");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(MESSAGE_KEY, "Simulation is already running"));
        }
        simulatorSchedular.startSimulation();
        log.info("POST /api/simulator/start — simulation started");
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Simulation started successfully"));
    }

    /**
     * POST /api/simulator/stop
     * Stops the continuous transaction simulation.
     */
    @PostMapping("/stop")
    public ResponseEntity<Map<String, String>> stop() {
        if (!simulatorSchedular.isRunning()) {
            log.warn("POST /api/simulator/stop — simulation is not running");
            return ResponseEntity.badRequest()
                    .body(Map.of(MESSAGE_KEY, "Simulation is not currently running"));
        }
        simulatorSchedular.stopSimulation();
        log.info("POST /api/simulator/stop — simulation stopped");
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Simulation stopped successfully"));
    }

    /**
     * POST /api/simulator/generate/{count}
     * Immediately generates and saves {@code count} transactions in one batch.
     *
     * @param count number of transactions to generate; must be &gt; 0
     */
    @PostMapping("/generate/{count}")
    public ResponseEntity<Map<String, Object>> generate(@PathVariable int count) {
        if (count <= 0) {
            log.warn("POST /api/simulator/generate/{} — invalid count", count);
            return ResponseEntity.badRequest()
                    .body(Map.of(MESSAGE_KEY, "count must be greater than zero"));
        }
        log.info("POST /api/simulator/generate/{} — generating batch", count);
        var saved = transactionSimulationService.generateAndSaveBatch(count);
        log.info("POST /api/simulator/generate/{} — saved {} transactions", count, saved.size());
        return ResponseEntity.ok(Map.of(
                MESSAGE_KEY, "Batch generated successfully",
                "generated", saved.size()
        ));
    }

    /**
     * POST /api/simulator/generate/coverage/{count}
     * Generates deterministic scenario data to exercise major fraud workflows.
     */
    @PostMapping("/generate/coverage/{count}")
    public ResponseEntity<Map<String, Object>> generateCoverage(@PathVariable int count) {
        if (count <= 0) {
            log.warn("POST /api/simulator/generate/coverage/{} — invalid count", count);
            return ResponseEntity.badRequest()
                    .body(Map.of(MESSAGE_KEY, "count must be greater than zero"));
        }
        log.info("POST /api/simulator/generate/coverage/{} — generating coverage batch", count);
        var saved = transactionSimulationService.generateCoverageBatch(count);
        log.info("POST /api/simulator/generate/coverage/{} — saved {} transactions", count, saved.size());
        return ResponseEntity.ok(Map.of(
                MESSAGE_KEY, "Coverage batch generated successfully",
                "generated", saved.size()
        ));
    }

    /**
     * GET /api/simulator/status
     * Returns whether the continuous simulation is currently active.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        boolean running = simulatorSchedular.isRunning();
        log.info("GET /api/simulator/status — running={}", running);
        return ResponseEntity.ok(Map.of(
                "running", running,
                MESSAGE_KEY, running ? "Simulation is active" : "Simulation is stopped"
        ));
    }
}
