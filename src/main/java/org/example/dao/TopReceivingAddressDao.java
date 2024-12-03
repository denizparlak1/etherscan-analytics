package org.example.dao;

import org.example.model.TopReceivingAddress;
import java.util.List;

public interface TopReceivingAddressDao {
    void saveOrUpdate(TopReceivingAddress topReceivingAddress);
    List<TopReceivingAddress> getAllTopReceivingAddresses();
    void deleteTopReceivingAddress(TopReceivingAddress topReceivingAddress);
}
