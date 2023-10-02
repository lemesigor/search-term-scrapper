# axreng-test

REST API to scrape a website and return the number of times a word appears in it.

## Overview

This project uses the following technologies:

**Language and main dependencies**
- Java 14
- Spark Java
- Gson
- Maven
- Junit
- Mockito

### Running the Application
```sh
docker build . -t axreng/backend
docker run -e BASE_URL=http://hiring.axreng.com/ -p 4567:4567 --rm axreng/backend ## base URL is a must have
```

### Project structure


The project is structure is inspired by Hexagonal Architecture, where the main composition root is the `Main` class, which is responsible for creating the main objects and injecting the dependencies. The `Main` class is also responsible for starting the server.

```sh
# *******************************************
# **       application source code         **
# *******************************************

└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── axreng
    │   │           └── backend
    │   │               ├── Main.java               # Main composition root
    │   │               ├── application             # Application layer with the core business logic
    │   │               │   ├── domain              # Domain layer with the business entities
    │   │               │   ├── factory             # Factory classes to create objects
    │   │               │   └── usecases            # Business behaviour logic (services)
    │   │               ├── infrastructure          # Infrastructure layer with the external dependencies
    │   │               │   ├── cache            
    │   │               │   ├── database         
    │   │               │   ├── http             
    │   │               │   ├── parser           
    │   │               │   ├── storage
    │   │               │   └── threads
    │   │               ├── presentation            # Presentation layer with
    │   │               │   └── web                 # Web layer with the REST API
    │   │               │       └── controllers 
    │   │               │           ├── requests    # Translate domain-object to http-object (DTO)
    │   │               │           └── responses   # Translate domain-object to http-object (DTO)
    │   │               └── shared                  # Utility code shared across the application
    │   └── resources



# *******************************************
# **       application tests code          **
# *******************************************
        └── test-classes
                    ├── integration                 # Integration tests that will actually hit datasource, receive requests,etc    
                    │   └── presentation
                    │       └── web                 # Test the REST api laywer of the application
                    │           └── utils           # General utilities in order to test the REST api

                    └── unit                        # Unit tests that will test the business logic of the application and isolated modules
                        ├── application
                        ├── infrastructure          # Test infrastructure layer dependencies isolated
                        └── shared                  # Test shared code dependencies isolated



```
### Project Architecture and Code Design

The project is structured in layers based on [Hexagonal Architecture](https://netflixtechblog.com/ready-for-changes-with-hexagonal-architecture-b315ec967749) due it's
simplicity, testability and maintainability if any change is needed, decoupling the business logic from the external dependencies.

Also, the project relies on a few design patterns and engineering practices (SOLID, Clean Code, OOP core concepts) to make the code maintainable, testable and performant. Such as:


#### Dependency Inversion

The application relies heavily on dependency inversion and dependency injection in order to inject the dependencies into the classes. The dependency injection is done manually in the `Main` class, which is the main composition root of the application.
All The dependencies in the presentation layer are being injected manually as well

#### Factory

The application relies on factory design pattern to create objects. The factory classes are located in the `application/factory` package, by doing that it guarantees an abstraction by a aggregation injection point.

#### Singleton

A few dependencies are being created using the singleton pattern in order to keep the same instance of the object during the application lifecycle such as `infrastructure/database`, `infrastructure/threads`, etc.


### Business Rules

The summary of the business rules of the application which includes the core application layer and the web api layer are:
* Given a word between 4 and 32 characters, the application should be able to scrape a website and return the internal urls for the word.
* The application will look for the world only in the internal urls of the website including the times that the world appear as a substring.
* An environment variable `BASE_URL` **must** be set in order to run the application otherwise it will use a default URL.
* In order to scrape a website, a `POST` request must be made to `/crawl` with the following payload:
```json
{
    "keyword": "string"
}
```
* The website internal pages **must** follow the same domain as the base URL and have `.html` suffix, and it will scrape only internal.
* It's possible to retrieve the number of times a word appears in the website by making a `GET` request to `/crawl/{word_id}`. It will also inform if the scrapping is still in progress and the partial results.
* The application supports multiple scrapping at the same time, and it's possible to retrieve the status of each scrapping.
* All the information regarding the scrapping is stored in the application instance as long as it's still running.

### Infrastructure Layer
The infrastructure layer is responsible for the external dependencies of the application, such as database, cache, http, etc. It's main dependencies that are being injected in the application and that have a direct impact on the application are:

- `infrastructure/thread`: Responsible for creating threads to run the application in parallel. Every parsing of the website is done in a separate thread.
- `infrastructure/cache`:  Responsible for caching the website content so that the application doesn't need to fetch the website content every time a request is made. In this project, it's being used a simple in-memory cache to store the website content. It will keep alive until the application is finished.
- `infrastructure/database`: Responsible for the database connection and queries. In this project, it's being used a simple in-memory database to store the words and their results.