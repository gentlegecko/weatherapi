# Weather API Application

## Overview

An application that allows the user to fetch the weather report of any city in the world. The weather report is summarized into a short description.

## Technologies Used
- Java 17
- Spring Boot
- H2 DB
- Mockito for testing
- OpenWeatherMap API

## Prerequisites
- JDK 17
- Maven

## Setup and Installation
1. Clone the repo
2. Navigate to project root
3. `mvn clean install`
4. `mvn spring-boot:run`

## Using the API
In your terminal, type in the following:
```bash
curl -H "apiKey: aa697814e2d93b7b986308dacc39a7b3" "http://localhost:8080/api/weather?city=London&country=uk"
```

## Testing
In your terminal, type in the following:
```bash
mvn test
```

## Configuration
All configurations including urls, rate limits, api keys, and h2 database are defined in the application.properties file.

## Future Improvements
* Adding swagger docs.
* More tests, especially integration and end-to-end testing.
