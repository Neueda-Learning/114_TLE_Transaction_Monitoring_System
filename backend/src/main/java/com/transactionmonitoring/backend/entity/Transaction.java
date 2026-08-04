package com.transactionmonitoring.backend.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "account_id")
    private String accountId;

    @Column(name  = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String Currency;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "payee_id")
    private String payeeid;

    @Column(name = "payee_name")
    private String payeeName;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "status")
    private String status;

    public Transaction(){

    }

    public Long getTransactionId(){
        return transactionId;
    }
    public void setTransactionId(Long transactionId){
        this.transactionId = transactionId;
    }

    public String getAccountId(){
        return accountId;
    }
    public void setAccountId(String accountId){
        this.accountId = accountId;
    }

    public BigDecimal getAmount(){
        return amount;
    }
    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }

    public String getCurrency(){
        return Currency;
    }
    public void setCurrency(String currency){
        Currency = currency;
    }

    public String getTransactionType(){
        return transactionType;
    }
    public void setTransactionType(String transactionType){
        this.transactionType = transactionType;
    }

    public String getPayeeid(){
        return payeeid;
    }
    public void setPayeeid(String payeeid){
        this.payeeid = payeeid;
    }

    public String getPayeeName(){
        return payeeName;
    }
    public void setPayeeName(String payeeName){
        this.payeeName = payeeName;
    }

    public LocalDateTime getTransactionDate(){
        return transactionDate;
    }
    public void setTransactionDate(LocalDateTime transactionDate){
        this.transactionDate = transactionDate;
    }

    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getPayeeId(){
        return payeeid;
    }
    

    
}
