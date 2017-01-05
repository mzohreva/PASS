
create database if not exists pass;
use pass;

create table projects (
    id integer not null auto_increment,
    title varchar(255),
    assignDate date,
    dueDate datetime,
    gracePeriodHours integer,
    submissionInstructions longtext,
    visible bit,
    primary key (id)
);

create table submissions (
    id integer not null auto_increment,
    project_id integer,
    user_username varchar(255),
    submissionDate datetime,
    compileOptions varchar(255),
    compileSuccessful bit,
    compileMessage longtext,
    testResult longtext,
    primary key (id)
);

create table users (
    username varchar(255) not null,
    studentId varchar(255) not null,
    firstname varchar(255) not null,
    lastname varchar(255) not null,
    email varchar(255) not null,
    salt varchar(255),
    password tinyblob,
    verified bit,
    primary key (username)
);

create table verification_codes (
    code binary(16) not null,
    creationDate datetime,
    reason integer,
    user_username varchar(255),
    primary key (code)
);

alter table submissions
    add constraint FKe8r8d1xi83eopibrfiml7am0m
    foreign key (project_id)
    references projects (id);

alter table submissions
    add constraint FK1xt09aylypjv2nbayjppm1yux
    foreign key (user_username)
    references users (username);

alter table verification_codes
    add constraint FK6vfboxqny2fveou2k42edyvwo
    foreign key (user_username)
    references users (username);
