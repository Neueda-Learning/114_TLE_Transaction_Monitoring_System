package com.transactionmonitoring.backend.repository;
import com.transactionmonitoring.backend.entity.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepository extends JpaRepository<Logs,Long> {
    
}
