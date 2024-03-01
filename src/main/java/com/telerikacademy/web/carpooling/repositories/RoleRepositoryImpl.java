package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Role;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final SessionFactory sessionFactory;

    public RoleRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Role findByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Role> query = session.createQuery("FROM Role WHERE name = :name", Role.class);
            query.setParameter("name", name);
            List<Role> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("Role", "role", name);
            }
            return result.get(0);
        }
    }

    @Override
    public Role findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Role role = session.get(Role.class, id);
            if (role == null) {
                throw new EntityNotFoundException("Role", "role", String.valueOf(id));
            }
            return role;
        }
    }

    @Override
    public List<Role> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Role> query = session.createQuery("from Role ", Role.class);
            return query.list();
        }
    }
}
