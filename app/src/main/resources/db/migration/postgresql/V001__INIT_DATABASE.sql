create table READER
(
	ID BIGSERIAL not null primary key,
	FIRST_NAME VARCHAR(255),
	LAST_NAME VARCHAR(255)
);

create table BOOK
(
	ID BIGSERIAL not null primary key,
	NAME VARCHAR(255),
	AUTHOR VARCHAR(255)
);
