-- Reader
INSERT INTO reader (id, first_name, last_name) VALUES (1, 'Ján', 'Prvý');
INSERT INTO reader (id, first_name, last_name) VALUES (2, 'Lukáš', 'Druhý');
INSERT INTO reader (id, first_name, last_name) VALUES (3, 'Matej', 'Tretí');
INSERT INTO reader (id, first_name, last_name) VALUES (4, 'Jozef', 'Štvrtý');
INSERT INTO reader (id, first_name, last_name) VALUES (5, 'Peter', 'Piaty');
INSERT INTO reader (id, first_name, last_name) VALUES (6, 'Pavol', 'Šiesty');
INSERT INTO reader (id, first_name, last_name) VALUES (7, 'Juraj', 'Siedmy');
-- Book
INSERT INTO book (id, name, author) VALUES (1, 'Starec a more', 'Ernest Hemingway');
INSERT INTO book (id, name, author) VALUES (2, 'Rómeo a Júlia', 'William Shakespeare');
INSERT INTO book (id, name, author) VALUES (3, 'Krvavé sonety', 'Pavol Országh Hviezdoslav');
INSERT INTO book (id, name, author) VALUES (4, 'Hájniková žena', 'Pavol Országh Hviezdoslav');
INSERT INTO book (id, name, author) VALUES (5, 'Hamlet', 'William Shakespeare');
INSERT INTO book (id, name, author) VALUES (6, 'Živý bič', 'Milo Urban');
-- Borrowed book
INSERT INTO borrowed_book (id, borrowed_on, book_id, reader_id) VALUES (1, '2016-03-25', 1, 1);
INSERT INTO borrowed_book (id, borrowed_on, book_id, reader_id) VALUES (2, '2018-06-16', 2, 2);
INSERT INTO borrowed_book (id, borrowed_on, book_id, reader_id) VALUES (3, '2017-02-01', 3, 3);
INSERT INTO borrowed_book (id, borrowed_on, book_id, reader_id) VALUES (4, '2009-10-25', 5, 4);
