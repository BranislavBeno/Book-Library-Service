[![Application Tests](https://github.com/BranislavBeno/Book-Library-Service/actions/workflows/03-run-tests.yml/badge.svg)](https://github.com/BranislavBeno/Book-Library-Service/actions/workflows/03-run-tests.yml)
[![Application Deployment](https://github.com/BranislavBeno/Book-Library-Service/actions/workflows/04-build-and-deploy-application.yml/badge.svg)](https://github.com/BranislavBeno/Book-Library-Service/actions/workflows/04-build-and-deploy-application.yml)  
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=BranislavBeno_BookLibraryService&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=BranislavBeno_BookLibraryService)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=BranislavBeno_BookLibraryService&metric=coverage)](https://sonarcloud.io/summary/new_code?id=BranislavBeno_BookLibraryService)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=BranislavBeno_BookLibraryService&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=BranislavBeno_BookLibraryService)  
[![](https://img.shields.io/badge/Java-19-blue)](/app/build.gradle.kts)
[![](https://img.shields.io/badge/Spring%20Boot-3.0.5-blue)](/app/build.gradle.kts)
[![](https://img.shields.io/badge/Gradle-8.0.2-blue)](/gradle/wrapper/gradle-wrapper.properties)
[![](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

# Simple web application for book library service
This application allows to provide usual actions with book library, such as:
- view paginated all books (highlighted whether are borrowed or available)
- view paginated only available books
- view paginated only borrowed books
- add new book into library
- update book data
- remove book from library
- borrow and return book

> Application listens on port 8080.  
> It offers web UI and REST API interface as well.

### Configuration
Following application settings are configurable over application.properties file:
- book.repository.path - input file destination
- book.service.page.size - page size for paginated outputs
- book.authentication.user - username for authenticated access
- book.authentication.password - password for authenticated access

> Default credentials are:
> - username=user
> - password=passwd

### Web UI usage
After application start, click on http://localhost:8080/

After successful login, web UI offers three types of view:
1. _*All books*_. Borrowed are highlighted red, available green. This view allows book data updating or book removal from library.  
   Book data updating doesn't allow borrow or rent book.
2. _*Available books*_. This view allows to borrow book.
3. _*Borrowed books*_.  This view allows to return book.

Main page offers also add book into library.

> Due to alignment with REST API is login provided over web browser default dialog and not over own HTML login page.

### REST API usage
For sending requests and receiving responses use `Postman`, `curl` or web browser.

> All endpoints are secured with basic authentication (see [Configuration](#configuration))

#### API description
Following endpoints are available for usage:

- **GET /api/v1/books/all?page={pageNumber}** - returns required page from list of all books or empty list when no book was found.
- **GET /api/v1/books/available?page={pageNumber}** - returns required page from list of available books or empty list when no book was found.
- **GET /api/v1/books/borrowed?page={pageNumber}** - returns required page from list of borrowed books or empty list when no book was found.
- **POST /api/v1/books/add** - adds new book into library.  
> Input is validated. Book author can't be empty. Book name can't be empty or longer than 15 signs.

  Request body example for new book adding:
  ```json
  {
     "name": "My memories",
     "author": "John Doe"
  }
  ```
- **PUT /api/v1/books/update** - updates book data. Operation is refused when a book with given ID doesn't exist.  
> Input is validated. Book author can't be empty. Book name can't be empty or longer than 15 signs.

  Request body example for existing book updating:
  ```json
  {
     "id": 1,
     "name": "My memories",
     "author": "John Doe"
  }
  ```
- **DELETE /api/v1/books/delete?bookId={id}** - deletes book with given ID. Operation is refused when book with given ID doesn't exist.  
- **PUT /api/v1/books/avail?bookId={id}** - makes available book with given ID. Operation is refused when book with given ID doesn't exist.  
- **PUT /api/v1/books/borrow** - borrows the book. Operation is refused when a book with given ID doesn't exist.  
> Input is validated. Date of borrow can't be later than today.

  Request body example for book borrowing:
  ```json
  {
     "bookId": 1,
     "firstName": "John",
     "lastName": "Doe",
     "from": "2023-01-05"
  }
  ```
