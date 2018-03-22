CREATE DATABASE IF NOT EXISTS photo default charset utf8 COLLATE utf8_general_ci;

CREATE TABLE photo.ugi(
	sha1 char(40) PRIMARY KEY NOT NULL,
	tags text,
	story text,
	updated_on bigint NOT NULL
);

CREATE TABLE photo.meta_indexed(
	sha1 char(40) PRIMARY KEY NOT NULL,
	path text NOT NULL,
	indexed_on bigint NOT NULL
);