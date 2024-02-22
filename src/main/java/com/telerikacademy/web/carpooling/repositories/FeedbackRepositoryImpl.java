package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.Feedback;
import com.telerikacademy.web.carpooling.models.FilterFeedbackOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FeedbackRepositoryImpl implements FeedbackRepository{

    private final SessionFactory sessionFactory;

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
                    filters.add("author like :author");
                    params.put("author", String.format("%%%s%%", value));
                }
            });

            filterFeedbackOptions.getRecipient().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("recipient like :recipient");
                    params.put("recipient", String.format("%%%s%%", value));
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
}
