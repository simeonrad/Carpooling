package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.TravelApplication;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TravelApplicationRepositoryImpl implements TravelApplicationRepository {
    public static final String NO_APPLICATIONS_FOUND = "No applications found";
    private final SessionFactory sessionFactory;

    @Autowired
    public TravelApplicationRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(TravelApplication application) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(application);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(TravelApplication application) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(application);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(TravelApplication application) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(application);
            session.getTransaction().commit();
        }
    }
    @Override
    public TravelApplication getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            TravelApplication application = session.get(TravelApplication.class, id);
            if (application == null) {
                throw new EntityNotFoundException("TravelApplication", id);
            }
            return application;
        }
    }

    @Override
    public List<TravelApplication> getByTravelId(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<TravelApplication> query = session.createQuery("from TravelApplication where travel.id = :id", TravelApplication.class);
            query.setParameter("id", id);
            if (query.list().isEmpty())
                throw new EntityNotFoundException("Application",id);
            return query.list();

        }
    }

    @Override
    public List<TravelApplication> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<TravelApplication> query = session.createQuery("from TravelApplication", TravelApplication.class);
            if (query.list().isEmpty())
                throw new EntityNotFoundException(NO_APPLICATIONS_FOUND);
            return query.list();
        }
    }
}
