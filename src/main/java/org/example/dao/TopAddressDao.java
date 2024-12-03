package org.example.dao;

import org.example.model.TopAddress;
import java.util.List;

public interface TopAddressDao {
    void saveOrUpdate(TopAddress topAddress);
    List<TopAddress> getAllTopAddresses();
    void deleteTopAddress(TopAddress topAddress);
}
