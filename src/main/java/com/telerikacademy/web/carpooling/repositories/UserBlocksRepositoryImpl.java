package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.UserBlock;
import com.telerikacademy.web.carpooling.repositories.contracts.UserBlocksRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class UserBlocksRepositoryImpl implements UserBlocksRepository {
    private final SessionFactory sessionFactory;
    private final EntityManager entityManager;

    @Autowired
    public UserBlocksRepositoryImpl(SessionFactory sessionFactory, EntityManager entityManager) {
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
        LocalDateTime currentTimestamp = LocalDateTime.now();
        String query = "SELECT count(u) > 0 FROM UserBlock u WHERE u.user.id = :userId AND u.blockExpireTimestamp > :currentTimestamp";

        return entityManager.createQuery(query, Boolean.class)
                .setParameter("userId", userId)
                .setParameter("currentTimestamp", currentTimestamp)
                .getSingleResult();
    }

    @Override
    public UserBlock get(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<UserBlock> query = session.createQuery("from UserBlock where user.id = :userId", UserBlock.class);
            query.setParameter("userId", userId);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("User", userId, "blocked");
            }
            return query.list().get(0);
        }
    }
}
