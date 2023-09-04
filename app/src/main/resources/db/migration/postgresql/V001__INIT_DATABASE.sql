-- Reader
CREATE SEQUENCE IF NOT EXISTS reader_seq START WITH 20 INCREMENT BY 1;

CREATE TABLE reader (
  id INTEGER NOT NULL,
   first_name VARCHAR(255),
   last_name VARCHAR(255),
   CONSTRAINT pk_reader PRIMARY KEY (id)
);

INSERT INTO reader (id, first_name, last_name) VALUES (1, 'Peter', 'Prvý');
INSERT INTO reader (id, first_name, last_name) VALUES (2, 'Lukáš', 'Druhý');
INSERT INTO reader (id, first_name, last_name) VALUES (3, 'Matej', 'Tretí');
INSERT INTO reader (id, first_name, last_name) VALUES (4, 'Jozef', 'Štvrtý');

-- Book
CREATE SEQUENCE IF NOT EXISTS book_seq START WITH 20 INCREMENT BY 1;

CREATE TABLE book (
  id INTEGER NOT NULL,
   name VARCHAR(255),
   author VARCHAR(255),
   CONSTRAINT pk_book PRIMARY KEY (id)
);

INSERT INTO book (id, name, author) VALUES (1, 'Starec a more', 'Ernest Hemingway');
INSERT INTO book (id, name, author) VALUES (2, 'Rómeo a Júlia', 'William Shakespeare');
INSERT INTO book (id, name, author) VALUES (3, 'Krvavé sonety', 'Pavol Országh Hviezdoslav');
INSERT INTO book (id, name, author) VALUES (4, 'Hájniková žena', 'Pavol Országh Hviezdoslav');
INSERT INTO book (id, name, author) VALUES (5, 'Hamlet', 'William Shakespeare');
INSERT INTO book (id, name, author) VALUES (6, 'Živý bič', 'Milo Urban');

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

INSERT INTO borrowed_book (id, borrowed_on, book_id, reader_id) VALUES (1, '2016-03-25', 1, 1);
INSERT INTO borrowed_book (id, borrowed_on, book_id, reader_id) VALUES (2, '2018-06-16', 2, 2);
INSERT INTO borrowed_book (id, borrowed_on, book_id, reader_id) VALUES (3, '2017-02-01', 3, 3);
INSERT INTO borrowed_book (id, borrowed_on, book_id, reader_id) VALUES (4, '2009-10-25', 5, 4);
