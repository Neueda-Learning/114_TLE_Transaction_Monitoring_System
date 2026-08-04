package com.transactionmonitoring.backend.service;
import com.transactionmonitoring.backend.repository.LogsRepository;
import com.transactionmonitoring.backend.entity.Logs;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LogService {   
    private final LogsRepository logsRepository;

    public LogService(LogsRepository logsRepository){
        this.logsRepository = logsRepository;
    }
    
    public Logs saveLog(Logs log){
        return logsRepository.save(log);
    }

    public List<Logs> getAllLogs() {
        return logsRepository.findAll();
    }

    public Optional<Logs> getLogById(Long id) {
        return logsRepository.findById(id);
    }

    public void deleteLogById(Long id) {
        logsRepository.deleteById(id);
    }
}
