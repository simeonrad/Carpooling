create table roles
(
    role_id   int auto_increment
        primary key,
    role_name varchar(255) not null,
    constraint role_name
        unique (role_name)
);

create table statuses
(
    status_id int auto_increment
        primary key,
    status    char(255) not null
);

create table users
(
    user_id      int auto_increment
        primary key,
    username     varchar(20)                                                                                                        not null,
    password     varchar(255)                                                                                                       not null,
    first_name   varchar(20)                                                                                                        not null,
    last_name    varchar(20)                                                                                                        not null,
    email        varchar(255)                                                                                                       not null,
    phone_number varchar(10)                                                                                                        not null,
    photo_url    char(255) default 'https://i.ibb.co/3dVFMxL/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg' not null,
    user_role    int       default 1                                                                                                not null,
    is_deleted   tinyint(1)                                                                                                         not null,
    is_blocked   tinyint(1)                                                                                                         not null,
    constraint email
        unique (email),
    constraint phone_number
        unique (phone_number),
    constraint username
        unique (username),
    constraint users_roles_role_id_fk
        foreign key (user_role) references roles (role_id)
);

create table travels
(
    travel_id      int auto_increment
        primary key,
    organizer_id   int           not null,
    start_point    varchar(255)  not null,
    end_point      varchar(255)  not null,
    departure_time datetime      not null,
    free_spots     int           not null,
    travel_status  int default 1 null,
    constraint travels_ibfk_1
        foreign key (organizer_id) references users (user_id),
    constraint travels_travel_statuses_status_id_fk
        foreign key (travel_status) references statuses (status_id)
);

create table applications
(
    application_id int auto_increment
        primary key,
    travel_id      int           not null,
    passenger_id   int           not null,
    status         int default 6 not null,
    constraint applications_ibfk_1
        foreign key (travel_id) references travels (travel_id),
    constraint applications_ibfk_2
        foreign key (passenger_id) references users (user_id),
    constraint applications_statuses_status_id_fk
        foreign key (status) references statuses (status_id)
);

create index passenger_id
    on applications (passenger_id);

create index travel_id
    on applications (travel_id);

create table feedbacks
(
    feedback_id  int auto_increment
        primary key,
    travel_id    int not null,
    author_id    int not null,
    recipient_id int not null,
    rating       int not null
        check (`rating` between 0 and 5),
    constraint feedbacks_ibfk_1
        foreign key (travel_id) references travels (travel_id),
    constraint feedbacks_ibfk_2
        foreign key (author_id) references users (user_id),
    constraint feedbacks_ibfk_3
        foreign key (recipient_id) references users (user_id)
);

create table feedback_comments
(
    comment_id  int auto_increment
        primary key,
    feedback_id int  not null,
    comment     text not null,
    constraint feedback_comments_ibfk_1
        foreign key (feedback_id) references feedbacks (feedback_id)
);

create index feedback_id
    on feedback_comments (feedback_id);

create index author_id
    on feedbacks (author_id);

create index recipient_id
    on feedbacks (recipient_id);

create index travel_id
    on feedbacks (travel_id);

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

