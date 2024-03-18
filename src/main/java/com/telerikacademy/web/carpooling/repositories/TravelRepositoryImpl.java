package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

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
            session.remove(travel);
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
                filters.add("status.status = :travelStatus");
                params.put("travelStatus", "%" + travelStatus + "%");
            });

            StringBuilder queryString = new StringBuilder("from Travel t");
            if (!filters.isEmpty()) {
                queryString.append(" where ").append(String.join(" and ", filters));
            }

            filterOptions.getSortBy().ifPresent(sortBy -> {
                List<String> validSortProperties = List.of("departureTime", "freeSpots", "startPoint", "endPoint");
                String sortOrder = filterOptions.getSortOrder().orElse("asc").toLowerCase();

                if (!sortOrder.equals("asc") && !sortOrder.equals("desc")) {
                    sortOrder = "asc";
                }

                if (validSortProperties.contains(sortBy)) {
                    queryString.append(" order by t.").append(sortBy).append(" ").append(sortOrder);
                }
            });

            Query<Travel> query = session.createQuery(queryString.toString(), Travel.class);
            params.forEach(query::setParameter);

            return query.list();
        }
    }

    @Override
    public Page<Travel> getMyTravels(FilterTravelOptions filterOptions, Pageable pageable) {
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
                    filters.add("startPoint.value like :startPoint");
                    params.put("startPoint", "%" + startPoint + "%");
                }
            });

            filterOptions.getEndPoint().ifPresent(endPoint -> {
                if (!endPoint.isBlank()) {
                    filters.add("endPoint.value like :endPoint");
                    params.put("endPoint", "%" + endPoint + "%");
                }
            });

            LocalDateTime currentTime = LocalDateTime.now();
            filters.add("departureTime >= :currentTime");
            params.put("currentTime", currentTime);


            filterOptions.getFreeSpots().ifPresent(freeSpots -> {
                filters.add("freeSpots = :freeSpots");
                params.put("freeSpots", freeSpots);
            });

            filterOptions.getTravelStatus()
                    .filter(travelStatus -> !travelStatus.isBlank())
                    .ifPresent(travelStatus -> {
                        ApplicationStatus travelStatusEnum = ApplicationStatus.valueOf(travelStatus.toUpperCase());
                        filters.add("status.status = :travelStatus");
                        params.put("travelStatus", travelStatusEnum);
                    });

            StringBuilder queryString = new StringBuilder("from Travel t ");
            StringBuilder countQueryString = new StringBuilder("select count(t) from Travel t ");

            if (!filters.isEmpty()) {
                String whereClause = "where " + String.join(" and ", filters);
                queryString.append(whereClause);
                countQueryString.append(whereClause);
            }

            filterOptions.getSortBy().ifPresent(sortBy -> {
                List<String> validSortProperties = List.of("departureTime", "freeSpots", "startPoint", "endPoint");
                String sortOrder = filterOptions.getSortOrder().orElse("asc").toLowerCase();

                if (!sortOrder.equals("asc") && !sortOrder.equals("desc")) {
                    sortOrder = "asc";
                }

                if (validSortProperties.contains(sortBy)) {
                    queryString.append(" order by t.").append(sortBy).append(" ").append(sortOrder);
                }
            });

            Query<Long> countQuery = session.createQuery(countQueryString.toString(), Long.class);
            params.forEach(countQuery::setParameter);
            long total = countQuery.uniqueResult();

            Query<Travel> query = session.createQuery(queryString.toString(), Travel.class);
            params.forEach(query::setParameter);

            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());

            List<Travel> travels = query.list();
            return new PageImpl<>(travels, pageable, total);
        }
    }

    @Override
    public Page<Travel> getAllMyTravels(FilterTravelOptions filterOptions, Pageable pageable) {
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
                filters.add("t.departureTime = :departureTime");
                params.put("departureTime", departureTime);
            });


            filterOptions.getFreeSpots().ifPresent(freeSpots -> {
                filters.add("freeSpots = :freeSpots");
                params.put("freeSpots", freeSpots);
            });

            filterOptions.getTravelStatus()
                    .filter(travelStatus -> !travelStatus.isBlank())
                    .ifPresent(travelStatus -> {
                        ApplicationStatus travelStatusEnum = ApplicationStatus.valueOf(travelStatus.toUpperCase());
                        filters.add("status.status = :travelStatus");
                        params.put("travelStatus", travelStatusEnum);
                    });

            StringBuilder queryString = new StringBuilder("from Travel t ");
            StringBuilder countQueryString = new StringBuilder("select count(t) from Travel t ");

            if (!filters.isEmpty()) {
                String whereClause = "where " + String.join(" and ", filters);
                queryString.append(whereClause);
                countQueryString.append(whereClause);
            }

            filterOptions.getSortBy().ifPresent(sortBy -> {
                List<String> validSortProperties = List.of("departureTime", "freeSpots", "startPoint", "endPoint");
                String sortOrder = filterOptions.getSortOrder().orElse("asc").toLowerCase();

                if (!sortOrder.equals("asc") && !sortOrder.equals("desc")) {
                    sortOrder = "asc";
                }

                if (validSortProperties.contains(sortBy)) {
                    queryString.append(" order by t.").append(sortBy).append(" ").append(sortOrder);
                }
            });

            Query<Long> countQuery = session.createQuery(countQueryString.toString(), Long.class);
            params.forEach(countQuery::setParameter);
            long total = countQuery.uniqueResult();

            Query<Travel> query = session.createQuery(queryString.toString(), Travel.class);
            params.forEach(query::setParameter);

            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());

            List<Travel> travels = query.list();
            return new PageImpl<>(travels, pageable, total);
        }
    }

    @Override
    public Page<Travel> getTravelsIParticipatedIn(FilterTravelOptions filterOptions, Pageable pageable, int currentUserId) {
        try (Session session = sessionFactory.openSession()) {
            Map<String, Object> params = new HashMap<>();
            params.put("currentUserId", currentUserId);
            params.put("travelStatusId", 2);

            StringBuilder queryString = new StringBuilder("select t from Travel t ");
            queryString.append("inner join t.applications a ");
            queryString.append("where a.passenger.id = :currentUserId ");

            List<String> filters = new ArrayList<>();

            filterOptions.getAuthor().ifPresent(author -> {
                if (!author.isBlank()) {
                    filters.add("t.driver.username like :author");
                    params.put("author", "%" + author + "%");
                }
            });

            filterOptions.getStartPoint().ifPresent(startPoint -> {
                if (!startPoint.isBlank()) {
                    filters.add("t.startPoint like :startPoint");
                    params.put("startPoint", "%" + startPoint + "%");
                }
            });

            filterOptions.getEndPoint().ifPresent(endPoint -> {
                if (!endPoint.isBlank()) {
                    filters.add("t.endPoint like :endPoint");
                    params.put("endPoint", "%" + endPoint + "%");
                }
            });

            filterOptions.getDepartureTime().ifPresent(departureTime -> {
                filters.add("t.departureTime = :departureTime");
                params.put("departureTime", departureTime);
            });

            filterOptions.getFreeSpots().ifPresent(freeSpots -> {
                filters.add("t.freeSpots = :freeSpots");
                params.put("freeSpots", freeSpots);
            });

            filters.add("t.status.id = :travelStatusId");

            if (!filters.isEmpty()) {
                queryString.append("and ").append(String.join(" and ", filters)).append(" ");
            }

            filterOptions.getSortBy().ifPresent(sortBy -> {
                String sortOrder = filterOptions.getSortOrder().orElse("asc").toLowerCase();
                queryString.append("order by t.").append(sortBy).append(" ").append(sortOrder);
            });

            Query<Travel> query = session.createQuery(queryString.toString(), Travel.class);
            params.forEach(query::setParameter);
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            List<Travel> travels = query.getResultList();

            String countQueryString = queryString.toString().replaceFirst("select t", "select count(t)");
            Query<Long> countQuery = session.createQuery(countQueryString, Long.class);
            params.forEach(countQuery::setParameter);
            long total = countQuery.getSingleResult();

            return new PageImpl<>(travels, pageable, total);
        }
    }


    @Override
    public Page<Travel> get(FilterTravelOptions filterOptions, Pageable pageable) {
        return null;
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

    @Override
    public boolean isUserAParticipantInTravel(int userId, int travelId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from TravelApplication where travel.id = :travelId and passenger.id = :userId and status.id = :completedStatus";

            Query<TravelApplication> query = session.createQuery(hql, TravelApplication.class);
            query.setParameter("userId", userId);
            query.setParameter("travelId", travelId);
            query.setParameter("completedStatus", 2);

            return !query.list().isEmpty();
        }
    }



    @Override
    public boolean hasUserAlreadyGiveFeedbackForTheRecipient(int authorId, int recipientId, int travelId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from Feedback where travel.id = :travelId and author.id = :authorId and recipient.id = :recipientId";

            Query<Feedback> query = session.createQuery(hql, Feedback.class);
            query.setParameter("authorId", authorId);
            query.setParameter("recipientId", recipientId);
            query.setParameter("travelId", travelId);

            return query.list().isEmpty();
        }
    }

    @Override
    public boolean driverHasAlreadyGivenFeedbackForTheRecipient(int driverId, int recipientId, int travelId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from Feedback where travel.id = :travelId and travel.driver.id = :driverId and recipient.id = :recipientId";

            Query<Feedback> query = session.createQuery(hql, Feedback.class);
            query.setParameter("driverId", driverId);
            query.setParameter("recipientId", recipientId);
            query.setParameter("travelId", travelId);

            return !query.list().isEmpty();
        }
    }

    @Override
    public boolean isRecipientAParticipantInTravel(int recipientId, int travelId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from TravelApplication where travel.id = :travelId and passenger.id = :recipientId and status.id = :approvedStatus";

            Query<TravelApplication> query = session.createQuery(hql, TravelApplication.class);
            query.setParameter("recipientId", recipientId);
            query.setParameter("travelId", travelId);
            query.setParameter("approvedStatus", 4);

            return query.list().isEmpty();
        }
    }
}
