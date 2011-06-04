# Initial schema

# --- !Ups

CREATE TABLE Album (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
	genre varchar(255) NOT NULL,
    nbVotes bigint(20),
    releaseDate datetime NOT NULL,
	FOREIGN KEY (artist_id) REFERENCES (Artist(id)),
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Album;