package org.example.dao;

import org.example.model.TopAddress;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class TopAddressDaoImpl implements TopAddressDao {

    @Override
    public void saveOrUpdate(TopAddress topAddress) {
        Session session = null;
        org.hibernate.Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            session.saveOrUpdate(topAddress);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public List<TopAddress> getAllTopAddresses() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<TopAddress> query = builder.createQuery(TopAddress.class);
            Root<TopAddress> root = query.from(TopAddress.class);
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
    public void deleteTopAddress(TopAddress topAddress) {
        Session session = null;
        org.hibernate.Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            session.delete(topAddress);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }
}
