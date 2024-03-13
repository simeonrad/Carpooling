package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Colour;
import com.telerikacademy.web.carpooling.repositories.contracts.ColourRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ColourRepositoryImpl implements ColourRepository {
    public static final String NO_COLOUR_FOUND = "No colour found";

    private final SessionFactory sessionFactory;

    public ColourRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void create(Colour colour) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(colour);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Colour colour) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(colour);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Colour colour) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(colour);
            session.getTransaction().commit();
        }
    }
    @Override
    public Colour getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Colour colour = session.get(Colour.class, id);
            if (colour == null) {
                throw new EntityNotFoundException("Colour", id);
            }
            return colour;
        }
    }
    @Override
    public Colour getByValue(String value) {
        try (Session session = sessionFactory.openSession()) {
            Query<Colour> query = session.createQuery("from Colour where name = :value ", Colour.class);
            query.setParameter("value", value);
            if (query.list().isEmpty())
                throw new EntityNotFoundException(NO_COLOUR_FOUND);
            return query.list().get(0);
        }
    }

    @Override
    public List<Colour> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Colour> query = session.createQuery("from Colour", Colour.class);
            if (query.list().isEmpty())
                throw new EntityNotFoundException(NO_COLOUR_FOUND);
            return query.list();
        }
    }
}

