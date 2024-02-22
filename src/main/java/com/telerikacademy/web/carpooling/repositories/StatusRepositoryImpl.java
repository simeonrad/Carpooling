package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Status;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class StatusRepositoryImpl implements StatusRepository {

    private final SessionFactory sessionFactory;
@Autowired
    public StatusRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
@Override
    public Status getByValue(String value){
        try (Session session = sessionFactory.openSession()) {
            Query<Status> query = session.createQuery("from Status where status = :value", Status.class);
            query.setParameter("value", value);
            if (query.list().isEmpty())
                throw new EntityNotFoundException("Status","value", value);
            return query.list().get(0);
        }
    }
}
