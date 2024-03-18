package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Location;
import com.telerikacademy.web.carpooling.models.Make;
import com.telerikacademy.web.carpooling.repositories.contracts.MakeRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LocationRepositoryImpl implements com.telerikacademy.web.carpooling.repositories.contracts.LocationRepository {
    public static final String NO_LOCATION_FOUND = "No location found";

    private final SessionFactory sessionFactory;

    public LocationRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void create(Location location) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(location);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Location location) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(location);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Location location) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(location);
            session.getTransaction().commit();
        }
    }
    @Override
    public Location getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Location location = session.get(Location.class, id);
            if (location == null) {
                throw new EntityNotFoundException("Location", id);
            }
            return location;
        }
    }
    @Override
    public Location getByValue(String value) {
        try (Session session = sessionFactory.openSession()) {
            Query<Location> query = session.createQuery("from Location where value = :name", Location.class);
            query.setParameter("name", value);
            if (query.list().isEmpty())
                throw new EntityNotFoundException(NO_LOCATION_FOUND);
            return query.list().get(0);
        }
    }

    @Override
    public List<Location> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Location> query = session.createQuery("from Location", Location.class);
            if (query.list().isEmpty())
                throw new EntityNotFoundException(NO_LOCATION_FOUND);
            return query.list();
        }
    }
}

