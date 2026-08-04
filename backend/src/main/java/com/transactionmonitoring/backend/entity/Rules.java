package com.transactionmonitoring.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "rules")
public class Rules {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rule_id")
	private Long ruleId;

	@Column(name = "rule_name", nullable = false, length = 100)
	private String ruleName;

	@Column(name = "rule_type", nullable = false, length = 50)
	private String ruleType;

	@Column(name = "field_name", length = 50)
	private String fieldName;

	@Column(name = "operator", length = 20)
	private String operator;

	@Column(name = "threshold_value", length = 100)
	private String thresholdValue;

	@Column(name = "time_window_minutes")
	private Integer timeWindowMinutes;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public Rules() {
	}

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public Integer getTimeWindowMinutes() {
		return timeWindowMinutes;
	}

	public void setTimeWindowMinutes(Integer timeWindowMinutes) {
		this.timeWindowMinutes = timeWindowMinutes;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
