# Initial schema

# --- !Ups

CREATE TABLE Artist (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    UNIQUE (name),
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Artist;