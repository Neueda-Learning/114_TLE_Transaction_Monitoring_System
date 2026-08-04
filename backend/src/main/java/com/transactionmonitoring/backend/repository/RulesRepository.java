package com.transactionmonitoring.backend.repository;
import com.transactionmonitoring.backend.entity.Rules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RulesRepository extends JpaRepository<Rules,Long> {
    
}
