package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.UserBlock;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;


@Repository
public class UserBlocksRepositoryImpl implements UserBlocksRepository {
    private SessionFactory sessionFactory;
    private EntityManager entityManager;
@Autowired
    public UserBlocksRepositoryImpl(SessionFactory sessionFactory,EntityManager entityManager) {
        this.sessionFactory = sessionFactory;
        this.entityManager = entityManager;
    }

    @Override
    public void create(UserBlock userBlock) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(userBlock);
            session.getTransaction().commit();
        }
    }
 @Override
 public void delete(UserBlock userBlock) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(userBlock);
            session.getTransaction().commit();
        }
    }
 @Override
 public void update(UserBlock userBlock) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(userBlock);
            session.getTransaction().commit();
        }
    }

    @Override
    public boolean isUserBlocked(int userId) {
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault());
        String query = "SELECT count(*) > 0 FROM UserBlock WHERE user.id = :userId AND blockExpireTimestamp > :currentTimestamp";
        boolean isBlocked = entityManager.createQuery(query, Boolean.class)
                .setParameter("userId", userId)
                .setParameter("currentTimestamp", currentDateTime)
                .getSingleResult();
        return isBlocked;
    }

    @Override
    public UserBlock get(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<UserBlock> query = session.createQuery("from UserBlock where user.id = :userId", UserBlock.class);
            if (query.list().isEmpty()){
                throw new EntityNotFoundException("UserBlock", userId);
            }
            return query.list().get(0);
        }
    }
}
