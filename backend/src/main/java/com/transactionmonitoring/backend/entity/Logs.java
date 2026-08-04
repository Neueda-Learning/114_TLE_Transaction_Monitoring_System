package com.transactionmonitoring.backend.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
public class Logs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    @Column(name = "alert_id")
    private Long alertId;

    public Long getAlertId(){
        return alertId;
    }

    public void setAlertId(Long alertId){
        this.alertId = alertId;
    }

    @Column(name = "action")
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Column(name = "old_status")
    private String oldStatus;
    public String getOldStatus(){
        return oldStatus;
    }
    public void setOldStatus(String oldStatus){
        this.oldStatus = oldStatus;
    }

    @Column(name = "new_status")
    private String newStatus;
    public String getNewStatus(){
        return newStatus;
    }
    public void setNewStatus(String newStatus){
        this.newStatus = newStatus;
    }

    @Column(name = "description")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }

}
