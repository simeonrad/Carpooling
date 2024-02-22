package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Travel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class TravelRepositoryImpl implements TravelRepository {
    private final SessionFactory sessionFactory;

    public TravelRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Travel travel) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(travel);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Travel travel) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(travel);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Travel travel) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(travel);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Travel> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Travel> query = session.createQuery("from Travel", Travel.class);
            return query.list();
        }
    }

    @Override
    public Travel getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Travel travel = session.get(Travel.class, id);
            if (travel == null) {
                throw new EntityNotFoundException("Travel", id);
            }
            return travel;
        }
    }

    public List<Travel> findByStartAndEndPoint(String startPoint, String endPoint) {
        try (Session session = sessionFactory.openSession()) {
            Query<Travel> query = session.createQuery("from Travel t where t.startPoint = :startPoint and t.endPoint = :endPoint", Travel.class);
            query.setParameter("startPoint", startPoint);
            query.setParameter("endPoint", endPoint);
            return query.list();
        }
    }
}
