package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.CarEngine;
import com.telerikacademy.web.carpooling.repositories.contracts.EngineRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EngineRepositoryImpl implements EngineRepository {

    public static final String NO_ENGINES_FOUND = "No engines found";

    private final SessionFactory sessionFactory;

    @Autowired
    public EngineRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public CarEngine getByValue(String value) {
        try (Session session = sessionFactory.openSession()) {
            Query<CarEngine> query = session.createQuery("from CarEngine where name = :value", CarEngine.class);
            query.setParameter("value", value);
            List<CarEngine> result = query.list();
            if (result.isEmpty())
                throw new EntityNotFoundException("Engine", "value", value);
            return result.get(0);
        }
    }
    @Override
    public List<CarEngine> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<CarEngine> query = session.createQuery("from CarEngine ", CarEngine.class);
            if (query.list().isEmpty())
                throw new EntityNotFoundException(NO_ENGINES_FOUND);
            return query.list();
        }
    }
}
