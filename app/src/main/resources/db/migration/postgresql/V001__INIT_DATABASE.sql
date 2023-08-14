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

create table BORROWED_BOOK
(
	ID BIGSERIAL not null primary key,
	BORROWED_ON DATE,
	BOOK_ID BIGINT,
	constraint FK_BOOK_TO_BORROW
		foreign key (BOOK_ID) references BOOK (ID)
);
