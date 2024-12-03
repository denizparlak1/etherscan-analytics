package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "top_addresses")
public class TopAddress {

    @Id
    @Column(name = "address")
    private String address;

    @Column(name = "rank")
    private int rank;

    @Column(name = "count")
    private long count;


    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
