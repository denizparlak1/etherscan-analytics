package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address")
    private String toAddress;

    @Column(name = "value")
    private double value;

    @Column(name = "transaction_date")
    private String transactionDate;

    // Getters and Setters

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}
