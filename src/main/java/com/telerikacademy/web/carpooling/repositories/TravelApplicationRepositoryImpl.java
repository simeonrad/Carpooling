package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TravelApplicationRepositoryImpl implements TravelApplicationRepository {
    public static final String NO_APPLICATIONS_FOUND = "No applications found";
    private final SessionFactory sessionFactory;
    @Autowired
    public TravelApplicationRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(TravelApplication application) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(application);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(TravelApplication application) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(application);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(TravelApplication application) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(application);
            session.getTransaction().commit();
        }
    }
    @Override
    public TravelApplication getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            TravelApplication application = session.get(TravelApplication.class, id);
            if (application == null) {
                throw new EntityNotFoundException("TravelApplication", id);
            }
            return application;
        }
    }

    @Override
    public List<TravelApplication> getByTravelId(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<TravelApplication> query = session.createQuery("from TravelApplication where travel.id = :id", TravelApplication.class);
            query.setParameter("id", id);
            if (query.list().isEmpty())
                throw new EntityNotFoundException("Application",id);
            return query.list();

        }
    }

    @Override
    public List<TravelApplication> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<TravelApplication> query = session.createQuery("from TravelApplication", TravelApplication.class);
            if (query.list().isEmpty())
                throw new EntityNotFoundException(NO_APPLICATIONS_FOUND);
            return query.list();
        }
    }

    @Override

    public List<TravelApplication> get(FilterApplicationOptions filterOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterOptions.getUsername().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("passenger.username like :username");
                    params.put("username", String.format("%%%s%%", value));
                }
            });

            filterOptions.getStatus().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("status.status = :status");
                    params.put("status", String.format("%s", value));
                }
            });

            StringBuilder queryString = new StringBuilder("from TravelApplication");
            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(filterOptions));

            Query<TravelApplication> query = session.createQuery(queryString.toString(), TravelApplication.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public Page<TravelApplication> getMyTravelApplications(FilterMyApplicationsOptions filterOptions, Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterOptions.getStartPoint().ifPresent(startPoint -> {
                if (!startPoint.isBlank()) {
                    filters.add("t.travel.startPoint like :startPoint");
                    params.put("startPoint", "%" + startPoint + "%");
                }
            });

            filterOptions.getEndPoint().ifPresent(endPoint -> {
                if (!endPoint.isBlank()) {
                    filters.add("t.travel.endPoint like :endPoint");
                    params.put("endPoint", "%" + endPoint + "%");
                }
            });

            filterOptions.getDepartureTime().ifPresent(departureTime -> {
                filters.add("t.travel.departureTime = :departureTime");
                params.put("departureTime", departureTime);
            });

            filterOptions.getDriver().ifPresent(driver -> {
                if (!driver.isBlank()) {
                    filters.add("t.travel.driver.username like :driver");
                    params.put("driver", "%" + driver + "%");
                }
            });


            StringBuilder queryString = new StringBuilder(
                    "select t from TravelApplication t " +
                            "join t.travel " +
                            "join t.travel.driver driver " +
                            "join t.status ");

            StringBuilder countQueryString = new StringBuilder(
                    "select count(t) from TravelApplication t " +
                            "join t.travel " +
                            "join t.travel.driver driver " +
                            "join t.status ");

            if (!filters.isEmpty()) {
                String whereClause = "where " + String.join(" and ", filters);
                queryString.append(whereClause);
                countQueryString.append(whereClause);
            }

            filterOptions.getSortBy().ifPresent(sortBy -> {
                List<String> validSortProperties = List.of("departureTime", "freeSpots", "startPoint", "endPoint", "travelStatus");
                String sortOrder = filterOptions.getSortOrder().orElse("asc").toLowerCase();

                if (!sortOrder.equals("asc") && !sortOrder.equals("desc")) {
                    sortOrder = "asc";
                }

                if (validSortProperties.contains(sortBy)) {
                    queryString.append(" order by t.travel.").append(sortBy).append(" ").append(sortOrder);
                }
            });

            Query<Long> countQuery = session.createQuery(countQueryString.toString(), Long.class);
            params.forEach(countQuery::setParameter);
            long total = countQuery.uniqueResult();

            Query<TravelApplication> query = session.createQuery(queryString.toString(), TravelApplication.class);
            params.forEach(query::setParameter);

            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());

            List<TravelApplication> applications = query.list();
            return new PageImpl<>(applications, pageable, total);
        }
    }



    private String generateOrderBy(FilterApplicationOptions filterOptions) {
        if (filterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = switch (filterOptions.getSortBy().get()) {
            case "status" -> "status";
            case "username" -> "passenger.username";
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
