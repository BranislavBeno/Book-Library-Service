-- Reader
CREATE SEQUENCE IF NOT EXISTS reader_seq START WITH 20 INCREMENT BY 1;

CREATE TABLE reader (
  id INTEGER NOT NULL,
   first_name VARCHAR(255),
   last_name VARCHAR(255),
   email VARCHAR(255),
   CONSTRAINT pk_reader PRIMARY KEY (id)
);

-- Book
CREATE SEQUENCE IF NOT EXISTS book_seq START WITH 20 INCREMENT BY 1;

CREATE TABLE book (
  id INTEGER NOT NULL,
   name VARCHAR(255),
   author VARCHAR(255),
   CONSTRAINT pk_book PRIMARY KEY (id)
);

-- Borrowed book
CREATE SEQUENCE IF NOT EXISTS borrowed_book_seq START WITH 20 INCREMENT BY 1;

CREATE TABLE borrowed_book (
  id INTEGER NOT NULL,
   borrowed_on date,
   book_id INTEGER,
   reader_id INTEGER,
   CONSTRAINT pk_borrowed_book PRIMARY KEY (id)
);

ALTER TABLE borrowed_book ADD CONSTRAINT FK_BORROWED_BOOK_ON_BOOK FOREIGN KEY (book_id) REFERENCES book (id);

ALTER TABLE borrowed_book ADD CONSTRAINT FK_BORROWED_BOOK_ON_READER FOREIGN KEY (reader_id) REFERENCES reader (id);
