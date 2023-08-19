-- Reader
INSERT INTO reader (id, first_name, last_name) VALUES (1, 'Peter', 'First');
INSERT INTO reader (id, first_name, last_name) VALUES (2, 'Lucas', 'Second');
-- Book
INSERT INTO book (id, name, author) VALUES (1, 'The Old Man and the Sea', 'Ernest Hemingway');
INSERT INTO book (id, name, author) VALUES (2, 'Romeo and Juliet', 'William Shakespeare');
INSERT INTO book (id, name, author) VALUES (3, 'Hamlet', 'William Shakespeare');
-- Borrowed book
INSERT INTO borrowed_book (id, borrowed_on, book_id, reader_id) VALUES (1, '2016-03-25', 2, 1);
INSERT INTO borrowed_book (id, borrowed_on, book_id, reader_id) VALUES (2, '2018-06-16', 1, 2);