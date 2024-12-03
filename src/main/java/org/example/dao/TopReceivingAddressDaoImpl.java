package org.example.dao;

import org.example.model.TopReceivingAddress;
import org.hibernate.Session;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class TopReceivingAddressDaoImpl implements TopReceivingAddressDao {

    @Override
    public void saveOrUpdate(TopReceivingAddress topReceivingAddress) {
        Session session = null;
        org.hibernate.Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            session.saveOrUpdate(topReceivingAddress);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public List<TopReceivingAddress> getAllTopReceivingAddresses() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<TopReceivingAddress> query = builder.createQuery(TopReceivingAddress.class);
            Root<TopReceivingAddress> root = query.from(TopReceivingAddress.class);
            query.select(root);

            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public void deleteTopReceivingAddress(TopReceivingAddress topReceivingAddress) {
        Session session = null;
        org.hibernate.Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            session.delete(topReceivingAddress);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }
}
