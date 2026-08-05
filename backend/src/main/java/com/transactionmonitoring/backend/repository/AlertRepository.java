package com.transactionmonitoring.backend.repository;
import com.transactionmonitoring.backend.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert,Long> {
    List<Alert> findAllByTransactionId(Long transactionId);

}
