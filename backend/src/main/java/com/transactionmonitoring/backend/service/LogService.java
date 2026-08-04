package com.transactionmonitoring.backend.service;
import com.transactionmonitoring.backend.repository.LogsRepository;
import com.transactionmonitoring.backend.entity.Logs;
import org.springframework.stereotype.Service;
@Service
public class LogService {   
    private final LogsRepository logsRepository;
    public LogService(LogsRepository logsRepository){
        this.logsRepository = logsRepository;
    }
    
    public Logs saveLog(Logs log){
        return logsRepository.save(log);
    }
}
