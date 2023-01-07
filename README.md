[![pipeline status](https://gitlab.com/brano.beno/library-for-nextit/badges/master/pipeline.svg)](https://gitlab.com/brano.beno/library-for-nextit/-/commits/master)
[![coverage report](https://gitlab.com/brano.beno/library-for-nextit/badges/master/coverage.svg)](https://gitlab.com/brano.beno/library-for-nextit/-/commits/master) 

# Simple web application for book library service
It is a web server and REST API server at once.  
This application allows to provide usual actions with book library, such as:
- view paginated all books (highlighted whether are borrowed or available)
- view paginated only available books
- view paginated only borrowed books
- add new book into library
- update book data
- remove book from library
- borrow and return book

> Application listens on port 8080

### REST API usage
For sending requests and receiving responses use `Postman`, `curl` or web browser.

> All endpoints are secured with basic authentication (see [Notes](#notes))

#### API description
Following endpoints are available for usage:

- **GET /api/v1/books/all?page={pageNumber}** - returns required page from list of all books or empty list when no book was found.
- **GET /api/v1/books/available?page={pageNumber}** - returns required page from list of available books or empty list when no book was found.
- **GET /api/v1/books/borrowed?page={pageNumber}** - returns required page from list of borrowed books or empty list when no book was found.
- **POST /api/v1/books/add** - adds new book into library. Input is validated. Book author can't be empty. Book name can't be empty or longer than 15 signs.
  Request body example for new book adding:
  ```json
  {
     "name": "My memories",
     "author": "John Doe"
  }
  ```
- **PUT /api/v1/books/update** - updates book data. Operation is refused when a book with given ID doesn't exist. Input is validated (see new book adding).  
  Request body example for existing book updating:
  ```json
  {
     "id": 1,
     "name": "My memories",
     "author": "John Doe"
  }
  ```
- **DELETE /api/v1/books/delete/{id}** - deletes book with given ID. Operation is refused when book with given ID doesn't exist.  
- **PUT /api/v1/books/avail/{id}** - makes available book with given ID. Operation is refused when book with given ID doesn't exist.  
- **PUT /api/v1/books/borrow** - borrows the book. Operation is refused when a book with given ID doesn't exist. Input is validated. Date of borrow can't be later than today.  
  Request body example for book borrowing:
  ```json
  {
     "bookId": 1,
     "firstName": "John",
     "lastName": "Doe",
     "from": "2023-01-05"
  }
  ```

### Web UI usage
After application start, click on http://localhost:8080/

> Due to alignment with REST API is login provided over web browser default dialog and not over own login page.

### Notes
Following application settings are configurable over application.properties file:
- book.repository.path - input file destination
- book.service.page.size - page size for paginated outputs
- book.authentication.user - username for authenticated access
- book.authentication.password - password for authenticated access