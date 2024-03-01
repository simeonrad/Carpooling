package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.Feedback;
import com.telerikacademy.web.carpooling.models.FilterFeedbackOptions;
import com.telerikacademy.web.carpooling.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FeedbackRepositoryImpl implements FeedbackRepository{

    private final SessionFactory sessionFactory;
    @PersistenceContext
    private EntityManager entityManager;

    public FeedbackRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Feedback feedback) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(feedback);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Feedback feedback) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(feedback);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Feedback feedback) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(feedback);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Feedback> getFeedbacksForUser(FilterFeedbackOptions filterFeedbackOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterFeedbackOptions.getAuthor().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("author.username like :authorUsername");
                    params.put("authorUsername", "%" + value + "%");
                }
            });

            filterFeedbackOptions.getRecipient().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("recipient.username like :recipientUsername");
                    params.put("recipientUsername", "%" + value + "%");
                }
            });


            StringBuilder queryString = new StringBuilder("from Feedback");
            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(filterFeedbackOptions));

            Query<Feedback> query = session.createQuery(queryString.toString(), Feedback.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public Page<Feedback> getMyReceivedFeedbacks(User user, Pageable pageable) {
        String fetchQuery = "SELECT f FROM Feedback f WHERE f.recipient = :user ORDER BY f.id DESC";
        List<Feedback> feedbacks = entityManager.createQuery(fetchQuery, Feedback.class)
                .setParameter("user", user)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        String countQuery = "SELECT COUNT(f) FROM Feedback f WHERE f.recipient = :user";
        long totalFeedbackCount = entityManager.createQuery(countQuery, Long.class)
                .setParameter("user", user)
                .getSingleResult();

        return new PageImpl<>(feedbacks, pageable, totalFeedbackCount);    }

    private String generateOrderBy(FilterFeedbackOptions filterOptions) {
        if (filterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = switch (filterOptions.getSortBy().get()) {
            case "author" -> "author";
            case "recipient" -> "recipient";
            default -> "id";
        };

        if (orderBy.isEmpty()) {
            return "";
        }

        orderBy = String.format(" order by %s", orderBy);

        if (filterOptions.getSortOrder().isPresent() && filterOptions.getSortOrder().get()
                .equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }

    @Override
    public Feedback getByTravelId(int travelId, int authorId, int recipientId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from Feedback where travel.id = :travelId and author.id = :authorId and recipient.id = :recipientId";
            Query<Feedback> query = session.createQuery(hql, Feedback.class);
            query.setParameter("travelId", travelId);
            query.setParameter("authorId", authorId);
            query.setParameter("recipientId", recipientId);
            return query.uniqueResult();
        }
    }
}
