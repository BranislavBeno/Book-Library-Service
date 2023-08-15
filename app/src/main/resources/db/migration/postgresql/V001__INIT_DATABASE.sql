CREATE TABLE reader (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   first_name VARCHAR(255),
   last_name VARCHAR(255),
   CONSTRAINT pk_reader PRIMARY KEY (id)
);

CREATE TABLE book (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   name VARCHAR(255),
   author VARCHAR(255),
   CONSTRAINT pk_book PRIMARY KEY (id)
);

CREATE TABLE borrowed_book (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   borrowed_on date,
   book_id BIGINT,
   CONSTRAINT pk_borrowedbook PRIMARY KEY (id)
);

ALTER TABLE borrowed_book ADD CONSTRAINT FK_BORROWEDBOOK_ON_BOOK FOREIGN KEY (book_id) REFERENCES book (id);