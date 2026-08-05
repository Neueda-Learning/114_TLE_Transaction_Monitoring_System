package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class SimulatorSchedular {

    private static final Logger log = LoggerFactory.getLogger(SimulatorSchedular.class);

    private static final long INITIAL_DELAY_SECONDS = 0L;
    private static final long PERIOD_SECONDS        = 5L;

    private final TransactionSimulationService transactionSimulationService;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "tx-simulator");
                t.setDaemon(true);
                return t;
            });

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledFuture<?> scheduledFuture;

    public SimulatorSchedular(TransactionSimulationService transactionSimulationService) {
        this.transactionSimulationService = transactionSimulationService;
    }

    /**
     * Starts the simulation. Generates one transaction every {@value #PERIOD_SECONDS} seconds.
     * A no-op if the simulation is already running.
     */
    public synchronized void startSimulation() {
        if (running.get()) {
            log.warn("Simulation is already running — ignoring start request");
            return;
        }

        scheduledFuture = scheduler.scheduleAtFixedRate(
                this::generateTransaction,
                INITIAL_DELAY_SECONDS,
                PERIOD_SECONDS,
                TimeUnit.SECONDS
        );

        running.set(true);
        log.info("Transaction simulation started (period={}s)", PERIOD_SECONDS);
    }

    /**
     * Stops the simulation gracefully. A no-op if the simulation is not running.
     */
    public synchronized void stopSimulation() {
        if (!running.get()) {
            log.warn("Simulation is not running — ignoring stop request");
            return;
        }

        if (scheduledFuture != null) {
            scheduledFuture.cancel(false); // do not interrupt an in-progress task
            scheduledFuture = null;
        }

        running.set(false);
        log.info("Transaction simulation stopped");
    }

    /**
     * @return {@code true} if the simulation is currently active
     */
    public boolean isRunning() {
        return running.get();
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void generateTransaction() {
        try {
            transactionSimulationService.generateAndSaveOne();
        } catch (Exception e) {
            // Catch-all to prevent the ScheduledExecutorService from silently
            // swallowing exceptions and cancelling the recurring task.
            log.error("Error while generating transaction: {}", e.getMessage(), e);
        }
    }
}
