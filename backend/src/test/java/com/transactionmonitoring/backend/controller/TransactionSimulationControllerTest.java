package com.transactionmonitoring.backend.controller;

import com.transactionmonitoring.backend.entity.Transaction;
import com.transactionmonitoring.backend.service.TransactionSimulationService;
import com.transactionmonitoring.backend.simulation.SimulatorSchedular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionSimulationControllerTest {

    private TransactionSimulationService simulationService;
    private SimulatorSchedular simulatorSchedular;
    private TransactionSimulationController controller;

    @BeforeEach
    void setUp() {
        simulationService = mock(TransactionSimulationService.class);
        simulatorSchedular = mock(SimulatorSchedular.class);
        controller = new TransactionSimulationController(simulationService, simulatorSchedular);
    }

    @Test
    void start_returnsConflictWhenAlreadyRunning() {
        when(simulatorSchedular.isRunning()).thenReturn(true);

        ResponseEntity<Map<String, String>> response = controller.start();

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void start_startsSimulatorWhenStopped() {
        when(simulatorSchedular.isRunning()).thenReturn(false);

        ResponseEntity<Map<String, String>> response = controller.start();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(simulatorSchedular, times(1)).startSimulation();
    }

    @Test
    void generate_returnsBadRequestForInvalidCount() {
        ResponseEntity<Map<String, Object>> response = controller.generate(0);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void generate_callsBatchGenerationForValidCount() {
        when(simulationService.generateAndSaveBatch(5)).thenReturn(List.of(new Transaction(), new Transaction()));

        ResponseEntity<Map<String, Object>> response = controller.generate(5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(simulationService, times(1)).generateAndSaveBatch(5);
    }

    @Test
    void generateCoverage_callsCoverageGenerationForValidCount() {
        when(simulationService.generateCoverageBatch(6)).thenReturn(List.of(
                new Transaction(), new Transaction(), new Transaction()
        ));

        ResponseEntity<Map<String, Object>> response = controller.generateCoverage(6);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(simulationService, times(1)).generateCoverageBatch(6);
    }

    @Test
    void status_reportsSchedulerState() {
        when(simulatorSchedular.isRunning()).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = controller.status();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().get("running"));
    }
}
