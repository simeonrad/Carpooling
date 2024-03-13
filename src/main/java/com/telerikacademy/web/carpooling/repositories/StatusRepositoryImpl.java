package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Status;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import com.telerikacademy.web.carpooling.repositories.contracts.StatusRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StatusRepositoryImpl implements StatusRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public StatusRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Status getByValue(ApplicationStatus value) {
        try (Session session = sessionFactory.openSession()) {
            Query<Status> query = session.createQuery("from Status where status = :value", Status.class);
            query.setParameter("value", value);
            List<Status> result = query.list();
            if (result.isEmpty())
                throw new EntityNotFoundException("Status", "value", value.toString());
            return result.get(0);
        }
    }
}
