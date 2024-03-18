insert into statuses (status_id, status) VALUES (1, 'PLANNED'), (2, 'COMPLETED'), (3, 'CANCELED'), (4, 'APPROVED'),
                                                (5, 'DECLINED'), (6, 'PENDING');

insert into roles (role_id, role_name) VALUES (1, 'Regular user'), (2, 'Admin'), (3, 'Staff');

INSERT INTO car_colours (colour_name) VALUES ('Red'), ('Blue'), ('Green');

INSERT INTO car_makes (make) VALUES ('Toyota'), ('Ford'), ('Honda');

insert into engine_types (engine_type_id, type) VALUES (1, 'Diesel'), (2, 'Petrol'), (3, 'Hybrid'), (4, 'Electric');

INSERT INTO locations (value) VALUES ('New York'), ('Los Angeles'), ('Chicago');

INSERT INTO users (username, password, first_name, last_name, email, phone_number, user_role)
VALUES
('john_doe', 'password123', 'John', 'Doe', 'john.doe@example.com', '1234567890', 1),
('jane_smith', 'password123', 'Jane', 'Smith', 'jane.smith@example.com', '0987654321', 1);

INSERT INTO cars (make_id, licence_plate, owner_id, colour_id, engine_type) VALUES
(1, 'ABC123', 1, 1, 1),
(2, 'XYZ789', 2, 2, 2);

INSERT INTO is_verified (user_id, is_verified) VALUES (1, 1), (2, 0);

INSERT INTO travels (organizer_id, start_point, end_point, departure_time, free_spots, travel_status, duration_minutes, distance_km, car_id) VALUES
    (1, 1, 2, '2024-01-01 08:00:00', 3, 1, 120, 100, 1);