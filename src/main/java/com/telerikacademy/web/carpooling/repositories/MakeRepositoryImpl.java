package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Make;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MakeRepositoryImpl implements MakeRepository {
    public static final String NO_MAKE_FOUND = "No make found";

    private final SessionFactory sessionFactory;

    public MakeRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void create(Make make) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(make);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Make make) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(make);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Make make) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(make);
            session.getTransaction().commit();
        }
    }
    @Override
    public Make getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Make make = session.get(Make.class, id);
            if (make == null) {
                throw new EntityNotFoundException("Make", id);
            }
            return make;
        }
    }
    @Override
    public Make getByValue(String value) {
        try (Session session = sessionFactory.openSession()) {
            Query<Make> query = session.createQuery("from Make where name = :name", Make.class);
            query.setParameter("name", value);
            if (query.list().isEmpty())
                throw new EntityNotFoundException(NO_MAKE_FOUND);
            return query.list().get(0);
        }
    }

    @Override
    public List<Make> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Make> query = session.createQuery("from Make", Make.class);
            if (query.list().isEmpty())
                throw new EntityNotFoundException(NO_MAKE_FOUND);
            return query.list();
        }
    }
}

