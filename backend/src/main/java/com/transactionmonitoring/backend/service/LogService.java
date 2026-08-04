package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.entity.Logs;
import com.transactionmonitoring.backend.repository.LogsRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class LogService {

    private final LogsRepository logsRepository;

    public LogService(LogsRepository logsRepository) {
        this.logsRepository = logsRepository;
    }

    public Logs saveLog(Logs log) {
        return logsRepository.save(log);
    }

    // Get all logs
    public List<Logs> getAllLogs() {
        return logsRepository.findAll();
    }

    // Get log by id
    public Optional<Logs> getLogById(Long id) {
        return logsRepository.findById(id);
    }

    // Delete log by id
    public void deleteLogById(Long id) {
        logsRepository.deleteById(id);
    }
}