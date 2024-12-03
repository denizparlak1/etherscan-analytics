package org.example.service;

import org.example.dao.TransactionDao;
import org.example.dao.TransactionDaoImpl;
import org.example.model.Transaction;

import java.io.Serializable;

public class TransactionService implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient TransactionDao transactionDao;

    public void initialize() {
        if (transactionDao == null) {
            transactionDao = new TransactionDaoImpl();
        }
    }

    public void saveTransaction(Transaction transaction) {
        if (transactionDao == null) {
            initialize();
        }
        transactionDao.saveOrUpdate(transaction);
    }
}
