package org.example.service;

import org.example.dao.TopReceivingAddressDao;
import org.example.dao.TopReceivingAddressDaoImpl;
import org.example.model.TopReceivingAddress;
import java.io.Serializable;
import java.util.List;

public class TopReceivingAddressService implements Serializable {

    private transient TopReceivingAddressDao topReceivingAddressDao;

    public void initialize() {
        if (topReceivingAddressDao == null) {
            topReceivingAddressDao = new TopReceivingAddressDaoImpl();
        }
    }

    public void saveOrUpdate(TopReceivingAddress topReceivingAddress) {
        if (topReceivingAddressDao == null) {
            initialize();
        }
        topReceivingAddressDao.saveOrUpdate(topReceivingAddress);
    }

    public List<TopReceivingAddress> getAllTopReceivingAddresses() {
        if (topReceivingAddressDao == null) {
            initialize();
        }
        return topReceivingAddressDao.getAllTopReceivingAddresses();
    }

    public void deleteTopReceivingAddress(TopReceivingAddress topReceivingAddress) {
        if (topReceivingAddressDao == null) {
            initialize();
        }
        topReceivingAddressDao.deleteTopReceivingAddress(topReceivingAddress);
    }
}
