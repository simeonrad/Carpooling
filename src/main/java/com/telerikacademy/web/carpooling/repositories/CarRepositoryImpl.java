package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Car;
import com.telerikacademy.web.carpooling.repositories.contracts.CarRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CarRepositoryImpl implements CarRepository {

    public static final String NO_CARS_FOUND = "No cars found";

    private final SessionFactory sessionFactory;

    @Autowired
    public CarRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void create(Car car) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(car);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Car car) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(car);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Car car) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(car);
            session.getTransaction().commit();
        }
    }
    @Override
    public Car getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Car car = session.get(Car.class, id);
            if (car == null) {
                throw new EntityNotFoundException("Car", id);
            }
            return car;
        }
    }

    @Override
    public List<Car> getByUserId(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Car> query = session.createQuery("from Car where owner.id = :id", Car.class);
            query.setParameter("id", id);
            if (query.list().isEmpty())
                throw new EntityNotFoundException(String.format("Car with owner id %s not found!", id));
            return query.list();

        }
    }

    @Override
    public List<Car> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Car> query = session.createQuery("from Car", Car.class);
            if (query.list().isEmpty())
                throw new EntityNotFoundException(NO_CARS_FOUND);
            return query.list();
        }
    }
}
