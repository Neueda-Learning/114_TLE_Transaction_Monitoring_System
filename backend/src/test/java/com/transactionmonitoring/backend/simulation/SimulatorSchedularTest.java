package com.transactionmonitoring.backend.simulation;

import com.transactionmonitoring.backend.service.TransactionSimulationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SimulatorSchedularTest {

    @Mock
    private TransactionSimulationService transactionSimulationService;

    private SimulatorSchedular simulator;

    @BeforeEach
    void setUp() {
        simulator = new SimulatorSchedular(transactionSimulationService, 5L);
    }

    @Test
    void isRunning_returnsFalseInitially() {
        assertFalse(simulator.isRunning());
    }

    @Test
    void startSimulation_setsRunningTrue() throws InterruptedException {
        simulator.startSimulation();
        Thread.sleep(100);
        assertTrue(simulator.isRunning());
        simulator.stopSimulation();
    }

    @Test
    void stopSimulation_setsRunningFalse() throws InterruptedException {
        simulator.startSimulation();
        Thread.sleep(100);
        simulator.stopSimulation();
        assertFalse(simulator.isRunning());
    }

    @Test
    void stopBeforeStart_doesNotThrow() {
        simulator.stopSimulation();
        assertFalse(simulator.isRunning());
    }
}
