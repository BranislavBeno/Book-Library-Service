-- Reader
INSERT INTO reader (id, first_name, last_name, email) VALUES (1, 'Ján', 'Prvý', 'jan@b-l-s.click');
INSERT INTO reader (id, first_name, last_name, email) VALUES (2, 'Lukáš', 'Druhý', 'lukas@b-l-s.click');
INSERT INTO reader (id, first_name, last_name, email) VALUES (3, 'Matej', 'Tretí', 'matej@b-l-s.click');
INSERT INTO reader (id, first_name, last_name, email) VALUES (4, 'Jozef', 'Štvrtý', 'jozef@b-l-s.click');
INSERT INTO reader (id, first_name, last_name, email) VALUES (5, 'Peter', 'Piaty', 'peter@b-l-s.click');
INSERT INTO reader (id, first_name, last_name, email) VALUES (6, 'Pavol', 'Šiesty', 'pavol@b-l-s.click');
INSERT INTO reader (id, first_name, last_name, email) VALUES (7, 'Juraj', 'Siedmy', 'juraj@b-l-s.click');
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
--- Book recommendation request
INSERT INTO book_recommendation_request (id, token, book_id, recommenced_id) VALUES (1, 'token', 1, 2);
