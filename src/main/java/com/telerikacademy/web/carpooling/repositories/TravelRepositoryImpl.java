package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.FilterTravelOptions;
import com.telerikacademy.web.carpooling.models.Travel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TravelRepositoryImpl implements TravelRepository {
    private final SessionFactory sessionFactory;

    public TravelRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Travel travel) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(travel);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Travel travel) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(travel);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Travel travel) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(travel);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Travel> get(FilterTravelOptions filterOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterOptions.getAuthor().ifPresent(author -> {
                if (!author.isBlank()) {
                    filters.add("driver.username like :author");
                    params.put("author", "%" + author + "%");
                }
            });

            filterOptions.getStartPoint().ifPresent(startPoint -> {
                if (!startPoint.isBlank()) {
                    filters.add("startPoint like :startPoint");
                    params.put("startPoint", "%" + startPoint + "%");
                }
            });

            filterOptions.getEndPoint().ifPresent(endPoint -> {
                if (!endPoint.isBlank()) {
                    filters.add("endPoint like :endPoint");
                    params.put("endPoint", "%" + endPoint + "%");
                }
            });

            filterOptions.getDepartureTime().ifPresent(departureTime -> {
                filters.add("departureTime = :departureTime");
                params.put("departureTime", departureTime);
            });

            filterOptions.getFreeSpots().ifPresent(freeSpots -> {
                filters.add("freeSpots = :freeSpots");
                params.put("freeSpots", freeSpots);
            });

            filterOptions.getTravelStatus().ifPresent(travelStatus -> {
                filters.add("status.status like :travelStatus");
                params.put("travelStatus", "%" + travelStatus + "%");
            });

            StringBuilder queryString = new StringBuilder("from Travel t");
            if (!filters.isEmpty()) {
                queryString.append(" where ").append(String.join(" and ", filters));
            }

            filterOptions.getSortBy().ifPresent(sortBy -> {
                String sortOrder = filterOptions.getSortOrder().orElse("asc");
                queryString.append(" order by t.").append(sortBy).append(" ").append(sortOrder);
            });

            Query<Travel> query = session.createQuery(queryString.toString(), Travel.class);
            params.forEach(query::setParameter);

            return query.list();
        }
    }


    @Override
    public List<Travel> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Travel> query = session.createQuery("from Travel", Travel.class);
            return query.list();
        }
    }

    @Override
    public Travel getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Travel travel = session.get(Travel.class, id);
            if (travel == null) {
                throw new EntityNotFoundException("Travel", id);
            }
            return travel;
        }
    }

    public List<Travel> findByStartAndEndPoint(String startPoint, String endPoint) {
        try (Session session = sessionFactory.openSession()) {
            Query<Travel> query = session.createQuery("from Travel t where t.startPoint = :startPoint and t.endPoint = :endPoint", Travel.class);
            query.setParameter("startPoint", startPoint);
            query.setParameter("endPoint", endPoint);
            return query.list();
        }
    }
}
