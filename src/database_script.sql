create table roles
(
    role_id   int auto_increment
        primary key,
    role_name varchar(255) not null,
    constraint role_name
        unique (role_name)
);

create table users
(
    user_id      int auto_increment
        primary key,
    username     varchar(20)  not null,
    password     varchar(255) not null,
    first_name   varchar(20)  not null,
    last_name    varchar(20)  not null,
    email        varchar(255) not null,
    phone_number varchar(10)  not null,
    constraint email
        unique (email),
    constraint phone_number
        unique (phone_number),
    constraint username
        unique (username)
);

create table travels
(
    travel_id      int auto_increment
        primary key,
    organizer_id   int          not null,
    start_point    varchar(255) not null,
    end_point      varchar(255) not null,
    departure_time datetime     not null,
    free_spots     int          not null,
    constraint travels_ibfk_1
        foreign key (organizer_id) references users (user_id)
);

create table applications
(
    application_id int auto_increment
        primary key,
    travel_id      int                                                   not null,
    passenger_id   int                                                   not null,
    status         enum ('pending', 'approved', 'declined', 'cancelled') not null,
    constraint applications_ibfk_1
        foreign key (travel_id) references travels (travel_id),
    constraint applications_ibfk_2
        foreign key (passenger_id) references users (user_id)
);

create table application_cancellation
(
    cancellation_id int auto_increment
        primary key,
    application_id  int                          not null,
    cancelled_by    enum ('driver', 'passenger') not null,
    constraint application_id
        unique (application_id),
    constraint application_cancellation_ibfk_1
        foreign key (application_id) references applications (application_id)
);

create index passenger_id
    on applications (passenger_id);

create index travel_id
    on applications (travel_id);

create table feedback
(
    feedback_id  int auto_increment
        primary key,
    travel_id    int not null,
    author_id    int not null,
    recipient_id int not null,
    rating       int not null
        check (`rating` between 0 and 5),
    constraint feedback_ibfk_1
        foreign key (travel_id) references travels (travel_id),
    constraint feedback_ibfk_2
        foreign key (author_id) references users (user_id),
    constraint feedback_ibfk_3
        foreign key (recipient_id) references users (user_id)
);

create index author_id
    on feedback (author_id);

create index recipient_id
    on feedback (recipient_id);

create index travel_id
    on feedback (travel_id);

create table feedback_comments
(
    comment_id  int auto_increment
        primary key,
    feedback_id int  not null,
    comment     text not null,
    constraint feedback_comments_ibfk_1
        foreign key (feedback_id) references feedback (feedback_id)
);

create index feedback_id
    on feedback_comments (feedback_id);

create table travel_comments
(
    comment_id int auto_increment
        primary key,
    travel_id  int  not null,
    comment    text not null,
    constraint travel_comments_ibfk_1
        foreign key (travel_id) references travels (travel_id)
);

create index travel_id
    on travel_comments (travel_id);

create index organizer_id
    on travels (organizer_id);

create table trip_cancellations
(
    cancellation_id   int auto_increment
        primary key,
    travel_id         int                                   not null,
    cancelled_by      enum ('driver', 'passenger')          not null,
    cancellation_time timestamp default current_timestamp() null,
    constraint travel_id
        unique (travel_id),
    constraint trip_cancellations_ibfk_1
        foreign key (travel_id) references travels (travel_id)
);

create table trip_status
(
    status_id int auto_increment
        primary key,
    travel_id int                                        not null,
    status    enum ('planned', 'completed', 'cancelled') not null,
    constraint travel_id
        unique (travel_id),
    constraint trip_status_ibfk_1
        foreign key (travel_id) references travels (travel_id)
);

create table user_roles
(
    user_id int not null,
    role_id int not null,
    primary key (user_id, role_id),
    constraint user_roles_ibfk_1
        foreign key (user_id) references users (user_id),
    constraint user_roles_ibfk_2
        foreign key (role_id) references roles (role_id)
);

create index role_id
    on user_roles (role_id);

