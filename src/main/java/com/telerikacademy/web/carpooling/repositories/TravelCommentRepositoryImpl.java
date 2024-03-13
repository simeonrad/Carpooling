package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.TravelComment;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelCommentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class TravelCommentRepositoryImpl implements TravelCommentRepository {
    private final SessionFactory sessionFactory;

    public TravelCommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void saveOrUpdateComment(TravelComment travelComment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(travelComment);
            session.getTransaction().commit();
        }
    }

    @Override
    public TravelComment findByTravelId(int travelId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from TravelComment where travel.id = :travelId";
            Query<TravelComment> query = session.createQuery(hql, TravelComment.class);
            query.setParameter("travelId", travelId);
            return query.uniqueResult();
        }
    }

    @Override
    public void deleteCommentByTravelId(int travelId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            TravelComment travelComment = findByTravelId(travelId);
            if (travelComment != null) {
                session.delete(travelComment);
            }
            session.getTransaction().commit();
        }
    }
}
