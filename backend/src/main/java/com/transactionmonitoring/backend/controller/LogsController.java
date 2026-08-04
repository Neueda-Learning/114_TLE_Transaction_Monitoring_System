package com.transactionmonitoring.backend.controller;
import com.transactionmonitoring.backend.entity.Logs;
import com.transactionmonitoring.backend.service.LogService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/logs")
public class LogsController {

    private final LogService logService;

    public LogsController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping
    public Logs createLog(@RequestBody Logs log) {
        return logService.saveLog(log);
    }

    @GetMapping
    public List<Logs> getAllLogs() {
        return logService.getAllLogs();
    }

    @GetMapping("/{id}")
    public Optional<Logs> getLogById(@PathVariable Long id) {
        return logService.getLogById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteLog(@PathVariable Long id) {
        logService.deleteLogById(id);
    }
}