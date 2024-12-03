package org.example.service;

import org.example.dao.TopAddressDao;
import org.example.dao.TopAddressDaoImpl;
import org.example.model.TopAddress;
import java.io.Serializable;
import java.util.List;

public class TopAddressService implements Serializable {

    private transient TopAddressDao topAddressDao;

    public void initialize() {
        if (topAddressDao == null) {
            topAddressDao = new TopAddressDaoImpl();
        }
    }

    public void saveOrUpdate(TopAddress topAddress) {
        if (topAddressDao == null) {
            initialize();
        }
        topAddressDao.saveOrUpdate(topAddress);
    }

    public List<TopAddress> getAllTopAddresses() {
        if (topAddressDao == null) {
            initialize();
        }
        return topAddressDao.getAllTopAddresses();
    }

    public void deleteTopAddress(TopAddress topAddress) {
        if (topAddressDao == null) {
            initialize();
        }
        topAddressDao.deleteTopAddress(topAddress);
    }
}
