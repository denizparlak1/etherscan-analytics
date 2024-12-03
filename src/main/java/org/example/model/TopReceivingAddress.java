package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "top_receiving_addresses")
public class TopReceivingAddress {

    @Id
    @Column(name = "address")
    private String address;

    @Column(name = "rank")
    private int rank;

    @Column(name = "total_value")
    private long totalValue;

    // Getters and Setters

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(long totalValue) {
        this.totalValue = totalValue;
    }
}
