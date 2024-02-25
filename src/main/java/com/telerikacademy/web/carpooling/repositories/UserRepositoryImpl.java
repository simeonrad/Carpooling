package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.IsDeleted;
import com.telerikacademy.web.carpooling.models.NonVerifiedUser;
import com.telerikacademy.web.carpooling.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void create(NonVerifiedUser nonVerifiedUser) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(nonVerifiedUser);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(IsDeleted isDeleted) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(isDeleted);
            session.getTransaction().commit();
        }
    }

    @Override
    public void unmarkAsDeleted(IsDeleted isDeleted) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(isDeleted);
            session.getTransaction().commit();
        }
    }

    @Override
    public IsDeleted getDeletedById(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<IsDeleted> query = session.createQuery("FROM IsDeleted WHERE user.id = :userId", IsDeleted.class);
            query.setParameter("userId", userId);
            IsDeleted isDeleted = query.uniqueResult();
            if (isDeleted == null) {
                throw new EntityNotFoundException("User", userId);
            }
            return isDeleted;
        }
    }



    @Override
    public void update(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public boolean isDeleted(int userId) {
        try(Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT count(*) FROM IsDeleted id WHERE id.user.id = :userId", Long.class);
            query.setParameter("userId", userId);
            long count = query.uniqueResult();
            return count > 0;
        }
    }

    @Override
    public List<User> get(FilterUserOptions filterOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterOptions.getUsername().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("username like :username");
                    params.put("username", String.format("%%%s%%", value));
                }
            });

            filterOptions.getEmail().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("email like :email");
                    params.put("email", String.format("%%%s%%", value));
                }
            });

            filterOptions.getPhoneNumber().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("phoneNumber = :phoneNumber");
                    params.put("phoneNumber", String.format("%s", value));
                }
            });

            StringBuilder queryString = new StringBuilder("from User");
            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(filterOptions));

            Query<User> query = session.createQuery(queryString.toString(), User.class);
            query.setProperties(params);
            return query.list();
        }
    }

    private String generateOrderBy(FilterUserOptions filterOptions) {
        if (filterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = switch (filterOptions.getSortBy().get()) {
            case "email" -> "email";
            case "phoneNumber" -> "phoneNumber";
            case "username" -> "username";
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
    public User getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }

    @Override
    public List<User> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            return query.list();
        }
    }

    @Override
    public List<User> getAllNotDeleted() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT u FROM User u LEFT JOIN IsDeleted d ON u.id = d.user.id WHERE d.user.id IS NULL";
            Query<User> query = session.createQuery(hql, User.class);
            return query.list();
        }
    }

    @Override
    public User getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);
            List<User> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("User", "username", username);
            }
            return result.get(0);
        }
    }

    @Override
    public User getByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", email);
            List<User> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("Email", "name", email);
            }
            return result.get(0);
        }
    }

    @Override
    public boolean updateEmail(String email, int currentUserId) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email = :email and id != :currentUserId", User.class);
            query.setParameter("email", email);
            query.setParameter("currentUserId", currentUserId);
            List<User> result = query.list();
            return !result.isEmpty();
        }
    }

    @Override
    public boolean telephoneExists(String phoneNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where phoneNumber = :phoneNumber", User.class);
            query.setParameter("phoneNumber", phoneNumber);
            List<User> result = query.list();
            return !result.isEmpty();
        }
    }

    @Override
    public NonVerifiedUser getNonVerifiedById(int userId) {
        try (Session session = sessionFactory.openSession()) {
            String queryString = "FROM NonVerifiedUser nv WHERE nv.id = :userId";
            Query<NonVerifiedUser> query = session.createQuery(queryString, NonVerifiedUser.class);
            query.setParameter("userId", userId);
            return query.uniqueResult();
        }
    }

    @Override
    public void verify(NonVerifiedUser nonVerifiedUser) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(nonVerifiedUser);
            session.getTransaction().commit();
        }
    }
}
