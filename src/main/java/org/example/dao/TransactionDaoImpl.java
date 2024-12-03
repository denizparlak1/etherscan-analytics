package org.example.dao;

import org.example.model.Transaction;
import org.hibernate.Session;

public class TransactionDaoImpl implements TransactionDao {

    @Override
    public void saveOrUpdate(Transaction transaction) {
        Session session = null;
        org.hibernate.Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            session.saveOrUpdate(transaction);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }
}
