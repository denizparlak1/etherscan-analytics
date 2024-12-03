package org.example.dao;

import org.example.model.Transaction;

public interface TransactionDao {
    void saveOrUpdate(Transaction transaction);
}
